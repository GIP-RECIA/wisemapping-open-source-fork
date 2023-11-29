package com.wisemapping.webmvc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class HealthCheckController {

  private static final Logger logger = LogManager.getLogger();

  @GetMapping(value = "health-check")
  @ResponseStatus(HttpStatus.OK)
  public void healthCheck(HttpServletRequest request, HttpServletResponse response) {
    if (logger.isDebugEnabled()) logger.debug("Health check. HTTP 200: OK.");
  }

}
