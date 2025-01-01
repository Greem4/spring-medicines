package com.greem4.springmedicines.service;

import com.greem4.springmedicines.database.entity.User;
import com.greem4.springmedicines.database.repository.UserRepository;
import com.greem4.springmedicines.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return new CustomUserDetails(user);
    }
}
