package com.wisemapping.security.cas;

import com.wisemapping.exceptions.WiseMappingException;
import com.wisemapping.model.AuthenticationType;
import com.wisemapping.model.User;
import com.wisemapping.service.UserService;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Calendar;

public class CasUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        String casUid = token.getPrincipal().toString();
        User user = userService.getUserBy(casUid);
        if (user == null) user = createUser(casUid);

        return new com.wisemapping.security.UserDetails(user, false);
    }

    private User createUser(String casUid) {
        User user = new User();
        user.setEmail(casUid);
        user.setFirstname("");
        user.setLastname("");
        user.setPassword("");
        user.setActivationDate(Calendar.getInstance());

        try {
            user.setAuthenticationType(AuthenticationType.CAS);
            user = userService.createUser(user, false, false);
        } catch (WiseMappingException e) {
            throw new IllegalStateException(e);
        }

        return user;
    }

}
