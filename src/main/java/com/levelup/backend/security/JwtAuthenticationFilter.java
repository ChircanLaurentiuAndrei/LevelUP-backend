package com.levelup.backend.security;

import com.levelup.backend.entity.User;
import com.levelup.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    private String parseJwt(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseJwt(request);
            if (token != null && jwtUtils.validateJwtToken(token)) {
                UUID userId = jwtUtils.getUserIdFromJwtToken(token);

                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password("")
                            .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                            .build();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
        }
        filterChain.doFilter(request, response);
    }
}
