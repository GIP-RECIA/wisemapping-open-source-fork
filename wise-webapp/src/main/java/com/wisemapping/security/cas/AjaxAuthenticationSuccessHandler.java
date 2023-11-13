package com.wisemapping.security.cas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Component
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private static final Logger logger = LogManager.getLogger();

  public AjaxAuthenticationSuccessHandler() {
    setDefaultTargetUrl("/");
    setTargetUrlParameter("spring-security-redirect");
  }

  @Override
  protected void handle(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException {
    String targetUrl = this.determineTargetUrl(request, response);
    if (response.isCommitted())
      logger.debug("Response has already been committed. Unable to redirect to {}", targetUrl);
    else response.sendRedirect(targetUrl);
  }

  @Override
  public void onAuthenticationSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, ServletException {
    this.handle(request, response, authentication);
    this.clearAuthenticationAttributes(request);
  }

}
