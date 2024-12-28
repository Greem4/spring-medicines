package com.greem4.springmedicines.service;

import com.greem4.springmedicines.database.entity.User;
import com.greem4.springmedicines.database.entity.Role;
import com.greem4.springmedicines.database.repository.UserRepository;
import com.greem4.springmedicines.dto.UserRoleUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRepository userRepository;

    public User updateUserRole(UserRoleUpdateRequest request) {
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        user.setRole(Role.valueOf(request.role().toUpperCase()));
        return userRepository.save(user);
    }

    public User enableUser(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User disableUser(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        user.setEnabled(false);
        return userRepository.save(user);
    }
}
