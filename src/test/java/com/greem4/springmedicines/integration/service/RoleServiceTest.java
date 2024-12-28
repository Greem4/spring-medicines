package com.greem4.springmedicines.integration.service;

import com.greem4.springmedicines.database.entity.Role;
import com.greem4.springmedicines.database.repository.UserRepository;
import com.greem4.springmedicines.dto.UserRoleUpdateRequest;
import com.greem4.springmedicines.integration.config.IntegrationTestBase;
import com.greem4.springmedicines.service.UserRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

public class RoleServiceTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    @Test
    void testUpdateUserRole() {
        var user = userRepository.findByUsername("user").orElseThrow();
        assertThat(user.getRole()).isEqualTo(Role.USER);

        var request = new UserRoleUpdateRequest("user", "ADMIN");
        userRoleService.updateUserRole(request);

        var update = userRepository.findByUsername("user").orElseThrow();
        assertThat(update.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void testDisableUserRole() {
        var user = userRepository.findByUsername("user").orElseThrow();
        assertThat(user.isEnabled()).isTrue();

        userRoleService.disableUser("user");

        var update = userRepository.findByUsername("user").orElseThrow();
        assertThat(update.isEnabled()).isFalse();
    }

    @Test
    void testEnableUserRole() {
        userRoleService.disableUser("user");
        var user = userRepository.findByUsername("user").orElseThrow();
        assertThat(user.isEnabled()).isFalse();

        userRoleService.enableUser("user");
        var update = userRepository.findByUsername("user").orElseThrow();
        assertThat(update.isEnabled()).isTrue();
    }
}