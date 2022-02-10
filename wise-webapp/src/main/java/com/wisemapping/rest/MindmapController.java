/*
 *    Copyright [2015] [wisemapping]
 *
 *   Licensed under WiseMapping Public License, Version 1.0 (the "License").
 *   It is basically the Apache License, Version 2.0 (the "License") plus the
 *   "powered by wisemapping" text requirement on every single page;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the license at
 *
 *       http://www.wisemapping.org/license
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.wisemapping.rest;

import com.wisemapping.exceptions.LabelCouldNotFoundException;
import com.wisemapping.exceptions.MapCouldNotFoundException;
import com.wisemapping.exceptions.SessionExpiredException;
import com.wisemapping.exceptions.WiseMappingException;
import com.wisemapping.model.*;
import com.wisemapping.rest.model.*;
import com.wisemapping.security.Utils;
import com.wisemapping.service.*;
import com.wisemapping.validator.MapInfoValidator;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class MindmapController extends BaseController {
    final Logger logger = Logger.getLogger(MindmapController.class);

    private static final String LATEST_HISTORY_REVISION = "latest";

    @Qualifier("mindmapService")
    @Autowired
    private MindmapService mindmapService;

    @Qualifier("labelService")
    @Autowired
    private LabelService labelService;

    @RequestMapping(method = RequestMethod.GET, value = "/maps/{id}", produces = {"application/json", "application/xml", "text/html"})
    @ResponseBody
    public RestMindmap retrieve(@PathVariable int id) throws WiseMappingException {
        final User user = Utils.getUser();
        final Mindmap mindMap = findMindmapById(id);
        return new RestMindmap(mindMap, user);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/maps/", produces = {"application/json", "application/xml"})
    public RestMindmapList retrieveList(@RequestParam(required = false) String q) throws IOException {
        final User user = Utils.getUser();

        final MindmapFilter filter = MindmapFilter.parse(q);
        List<Mindmap> mindmaps = mindmapService.findMindmapsByUser(user);
        mindmaps = mindmaps
                .stream()
                .filter(m -> filter.accept(m, user))
                .collect(Collectors.toUnmodifiableList());

        return new RestMindmapList(mindmaps, user);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/maps/{id}/history/", produces = {"application/json", "application/xml"})
    public RestMindmapHistoryList fetchHistory(@PathVariable int id) {
        final List<MindMapHistory> histories = mindmapService.findMindmapHistory(id);
        final RestMindmapHistoryList result = new RestMindmapHistoryList();
        for (MindMapHistory history : histories) {
            result.addHistory(new RestMindmapHistory(history));
        }
        return result;
    }

    @RequestMapping(value = "/maps/{id}/history/{hid}", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateRevertMindmap(@PathVariable int id, @PathVariable String hid) throws WiseMappingException, IOException {
        final Mindmap mindmap = findMindmapById(id);
        final User user = Utils.getUser();

        if (LATEST_HISTORY_REVISION.equals(hid)) {
            // Revert to the latest stored version ...
            List<MindMapHistory> mindmapHistory = mindmapService.findMindmapHistory(id);
            if (mindmapHistory.size() > 0) {
                final MindMapHistory mindMapHistory = mindmapHistory.get(0);
                mindmap.setZippedXml(mindMapHistory.getZippedXml());
                saveMindmapDocument(true, mindmap, user);
            }
        } else {
            mindmapService.revertChange(mindmap, Integer.parseInt(hid));
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/maps/{id}/document", consumes = {"application/xml", "application/json"}, produces = {"application/json", "application/xml"})
    @ResponseBody
    public Long updateDocument(@RequestBody RestMindmap restMindmap, @PathVariable int id, @RequestParam(required = false) boolean minor, @RequestParam(required = false) Long timestamp, @RequestParam(required = false) Long session) throws WiseMappingException, IOException {

        final Mindmap mindmap = findMindmapById(id);
        final User user = Utils.getUser();

        // Validate arguments ...
        final String properties = restMindmap.getProperties();
        if (properties == null) {
            throw new IllegalArgumentException("Map properties can not be null");
        }

        // Could the map be updated ?
        if (session != null) {
            verifyLock(mindmap, user, session, timestamp);
        }

        // Update collaboration properties ...
        final CollaborationProperties collaborationProperties = mindmap.findCollaborationProperties(user);
        collaborationProperties.setMindmapProperties(properties);

        // Validate content ...
        String xml = restMindmap.getXml();
        if (xml == null) {
            throw new IllegalArgumentException("Map xml can not be null");
        }
        mindmap.setXmlStr(xml);

        // Update map ...
        saveMindmapDocument(minor, mindmap, user);

        // Update edition timeout ...
        final LockManager lockManager = mindmapService.getLockManager();
        long result = -1;
        if (session != null) {
            final LockInfo lockInfo = lockManager.updateExpirationTimeout(mindmap, user);
            result = lockInfo.getTimestamp();
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/maps/{id}/document/xml", "/maps/{id}/document/xml-pub"}, consumes = {"text/plain"}, produces = {"application/xml; charset=UTF-8"})
    @ResponseBody
    public byte[] retrieveDocument(@PathVariable int id, @NotNull HttpServletResponse response) throws WiseMappingException, IOException {
        final Mindmap mindmap = findMindmapById(id);

        String xmlStr = mindmap.getXmlStr();
        return xmlStr.getBytes(StandardCharsets.UTF_8);
    }

    @RequestMapping(method = RequestMethod.PUT, value = {"/maps/{id}/document/xml"}, consumes = {"text/plain"})
    @ResponseBody
    public void updateDocument(@PathVariable int id, @RequestBody String xmlDoc) throws WiseMappingException, IOException {

        final Mindmap mindmap = findMindmapById(id);
        final User user = Utils.getUser();
        if (xmlDoc != null && !xmlDoc.isEmpty()) {
            mindmap.setXmlStr(xmlDoc);
        }

        mindmap.setXmlStr(xmlDoc);
        saveMindmapDocument(false, mindmap, user);
    }


    @RequestMapping(method = RequestMethod.GET, value = {"/maps/{id}/{hid}/document/xml"}, consumes = {"text/plain"}, produces = {"application/xml; charset=UTF-8"})
    @ResponseBody
    public byte[] retrieveDocument(@PathVariable int id, @PathVariable int hid, @NotNull HttpServletResponse response) throws WiseMappingException, IOException {
        final MindMapHistory mindmapHistory = mindmapService.findMindmapHistory(id, hid);
        return mindmapHistory.getUnzipXml();
    }

    private void verifyLock(@NotNull Mindmap mindmap, @NotNull User user, long session, long timestamp) throws WiseMappingException {

        // The lock was lost, reclaim as the ownership of it.
        final LockManager lockManager = mindmapService.getLockManager();
        final boolean lockLost = lockManager.isLocked(mindmap);
        if (!lockLost) {
            lockManager.lock(mindmap, user, session);
        }

        final LockInfo lockInfo = lockManager.getLockInfo(mindmap);
        if (lockInfo.getUser().identityEquality(user)) {
            long savedTimestamp = mindmap.getLastModificationTime().getTimeInMillis();
            final boolean outdated = savedTimestamp > timestamp;

            if (lockInfo.getSession() == session) {
                // Timestamp might not be returned to the client. This try to cover this case, ignoring the client timestamp check.
                final User lastEditor = mindmap.getLastEditor();
                boolean editedBySameUser = lastEditor == null || user.identityEquality(lastEditor);
                if (outdated && !editedBySameUser) {
                    throw new SessionExpiredException("Map has been updated by " + (lastEditor.getEmail()) + ",Timestamp:" + timestamp + "," + savedTimestamp + ", User:" + lastEditor.getId() + ":" + user.getId() + ",Mail:'" + lastEditor.getEmail() + "':'" + user.getEmail(), lastEditor);
                }
            } else if (outdated) {
                logger.warn("Sessions:" + session + ":" + lockInfo.getSession() + ",Timestamp: " + timestamp + ": " + savedTimestamp);
                // @Todo: Temporally disabled to unblock save action. More research needed.
//                throw new MultipleSessionsOpenException("Sessions:" + session + ":" + lockInfo.getSession() + ",Timestamp: " + timestamp + ": " + savedTimestamp);
            }
        } else {
            throw new SessionExpiredException("Different Users.", lockInfo.getUser());
        }
    }

    /**
     * The intention of this method is the update of several properties at once ...
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/maps/{id}", consumes = {"application/xml", "application/json"}, produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateProperties(@RequestBody RestMindmap restMindmap, @PathVariable int id, @RequestParam(required = false) boolean minor) throws IOException, WiseMappingException {

        final Mindmap mindmap = findMindmapById(id);
        final User user = Utils.getUser();

        final String xml = restMindmap.getXml();
        if (xml != null && !xml.isEmpty()) {
            mindmap.setXmlStr(xml);
        }

        // Update title  ...
        final String title = restMindmap.getTitle();
        if (title != null && !title.equals(mindmap.getTitle())) {
            if (mindmapService.getMindmapByTitle(title, user) != null) {
                throw buildValidationException("title", "You already have a map with this title");
            }
            mindmap.setTitle(title);
        }

        // Update description ...
        final String description = restMindmap.getDescription();
        if (description != null) {
            mindmap.setDescription(description);
        }

        // Update document properties ...
        final String properties = restMindmap.getProperties();
        if (properties != null) {
            final CollaborationProperties collaborationProperties = mindmap.findCollaborationProperties(user);
            collaborationProperties.setMindmapProperties(properties);
        }

        // Update map ...
        saveMindmapDocument(minor, mindmap, user);
    }

    @NotNull
    private Mindmap findMindmapById(int id) throws MapCouldNotFoundException {
        Mindmap result = mindmapService.findMindmapById(id);
        if (result == null) {
            throw new MapCouldNotFoundException("Map could not be found. Id:" + id);
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/maps/{id}/title", consumes = {"text/plain"}, produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateTitle(@RequestBody String title, @PathVariable int id) throws WiseMappingException {

        final Mindmap mindMap = findMindmapById(id);
        final User user = Utils.getUser();

        // Is there a map with the same name ?
        if (mindmapService.getMindmapByTitle(title, user) != null) {

            throw buildValidationException("title", "You already have a mindmap with this title");
        }

        // Update map ...
        final Mindmap mindmap = findMindmapById(id);
        mindmap.setTitle(title);
        mindmapService.updateMindmap(mindMap, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/maps/{id}/collabs/", consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateCollabs(@PathVariable int id, @NotNull @RequestBody RestCollaborationList restCollabs) throws CollaborationException, MapCouldNotFoundException {
        final Mindmap mindMap = findMindmapById(id);

        // Only owner can change collaborators...
        final User user = Utils.getUser();
        if (!mindMap.hasPermissions(user, CollaborationRole.OWNER)) {
            throw new IllegalArgumentException("No enough permissions");
        }

        // Compare one by one if some of the elements has been changed ....
        final Set<Collaboration> collabsToRemove = new HashSet<>(mindMap.getCollaborations());
        for (RestCollaboration restCollab : restCollabs.getCollaborations()) {
            final Collaboration collaboration = mindMap.findCollaboration(restCollab.getEmail());
            // Validate role format ...
            String roleStr = restCollab.getRole();
            if (roleStr == null) {
                throw new IllegalArgumentException(roleStr + " is not a valid role");
            }

            // Remove from the list of pendings to remove ...
            if (collaboration != null) {
                collabsToRemove.remove(collaboration);
            }

            // Is owner ?
            final CollaborationRole role = CollaborationRole.valueOf(roleStr.toUpperCase());
            if (role != CollaborationRole.OWNER) {
                mindmapService.addCollaboration(mindMap, restCollab.getEmail(), role, restCollabs.getMessage());
            }

        }

        // Remove all collaborations that no applies anymore ..
        for (final Collaboration collaboration : collabsToRemove) {
            mindmapService.removeCollaboration(mindMap, collaboration);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/maps/{id}/collabs/", consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void addCollab(@PathVariable int id, @NotNull @RequestBody RestCollaborationList restCollabs) throws CollaborationException, MapCouldNotFoundException {
        final Mindmap mindMap = findMindmapById(id);

        // Only owner can change collaborators...
        final User user = Utils.getUser();
        if (!mindMap.hasPermissions(user, CollaborationRole.OWNER)) {
            throw new IllegalArgumentException("No enough permissions");
        }

        // Has any role changed ?. Just removed it.
        final Map<String, Collaboration> mapsByEmail = mindMap
                .getCollaborations()
                .stream()
                .collect(Collectors.toMap(collaboration -> collaboration.getCollaborator().getEmail(), collaboration -> collaboration));

        restCollabs
                .getCollaborations()
                .forEach(collab -> {
                    final String email = collab.getEmail();
                    if (mapsByEmail.containsKey(email)) {
                        try {
                            mindmapService.removeCollaboration(mindMap, mapsByEmail.get(email));
                        } catch (CollaborationException e) {
                            logger.error(e);
                        }
                    }
                });


        // Great, let's add all the collabs again ...
        for (RestCollaboration restCollab : restCollabs.getCollaborations()) {
            final Collaboration collaboration = mindMap.findCollaboration(restCollab.getEmail());
            // Validate role format ...
            String roleStr = restCollab.getRole();
            if (roleStr == null) {
                throw new IllegalArgumentException(roleStr + " is not a valid role");
            }

            // Is owner ?
            final CollaborationRole role = CollaborationRole.valueOf(roleStr.toUpperCase());
            if (role == CollaborationRole.OWNER) {
                throw new IllegalArgumentException("Owner can not be added as part of the collaboration list.");
            }

            mindmapService.addCollaboration(mindMap, restCollab.getEmail(), role, restCollabs.getMessage());
        }
    }


    @RequestMapping(method = RequestMethod.GET, value = "/maps/{id}/collabs", produces = {"application/json", "application/xml"})
    public RestCollaborationList retrieveList(@PathVariable int id) throws MapCouldNotFoundException {
        final Mindmap mindMap = findMindmapById(id);

        final Set<Collaboration> collaborations = mindMap.getCollaborations();
        final List<RestCollaboration> collabs = new ArrayList<>();
        for (Collaboration collaboration : collaborations) {
            collabs.add(new RestCollaboration(collaboration));
        }

        final RestCollaborationList result = new RestCollaborationList();
        result.setCollaborations(collabs);

        return result;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/maps/{id}/description", consumes = {"text/plain"}, produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateDescription(@RequestBody String description, @PathVariable int id) throws WiseMappingException {

        final Mindmap mindMap = findMindmapById(id);
        final User user = Utils.getUser();

        // Update map ...
        final Mindmap mindmap = findMindmapById(id);
        mindmap.setDescription(description);
        mindmapService.updateMindmap(mindMap, false);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/maps/{id}/publish", consumes = {"text/plain"}, produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePublishState(@RequestBody String value, @PathVariable int id) throws WiseMappingException {

        final Mindmap mindMap = findMindmapById(id);

        final User user = Utils.getUser();
        if (!mindMap.hasPermissions(user, CollaborationRole.OWNER)) {
            throw new IllegalArgumentException("No enough to execute this operation");
        }

        // Update map status ...
        mindMap.setPublic(Boolean.parseBoolean(value));
        mindmapService.updateMindmap(mindMap, false);

    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/maps/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteMapById(@PathVariable int id) throws IOException, WiseMappingException {
        final User user = Utils.getUser();
        final Mindmap mindmap = findMindmapById(id);
        mindmapService.removeMindmap(mindmap, user);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/maps/{id}/collabs")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCollabByEmail(@PathVariable int id, @RequestParam(required = false) String email) throws IOException, WiseMappingException {
        logger.debug("Deleting permission for email:" + email);

        final Mindmap mindmap = findMindmapById(id);
        final User user = Utils.getUser();

        // Only owner can change collaborators...
        if (!mindmap.hasPermissions(user, CollaborationRole.OWNER)) {
            throw new IllegalArgumentException("No enough permissions");
        }

        final Collaboration collab = mindmap.findCollaboration(email);
        if (collab != null) {
            CollaborationRole role = collab.getRole();

            // Owner collab can not be removed ...
            if (role == CollaborationRole.OWNER) {
                throw new IllegalArgumentException("Can not remove owner collab");
            }
            mindmapService.removeCollaboration(mindmap, collab);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/maps/{id}/starred", consumes = {"text/plain"}, produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateStarredState(@RequestBody String value, @PathVariable int id) throws WiseMappingException {

        logger.debug("Update starred:" + value);
        final Mindmap mindmap = findMindmapById(id);
        final User user = Utils.getUser();

        // Update map status ...
        final boolean starred = Boolean.parseBoolean(value);
        final Optional<Collaboration> collaboration = mindmap.findCollaboration(user);
        if (!collaboration.isPresent()) {
            throw new WiseMappingException("No enough permissions.");
        }
        collaboration.get().getCollaborationProperties().setStarred(starred);
        mindmapService.updateCollaboration(user, collaboration.get());
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/maps/{id}/lock", consumes = {"text/plain"}, produces = {"application/json", "application/xml"})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateMapLock(@RequestBody String value, @PathVariable int id) throws IOException, WiseMappingException {
        final User user = Utils.getUser();
        final LockManager lockManager = mindmapService.getLockManager();
        final Mindmap mindmap = findMindmapById(id);

        final boolean lock = Boolean.parseBoolean(value);
        if (!lock) {
            lockManager.unlock(mindmap, user);
        } else {
            throw new UnsupportedOperationException("REST lock must be implemented.");
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/maps/batch")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void batchDelete(@RequestParam() String ids) throws IOException, WiseMappingException {
        final User user = Utils.getUser();
        final String[] mapsIds = ids.split(",");
        for (final String mapId : mapsIds) {
            final Mindmap mindmap = findMindmapById(Integer.parseInt(mapId));
            mindmapService.removeMindmap(mindmap, user);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/maps", consumes = {"application/xml", "application/json"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createMap(@RequestBody(required = false) RestMindmap restMindmap, @NotNull HttpServletResponse response, @RequestParam(required = false) String title, @RequestParam(required = false) String description) throws IOException, WiseMappingException {
        // If a default maps has not been defined, just create one ...
        if (restMindmap == null) {
            restMindmap = new RestMindmap();
        }

        // Overwrite title and description if they where specified by parameter.
        if (title != null && !title.isEmpty()) {
            restMindmap.setTitle(title);
        }

        description  = description != null && !description.isEmpty() ? description : "";
        restMindmap.setDescription(description);

        // Validate ...
        final BindingResult result = new BeanPropertyBindingResult(restMindmap, "");
        new MapInfoValidator(mindmapService).validate(restMindmap.getDelegated(), result);
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        // If the user has not specified the xml content, add one ...
        final Mindmap delegated = restMindmap.getDelegated();
        String xml = restMindmap.getXml();
        if (xml == null || xml.isEmpty()) {
            xml = Mindmap.getDefaultMindmapXml(restMindmap.getTitle());
        }
        delegated.setXmlStr(xml);

        // Add new mindmap ...
        final User user = Utils.getUser();
        mindmapService.addMindmap(delegated, user);

        // Return the new created map ...
        response.setHeader("Location", "/service/maps/" + delegated.getId());
        response.setHeader("ResourceId", Integer.toString(delegated.getId()));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/maps/{id}", consumes = {"application/xml", "application/json"}, produces = {"application/xml", "application/json", "text/plain"})
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createDuplicate(@RequestBody RestMindmapInfo restMindmap, @PathVariable int id, @NotNull HttpServletResponse response) throws IOException, WiseMappingException {
        // Validate ...
        final BindingResult result = new BeanPropertyBindingResult(restMindmap, "");
        new MapInfoValidator(mindmapService).validate(restMindmap.getDelegated(), result);
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        // Some basic validations ...
        final User user = Utils.getUser();

        // Create a shallowCopy of the map ...
        final Mindmap mindMap = findMindmapById(id);
        final Mindmap clonedMap = mindMap.shallowClone();
        clonedMap.setTitle(restMindmap.getTitle());
        clonedMap.setDescription(restMindmap.getDescription());

        // Add new mindmap ...
        mindmapService.addMindmap(clonedMap, user);

        // Return the new created map ...
        response.setHeader("Location", "/service/maps/" + clonedMap.getId());
        response.setHeader("ResourceId", Integer.toString(clonedMap.getId()));
    }

    private void saveMindmapDocument(boolean minor, @NotNull final Mindmap mindMap, @NotNull final User user) throws WiseMappingException {
        final Calendar now = Calendar.getInstance();
        mindMap.setLastModificationTime(now);
        mindMap.setLastEditor(user);
        mindmapService.updateMindmap(mindMap, !minor);
    }

    private ValidationException buildValidationException(@NotNull String fieldName, @NotNull String message) throws WiseMappingException {
        final BindingResult result = new BeanPropertyBindingResult(new RestMindmap(), "");
        result.rejectValue(fieldName, "error.not-specified", null, message);
        return new ValidationException(result);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/maps/{id}/labels/{lid}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeLabelFromMap(@PathVariable int id, @PathVariable int lid) throws WiseMappingException {
        final User user = Utils.getUser();
        final Mindmap mindmap = findMindmapById(id);
        final Label label = labelService.findLabelById(lid, user);

        if (label == null) {
            throw new LabelCouldNotFoundException("Label could not be found. Id: " + lid);
        }

        mindmap.removeLabel(label);
        mindmapService.updateMindmap(mindmap, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/maps/{id}/labels", consumes = {"application/xml", "application/json"})
    @ResponseStatus(value = HttpStatus.OK)
    public void updateLabel(@PathVariable int id, @RequestBody int lid) throws WiseMappingException {
        final User user = Utils.getUser();
        final Label label = labelService.findLabelById(lid, user);
        if (label == null) {
            throw new LabelCouldNotFoundException("Label could not be found. Id: " + lid);
        }

        final Mindmap mindmap = findMindmapById(id);
        mindmap.addLabel(label);
        mindmapService.updateMindmap(mindmap, false);
    }
}
