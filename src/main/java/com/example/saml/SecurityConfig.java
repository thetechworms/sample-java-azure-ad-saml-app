package com.example.saml;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${saml2.azure.entity-id}")
    private String entityId;

    @Value("${saml2.azure.metadata-uri}")
    private String metadataUri;

    /**
     * Registers this app as a SAML2 Service Provider (SP).
     * Azure AD metadata is loaded automatically from the federation metadata URL.
     */
    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        RelyingPartyRegistration registration = RelyingPartyRegistrations
                .fromMetadataLocation(metadataUri)
                .registrationId("azure")
                .entityId(entityId)
                // ACS URL: Azure AD will POST the SAML response here after login
                .assertionConsumerServiceLocation("{baseUrl}/login/saml2/sso/{registrationId}")
                .build();

        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }

    /**
     * Secures all endpoints and enables SAML2 login.
     * Unauthenticated users are redirected to Azure AD for login.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .saml2Login(saml2 -> saml2
                        .defaultSuccessUrl("/profile", true))
                .saml2Logout(saml2 -> {
                }); // Enable SAML single logout

        return http.build();
    }
}
