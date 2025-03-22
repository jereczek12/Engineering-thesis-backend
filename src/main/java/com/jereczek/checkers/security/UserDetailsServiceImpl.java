package com.jereczek.checkers.security;

import com.jereczek.checkers.model.players.PlayerEntity;
import com.jereczek.checkers.service.PlayerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PlayerService playerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<PlayerEntity> playerHuman = Optional.ofNullable(playerService.findPlayerByUsername(username));
        if (playerHuman.isPresent()) {
            return new User(playerHuman.get().getUsername(), playerHuman.get().getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Player not found with username: " + username);
        }
    }
}
