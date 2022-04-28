package com.mancala.api.services;

import java.util.Optional;
import java.util.UUID;

import com.mancala.api.exceptions.PlayerException;
import com.mancala.api.models.Player;
import com.mancala.api.repository.PlayerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PlayerService {
    @Autowired
    public final PlayerRepository playerRepository;

    public Player createPlayer(String name) {
        Player player = new Player();
        player.setId(UUID.randomUUID().toString());
        player.setName(name);
        playerRepository.save(player);
        return player;
    }

    public Player getPlayer(String id) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        optionalPlayer.orElseThrow(() -> new PlayerException("Cannot find player with provided ID"));

        return optionalPlayer.get();
    }

    public boolean deletePlayers() {
        playerRepository.deleteAll();
        return true;
    }

}
