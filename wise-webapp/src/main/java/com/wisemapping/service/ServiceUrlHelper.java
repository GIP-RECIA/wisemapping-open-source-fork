/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wisemapping.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
public class ServiceUrlHelper {

  private String contextPath;

  private List<String> authorizedDomainNames;

  private String protocol = "https://";

  private String itemUri;

  public ServiceUrlHelper(String contextPath, List<String> authorizedDomainNames, String protocol, String itemUri) {
    this.contextPath = contextPath;
    this.authorizedDomainNames = authorizedDomainNames;
    this.protocol = protocol;
    this.itemUri = itemUri;
  }

  public String getContextPath() {
    return contextPath;
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  public List<String> getAuthorizedDomainNames() {
    return authorizedDomainNames;
  }

  public void setAuthorizedDomainNames(List<String> authorizedDomainNames) {
    this.authorizedDomainNames = authorizedDomainNames;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getItemUri() {
    return itemUri;
  }

  public void setItemUri(String itemUri) {
    this.itemUri = itemUri;
  }

  /**
   * Used for conf display only
   */
  private String getItemUrl() {
    return protocol + authorizedDomainNames.get(0) + contextPath + itemUri + "ID";
  }

  public String getRootAppUrl(final HttpServletRequest request) {
    final String contextPath = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";

    return getRootDomainUrl(request) + contextPath;
  }

  public String getRootDomainUrl(final HttpServletRequest request) {
    final String url = request.getRequestURL().toString();
    final String uri = request.getRequestURI();

    return url.substring(0, url.length() - uri.length());
  }

}
