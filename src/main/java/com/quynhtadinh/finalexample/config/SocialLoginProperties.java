package com.quynhtadinh.finalexample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Reads Google/Facebook OAuth2 app credentials from env vars (via a custom
 * "oauth2.*" namespace rather than Spring Boot's own
 * "spring.security.oauth2.client.registration.*"), so that leaving them
 * unset does not fail application startup — Spring Boot's own property
 * binding builds (and validates) a ClientRegistration eagerly even for
 * blank client-id/secret. Providers are only registered when both values
 * are actually present (see OAuth2ClientConfig).
 */
@Component
public class SocialLoginProperties {

    @Value("${oauth2.google.client-id:}")
    private String googleClientId;

    @Value("${oauth2.google.client-secret:}")
    private String googleClientSecret;

    @Value("${oauth2.facebook.client-id:}")
    private String facebookClientId;

    @Value("${oauth2.facebook.client-secret:}")
    private String facebookClientSecret;

    public String getGoogleClientId() {
        return googleClientId;
    }

    public String getGoogleClientSecret() {
        return googleClientSecret;
    }

    public String getFacebookClientId() {
        return facebookClientId;
    }

    public String getFacebookClientSecret() {
        return facebookClientSecret;
    }

    public boolean isGoogleEnabled() {
        return StringUtils.hasText(googleClientId) && StringUtils.hasText(googleClientSecret);
    }

    public boolean isFacebookEnabled() {
        return StringUtils.hasText(facebookClientId) && StringUtils.hasText(facebookClientSecret);
    }

    public boolean isAnyEnabled() {
        return isGoogleEnabled() || isFacebookEnabled();
    }
}
