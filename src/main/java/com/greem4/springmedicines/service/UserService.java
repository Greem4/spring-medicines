package com.greem4.springmedicines.service;

import com.greem4.springmedicines.database.entity.User;
import com.greem4.springmedicines.database.entity.UserRole;
import com.greem4.springmedicines.database.repository.UserRepository;
import com.greem4.springmedicines.dto.UserCreatedRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserCreatedRequest request) {
        var role = UserRole.valueOf(request.role().toUpperCase());
        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
