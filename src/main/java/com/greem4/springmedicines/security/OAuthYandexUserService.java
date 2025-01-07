package com.greem4.springmedicines.security;

import com.greem4.springmedicines.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class OAuthYandexUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        log.debug("Attributes from Yandex: {}", oAuth2User.getAttributes());

        String provider = "yandex";
        String providerId = oAuth2User.getAttribute("id");
        String email = oAuth2User.getAttribute("default_email");
        if (email == null) {
            email = oAuth2User.getAttribute("email");
        }
        if (providerId == null) {
            throw new OAuth2AuthenticationException("Yandex did not return 'id'");
        }
        if (email == null) {
            throw new OAuth2AuthenticationException("No email from Yandex. Check scope or user settings.");
        }

        log.debug("Trying to find user in DB with provider={}, providerId={}", provider, providerId);
        var userOptional = userService.findByProviderAndProviderId(provider, providerId);

        if (userOptional.isEmpty()) {
            log.debug("User not found. Creating new user with email={}", email);
            userService.saveOAuthUser(email, provider, providerId);
        }

        var userInDb = userService.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new OAuth2AuthenticationException("User not found after creation?"));

        String springRole = "ROLE_" + userInDb.getRole();

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority(springRole)),
                oAuth2User.getAttributes(),
                "default_email"
        );
    }
}
