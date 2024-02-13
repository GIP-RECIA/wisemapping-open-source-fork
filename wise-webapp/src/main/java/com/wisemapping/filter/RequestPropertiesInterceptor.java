/*
*    Copyright [2022] [wisemapping]
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

package com.wisemapping.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

public class RequestPropertiesInterceptor implements HandlerInterceptor {
    @Value("${google.analytics.enabled}")
    private Boolean analyticsEnabled;

    @Value("${google.analytics.account}")
    private String analyticsAccount;

    @Value("${google.recaptcha2.enabled}")
    private Boolean recaptcha2Enabled;

    @Value("${site.static.js.url}")
    private String siteStaticUrl;

    @Value("${google.recaptcha2.siteKey}")
    private String recaptcha2SiteKey;

    @Value("${google.ads.enabled}")
    private Boolean adsEnabled;

    @Value("${site.homepage}")
    private String siteHomepage;

    @Value("${site.baseurl}")
    private String siteUrl;

    @Value("${site.baseurl}")
    private String baseUrl;

    @Value("${security.type}")
    private String securityType;

	@Value("${security.oauth2.google.url}")
    private String googleOauth2Url;

    @Value("${extendedUportalHeader.componentPath}")
    private String extendedUportalHeaderComponentPath;
    @Value("${extendedUportalHeader.serviceName}")
    private String extendedUportalHeaderServiceName;
    @Value("${extendedUportalHeader.contextApiUrl}")
    private String extendedUportalHeaderContextApiUrl;
    @Value("${extendedUportalHeader.signOutUrl}")
    private String extendedUportalHeaderSignOutUrl;
    @Value("${extendedUportalHeader.defaultOrgLogoPath}")
    private String extendedUportalHeaderDefaultOrgLogoPath;
    @Value("${extendedUportalHeader.defaultAvatarPath}")
    private String extendedUportalHeaderDefaultAvatarPath;
    @Value("${extendedUportalHeader.defaultOrgIconPath}")
    private String extendedUportalHeaderDefaultOrgIconPath;
    @Value("${extendedUportalHeader.favoriteApiUrl}")
    private String extendedUportalHeaderFavoriteApiUrl;
    @Value("${extendedUportalHeader.layoutApiUrl}")
    private String extendedUportalHeaderLayoutApiUrl;
    @Value("${extendedUportalHeader.organizationApiUrl}")
    private String extendedUportalHeaderOrganizationApiUrl;
    @Value("${extendedUportalHeader.portletApiUrl}")
    private String extendedUportalHeaderPortletApiUrl;
    @Value("${extendedUportalHeader.userInfoApiUrl}")
    private String extendedUportalHeaderUserInfoApiUrl;
    @Value("${extendedUportalHeader.userInfoPortletUrl}")
    private String extendedUportalHeaderUserInfoPortletUrl;
    @Value("${extendedUportalHeader.sessionApiUrl}")
    private String extendedUportalHeaderSessionApiUrl;
    @Value("${extendedUportalHeader.templateApiPath}")
    private String extendedUportalHeaderTemplateApiPath;
    @Value("${extendedUportalHeader.switchOrgPortletUrl}")
    private String extendedUportalHeaderSwitchOrgPortletUrl;
    @Value("${extendedUportalHeader.favoritesPortletCardSize}")
    private String extendedUportalHeaderFavoritesPortletCardSize;
    @Value("${extendedUportalHeader.gridPortletCardSize}")
    private String extendedUportalHeaderGridPortletCardSize;
    @Value("${extendedUportalHeader.hideActionMode}")
    private String extendedUportalHeaderHideActionMode;
    @Value("${extendedUportalHeader.showFavoritesInSlider}")
    private String extendedUportalHeaderShowFavoritesInSlider;
    @Value("${extendedUportalHeader.returnHomeTitle}")
    private String extendedUportalHeaderReturnHomeTitle;
    @Value("${extendedUportalHeader.returnHomeTarget}")
    private String extendedUportalHeaderReturnHomeTarget;
    @Value("${extendedUportalHeader.iconType}")
    private String extendedUportalHeaderIconType;


    @Value("${extendedUportalFooter.componentPath}")
    private String extendedUportalFooterComponentPath;
    @Value("${extendedUportalFooter.templateApiPath}")
    private String extendedUportalFooterTemplateApiPath;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, Object object) throws Exception {

        request.setAttribute("google.analytics.enabled", analyticsEnabled);
        request.setAttribute("google.analytics.account", analyticsAccount);
        request.setAttribute("google.ads.enabled", adsEnabled);

        request.setAttribute("google.recaptcha2.enabled", recaptcha2Enabled);
        request.setAttribute("google.recaptcha2.siteKey", recaptcha2SiteKey);

        request.setAttribute("security.oauth2.google.url", googleOauth2Url);

        request.setAttribute("site.homepage", siteHomepage);
        request.setAttribute("site.static.js.url", siteStaticUrl);

        request.setAttribute("security.type", securityType);

        request.setAttribute("extendedUportalHeader.componentPath", extendedUportalHeaderComponentPath);
        request.setAttribute("extendedUportalHeader.serviceName", extendedUportalHeaderServiceName);
        request.setAttribute("extendedUportalHeader.contextApiUrl", extendedUportalHeaderContextApiUrl);
        request.setAttribute("extendedUportalHeader.signOutUrl", extendedUportalHeaderSignOutUrl);
        request.setAttribute("extendedUportalHeader.defaultOrgLogoPath", extendedUportalHeaderDefaultOrgLogoPath);
        request.setAttribute("extendedUportalHeader.defaultAvatarPath", extendedUportalHeaderDefaultAvatarPath);
        request.setAttribute("extendedUportalHeader.defaultOrgIconPath", extendedUportalHeaderDefaultOrgIconPath);
        request.setAttribute("extendedUportalHeader.favoriteApiUrl", extendedUportalHeaderFavoriteApiUrl);
        request.setAttribute("extendedUportalHeader.layoutApiUrl", extendedUportalHeaderLayoutApiUrl);
        request.setAttribute("extendedUportalHeader.organizationApiUrl", extendedUportalHeaderOrganizationApiUrl);
        request.setAttribute("extendedUportalHeader.portletApiUrl", extendedUportalHeaderPortletApiUrl);
        request.setAttribute("extendedUportalHeader.userInfoApiUrl", extendedUportalHeaderUserInfoApiUrl);
        request.setAttribute("extendedUportalHeader.userInfoPortletUrl", extendedUportalHeaderUserInfoPortletUrl);
        request.setAttribute("extendedUportalHeader.sessionApiUrl", extendedUportalHeaderSessionApiUrl);
        request.setAttribute("extendedUportalHeader.templateApiPath", extendedUportalHeaderTemplateApiPath);
        request.setAttribute("extendedUportalHeader.switchOrgPortletUrl", extendedUportalHeaderSwitchOrgPortletUrl);
        request.setAttribute("extendedUportalHeader.favoritesPortletCardSize", extendedUportalHeaderFavoritesPortletCardSize);
        request.setAttribute("extendedUportalHeader.gridPortletCardSize", extendedUportalHeaderGridPortletCardSize);
        request.setAttribute("extendedUportalHeader.hideActionMode", extendedUportalHeaderHideActionMode);
        request.setAttribute("extendedUportalHeader.showFavoritesInSlider", extendedUportalHeaderShowFavoritesInSlider);
        request.setAttribute("extendedUportalHeader.returnHomeTitle", extendedUportalHeaderReturnHomeTitle);
        request.setAttribute("extendedUportalHeader.returnHomeTarget", extendedUportalHeaderReturnHomeTarget);
        request.setAttribute("extendedUportalHeader.iconType", extendedUportalHeaderIconType);

        request.setAttribute("extendedUportalFooter.componentPath", extendedUportalFooterComponentPath);
        request.setAttribute("extendedUportalFooter.templateApiPath", extendedUportalFooterTemplateApiPath);

        // If the property could not be resolved, try to infer one from the request...
        // Keep baseUrl in memory so it could be forced from the config
        if ("${site.baseurl}".equals(baseUrl)) {
            siteUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
        }
        request.setAttribute("site.baseurl", siteUrl);
        request.setAttribute("site.domain", new URI(request.getRequestURL().toString()).getHost());
        return true;
    }
}
