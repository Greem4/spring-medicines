package com.greem4.springmedicines.validation;

import com.greem4.springmedicines.database.repository.UserRepository;
import com.greem4.springmedicines.dto.ChangePasswordRequest;
import com.greem4.springmedicines.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangePassValidator implements Validator {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangePasswordRequest request = (ChangePasswordRequest) target;
        String username = request.username();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            log.warn("Неправильный старый пароль для пользователя: {}", username);
            errors.rejectValue("oldPassword", "error.oldPassword", "Старый пароль введён неверно");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())){
            log.warn("Новый пароль и его подтверждение не совпадают для пользователя: {}", username);
            errors.rejectValue("confirmNewPassword", "password.mismatch", "Новый пароль и его подтверждение не совпадают");
        }

        if (request.newPassword().length() < 6) {
            log.warn("Новый пароль слишком короткий для пользователя: {}", username);
            errors.rejectValue("newPassword", "password.tooShort", "Новый пароль должен быть не менее 6 символов");
        }
    }
}
