package com.greem4.springmedicines.unit.config;

import com.greem4.springmedicines.filter.JwtAuthenticationFilter;
import com.greem4.springmedicines.service.CustomUserDetailsService;
import com.greem4.springmedicines.util.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;


@ExtendWith(MockitoExtension.class)
public abstract class UnitTestBase {

    @Mock
    protected JwtUtils jwtUtils;
    @Mock
    protected CustomUserDetailsService userDetailsService;
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpServletResponse response;
    @Mock
    protected FilterChain chain;

    protected JwtAuthenticationFilter filter;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtUtils, userDetailsService);
        SecurityContextHolder.clearContext();
    }
}
