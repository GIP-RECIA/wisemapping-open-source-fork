package com.wisemapping.security.cas;

import com.wisemapping.service.ServiceUrlHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails springSecurityUser;
        String userName = null;

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }

        return userName;
    }

    /**
     * Get the User object of the current user.
     */
    public static UserDetails getCurrentUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                return (UserDetails) authentication.getPrincipal();
            }
        }

        return null;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        final Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();

        return authorities != null
                && !authorities.equals(AuthorityUtils.NO_AUTHORITIES)
                && !authorities.contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
    }

    public static String makeDynamicCASServiceUrl(ServiceUrlHelper urlHelper, HttpServletRequest request) {
        String urlBase = urlHelper.getRootAppUrl(request);
        for (String url : urlHelper.getAuthorizedDomainNames()) {
            if (urlBase.startsWith(url)) return urlBase;
        }
        throw new AccessDeniedException("The server is unable to authenticate from requested url " + urlBase);
    }

}
