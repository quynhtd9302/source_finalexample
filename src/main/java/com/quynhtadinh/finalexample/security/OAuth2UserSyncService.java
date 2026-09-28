package com.quynhtadinh.finalexample.security;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quynhtadinh.finalexample.entity.Role;
import com.quynhtadinh.finalexample.entity.User;
import com.quynhtadinh.finalexample.repository.RoleRepository;
import com.quynhtadinh.finalexample.repository.UserRepository;

/**
 * Creates or reuses a local {@link User} row for someone who just signed in
 * through Google/Facebook, keyed by their email. Shared by
 * {@link CustomOidcUserService} (Google) and {@link CustomOAuth2UserService}
 * (Facebook) so both providers end up with the same account/role model as
 * users who registered the normal way.
 */
@Service
public class OAuth2UserSyncService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User syncUser(String email, String provider) {
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_found"),
                    "Tài khoản " + provider + " của bạn chưa xác thực email, vui lòng dùng phương thức đăng nhập khác.");
        }

        User user = userRepository.findByUsername(email);
        if (user != null) {
            return user;
        }

        user = new User();
        user.setUsername(email);
        user.setEmail(email);
        // OAuth-only accounts never log in with a password, but Spring Security's
        // UserDetails wrapper requires a non-null value, so store an unusable one.
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setProvider(provider);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole != null) {
            roles.add(userRole);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public Set<GrantedAuthority> mapAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }
}
