package com.mancala.api.services;

import java.util.UUID;

import com.mancala.api.models.Player;
import com.mancala.api.respository.PlayerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    @Autowired
    public PlayerRepository playerRepository;

    public Player createPlayer(String name) {
        Player player = new Player();
        player.setId(UUID.randomUUID().toString());
        player.setName(name);
        playerRepository.save(player);
        return player;
    }

    public boolean deletePlayers() {
        playerRepository.deleteAll();
        return true;
    }
}
