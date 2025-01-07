package com.greem4.springmedicines.service;

import com.greem4.springmedicines.domain.Role;
import com.greem4.springmedicines.domain.User;
import com.greem4.springmedicines.repository.UserRepository;
import com.greem4.springmedicines.dto.ChangePasswordRequest;
import com.greem4.springmedicines.dto.RegisterRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.exception.*;
import com.greem4.springmedicines.mapper.UserResponseMap;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Transactional
    public UserResponse createUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        var user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setProvider("local");
        user.setProviderId(null);

        var saveUser = userRepository.save(user);
        return UserResponseMap.toUserResponse(saveUser);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponseMap::toUserResponse);
    }

    public UserResponse getUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));
        return UserResponseMap.toUserResponse(user);
    }

    @Transactional
    public void changePassword(String username, @Valid ChangePasswordRequest request) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Пароль успешно изменён для пользователя: {}", username);
    }

    public void saveOAuthUser(String login, String provider, String providerId) {
        var user = new User();
        user.setUsername(login);
        user.setPassword(passwordEncoder.encode("${//dd//pass}"));
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setRole(Role.USER);
        user.setEnabled(true);

        userRepository.save(user);
    }
}
