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
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jgribonvald on 31/05/16.
 */
public class CustomSessionFixationProtectionStrategy extends SessionFixationProtectionStrategy {

    private final ServiceUrlHelper serviceUrlHelper;
    private final ServiceProperties serviceProperties;
    private final String casTargetUrlParam;

    public CustomSessionFixationProtectionStrategy(
            ServiceUrlHelper serviceUrlHelper, ServiceProperties serviceProperties, String casTargetUrlParam
    ) {
        this.serviceUrlHelper = serviceUrlHelper;
        this.serviceProperties = serviceProperties;
        this.casTargetUrlParam = casTargetUrlParam;
    }

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        super.onAuthentication(authentication, request, response);
        ((AbstractAuthenticationToken) authentication).setDetails(
                new RememberWebAuthenticationDetails(request, serviceUrlHelper, serviceProperties, casTargetUrlParam)
        );
    }

}
