package com.greem4.springmedicines.service;

import com.greem4.springmedicines.database.entity.Role;
import com.greem4.springmedicines.database.entity.User;
import com.greem4.springmedicines.database.repository.UserRepository;
import com.greem4.springmedicines.dto.ChangePasswordRequest;
import com.greem4.springmedicines.dto.UserCreatedRequest;
import com.greem4.springmedicines.dto.UserResponse;
import com.greem4.springmedicines.exception.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByProviderAndProviderId(String provider, String providerId) {
        return userRepository.findByProviderAndProviderId(provider, providerId);
    }

    // fixme: транзакция?
    public UserResponse createUser(UserCreatedRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(true)
                .build();
        var savedUser = userRepository.save(user);
        return toUserResponse(savedUser);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toUserResponse);
    }

    public UserResponse getUserByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));
        return toUserResponse(user);
    }

    @SneakyThrows // fixme: почитай доку, для каких случаев сами создатели рекомендуют эту анноташку
    public void changePassword(String username, @Valid ChangePasswordRequest request) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        // fixme: Посмотри в сторону интерфейса Validator в spring validation
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Старый пароль введён неверно");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new PasswordMismatchException("Новый пароль и его подтверждение не совпадают");
        }

        if (request.newPassword().length() < 6) {// fixme: кажется, это ты валидируешь в само реквесте
            throw new PasswordTooShortException("Новый пароль должен быть не менее 6 символов");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void saveOAuthUser(String login, String provider, String providerId) {
        User user = new User();
        user.setUsername(login);
        user.setPassword(passwordEncoder.encode("${//dd//pass}"));
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setRole(Role.USER);
        user.setEnabled(true);

        userRepository.save(user);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled()
        );
    }
}
