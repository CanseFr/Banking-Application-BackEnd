package com.canse.banking.config;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS CONFIGURATION
        http.cors().configurationSource(httpServletRequest -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.applyPermitDefaultValues();
                    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH"));
                    corsConfiguration.setAllowedHeaders(
                            Arrays.asList("X-Requested-With", "Origin", "Content-Type","Accept", "Authorization","Access-Control-Allow-Origin"));
                    return corsConfiguration;
                }).and()
        // URL CONFIGURATION
            .csrf().disable()
            .authorizeRequests(
                    (request)->
                    {
                        try {
                            request.antMatchers("/**/authenticate",
                                                            "/**/register",
                                                            "/api/access/**",
                                                            "/h2-console/**",
                                                            // resources for swagger to work properly
                                                            "/v2/api-docs",
                                                            "/v3/api-docs",
                                                            "/v3/api-docs/**",
                                                            "/swagger-resources",
                                                            "/swagger-resources/**",
                                                            "/configuration/ui",
                                                            "/configuration/security",
                                                            "/swagger-ui/**",
                                                            "/webjars/**",
                                                            "/swagger-ui.html"
                            )
                            .permitAll()
                                .anyRequest()
                                .authenticated()
                                    .and()
                                    .sessionManagement()
                                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            //.cors()
        ;

        return http.build();
    }

    //@Bean
//    public CorsFilter corsFilter(){
//        return null;
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());


        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new  BCryptPasswordEncoder();
    }

}
