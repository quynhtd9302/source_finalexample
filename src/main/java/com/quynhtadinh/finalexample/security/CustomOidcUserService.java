package com.quynhtadinh.finalexample.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.quynhtadinh.finalexample.entity.User;

/** Handles Google sign-in (an OIDC provider) and syncs it to a local {@link User}. */
@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private OAuth2UserSyncService oAuth2UserSyncService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        User appUser = oAuth2UserSyncService.syncUser(oidcUser.getEmail(), "GOOGLE");
        Set<GrantedAuthority> authorities = oAuth2UserSyncService.mapAuthorities(appUser);

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
    }
}
