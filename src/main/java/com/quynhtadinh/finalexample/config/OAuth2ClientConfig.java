package com.quynhtadinh.finalexample.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

/**
 * Builds Google/Facebook client registrations by hand instead of relying on
 * Spring Boot's "spring.security.oauth2.client.registration.*" auto-config,
 * which eagerly validates every configured registration (including a blank
 * client-id) at startup and would crash the app whenever social login
 * credentials haven't been set yet. Here a provider is only ever added to
 * the repository when both id and secret are present.
 */
@Configuration
public class OAuth2ClientConfig {

    @Autowired
    private SocialLoginProperties socialLoginProperties;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();

        if (socialLoginProperties.isGoogleEnabled()) {
            registrations.add(CommonOAuth2Provider.GOOGLE.getBuilder("google")
                    .clientId(socialLoginProperties.getGoogleClientId())
                    .clientSecret(socialLoginProperties.getGoogleClientSecret())
                    .scope("openid", "profile", "email")
                    .build());
        }

        if (socialLoginProperties.isFacebookEnabled()) {
            registrations.add(CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                    .clientId(socialLoginProperties.getFacebookClientId())
                    .clientSecret(socialLoginProperties.getFacebookClientSecret())
                    .scope("email", "public_profile")
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email")
                    .userNameAttributeName("id")
                    .build());
        }

        if (registrations.isEmpty()) {
            // No social login configured — a no-op repository so the app still
            // starts and /oauth2/authorization/** simply won't resolve anything.
            return registrationId -> null;
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }
}
