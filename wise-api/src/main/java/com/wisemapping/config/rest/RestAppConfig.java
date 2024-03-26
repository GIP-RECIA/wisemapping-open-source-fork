package com.wisemapping.config.rest;

import com.wisemapping.filter.JwtAuthenticationFilter;
import com.wisemapping.rest.MindmapController;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;


@SpringBootApplication(scanBasePackageClasses = {MindmapController.class, JwtAuthenticationFilter.class})
@EnableWebSecurity
public class RestAppConfig {

    @Value("${app.api.http-basic-enabled:false}")
    private boolean enableHttpBasic;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    SecurityFilterChain apiSecurityFilterChain(@NotNull final HttpSecurity http, @NotNull final MvcRequestMatcher.Builder mvc) throws Exception {
        http
                .securityMatcher("/**")
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(mvc.pattern("/error")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/authenticate")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/users/")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/app/config")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/maps/*/metadata")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/maps/*/document/xml-pub")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/users/resetPassword")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/oauth2/googlecallback")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/oauth2/confirmaccountsync")).permitAll()
                        .requestMatchers(mvc.pattern("/api/restful/admin/**")).hasAnyRole("ADMIN")
                        .requestMatchers(mvc.pattern("/**")).hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.permitAll()
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        }))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Http basic is mainly used by automation tests.
        if (enableHttpBasic) {
            http.httpBasic(withDefaults());
        }

        return http.build();
    }
}
