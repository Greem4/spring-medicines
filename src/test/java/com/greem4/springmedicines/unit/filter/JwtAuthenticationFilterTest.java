package com.greem4.springmedicines.unit.filter;

import com.greem4.springmedicines.security.CustomUserDetails;
import com.greem4.springmedicines.unit.config.UnitTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest extends UnitTestBase {

    @Test
    void testValidToken() throws Exception {
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer valid.jwt.token");

        when(jwtUtils.validateJwtToken("valid.jwt.token"))
                .thenReturn(true);

        when(jwtUtils.getUsernameFromJwtToken("valid.jwt.token"))
                .thenReturn("admin");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetailsService.loadUserByUsername("admin"))
                .thenReturn(userDetails);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(userDetails);
    }

    @Test
    void testNoBearerToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);

        verify(response, never()).sendError(anyInt(), anyString());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}