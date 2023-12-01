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
package com.wisemapping.security.cas;

import com.wisemapping.service.ServiceUrlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

public class RememberWebAuthenticationDetailsSource
        implements AuthenticationDetailsSource<HttpServletRequest, ServiceAuthenticationDetails> {

    private static final Logger logger = LogManager.getLogger();

    private final ServiceUrlHelper urlHelper;
    private final ServiceProperties serviceProperties;
    private final String casTargetUrlParam;

    public RememberWebAuthenticationDetailsSource(
            @NotNull ServiceUrlHelper urlHelper, @NotNull ServiceProperties serviceProperties, @NotNull String casTargetUrlParam
    ) {
        this.urlHelper = urlHelper;
        this.serviceProperties = serviceProperties;
        this.casTargetUrlParam = casTargetUrlParam;
    }

    public RememberWebAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new RememberWebAuthenticationDetails(request, urlHelper, serviceProperties, casTargetUrlParam);
    }

}
