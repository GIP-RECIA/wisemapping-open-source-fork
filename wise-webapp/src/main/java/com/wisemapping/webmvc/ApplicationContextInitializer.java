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
package com.wisemapping.webmvc;


import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.ServletContextResource;

import java.io.IOException;
import java.util.Objects;

public class ApplicationContextInitializer implements org.springframework.context.ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    public void initialize(@NotNull ConfigurableWebApplicationContext ctx) {
        try {
            final Resource ressourceDefaultConfig = new ServletContextResource(Objects.requireNonNull(ctx.getServletContext()), "/WEB-INF/app.properties");
            final ResourcePropertySource resourcePropertySourceDefaultConfig = new ResourcePropertySource(ressourceDefaultConfig);
            ctx.getEnvironment().getPropertySources().addFirst(resourcePropertySourceDefaultConfig);
            // Override default config with external config if present
            if(ctx.getEnvironment().getProperty("PROJECT_HOME") != null){
                final Resource resourceExternalConfig = new FileSystemResource("/"+ctx.getEnvironment().getProperty("PROJECT_HOME")+"/wisemapping/wisemapping.properties");
                final ResourcePropertySource resourcePropertySourceExternalConfig = new ResourcePropertySource(resourceExternalConfig);
                ctx.getEnvironment().getPropertySources().addFirst(resourcePropertySourceExternalConfig);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}