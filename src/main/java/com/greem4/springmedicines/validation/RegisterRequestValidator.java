package com.greem4.springmedicines.validation;

import com.greem4.springmedicines.dto.RegisterRequest;
import com.greem4.springmedicines.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class RegisterRequestValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterRequest request = (RegisterRequest) target;

        if (userRepository.existsByUsername(request.username())) {
            errors.rejectValue("username", "Duplicate.userForm.username", "Имя пользователя уже используется");
        }

    }
}
