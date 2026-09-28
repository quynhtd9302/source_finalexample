package com.quynhtadinh.finalexample.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.quynhtadinh.finalexample.entity.User;

/** Handles Facebook sign-in (plain OAuth2, not OIDC) and syncs it to a local {@link User}. */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private OAuth2UserSyncService oAuth2UserSyncService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = (String) oauth2User.getAttributes().get("email");
        User appUser = oAuth2UserSyncService.syncUser(email, "FACEBOOK");
        Set<GrantedAuthority> authorities = oAuth2UserSyncService.mapAuthorities(appUser);

        // Facebook does not reliably return "email" (depends on user permission),
        // so the name-attribute key stays "id" — always present per the OAuth2 spec.
        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "id");
    }
}
