package com.jereczek.checkers.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jereczek.checkers.model.players.PlayerEntity;
import com.jereczek.checkers.repositories.PlayerRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class PlayerAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final PlayerRepo playerRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        PlayerEntity player = playerRepo.findByUsername(user.getUsername());
        response.getWriter().write(new ObjectMapper().writeValueAsString(player));
    }
}
