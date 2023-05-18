package com.gerp.kasamu.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
//@EnableResourceServer
//@Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    private static final String ROOT_PATTERN = "/**";

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver()     {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()
//                .antMatchers(ROOT_PATTERN).permitAll()
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
//                .antMatchers(HttpMethod.GET, ROOT_PATTERN).hasAnyAuthority("SCOPE_profile")
//                .antMatchers(HttpMethod.POST, ROOT_PATTERN).hasAnyAuthority("SCOPE_profile")
//                .antMatchers(HttpMethod.PATCH, ROOT_PATTERN).hasAnyAuthority("SCOPE_profile")
//                .antMatchers(HttpMethod.PUT, ROOT_PATTERN).hasAnyAuthority("SCOPE_profile")
//                .antMatchers(HttpMethod.DELETE, ROOT_PATTERN).hasAnyAuthority("SCOPE_profile")
                .antMatchers(HttpMethod.GET, ROOT_PATTERN).access("@customPermissionEvaluator.hasTokenScope('profile')")
                .antMatchers(HttpMethod.POST, ROOT_PATTERN).access("@customPermissionEvaluator.hasTokenScope('profile')")
                .antMatchers(HttpMethod.PATCH, ROOT_PATTERN).access("@customPermissionEvaluator.hasTokenScope('profile')")
                .antMatchers(HttpMethod.PUT, ROOT_PATTERN).access("@customPermissionEvaluator.hasTokenScope('profile')")
                .antMatchers(HttpMethod.DELETE, ROOT_PATTERN).access("@customPermissionEvaluator.hasTokenScope('profile')")
                .anyRequest().authenticated()
        ;

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }
}
