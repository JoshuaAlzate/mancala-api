package com.mancala.api.controllers;

import java.util.ArrayList;

import com.mancala.api.models.Player;
import com.mancala.api.services.PlayerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;

    @GetMapping("/getAll")
    Iterable<Player> getAllPlayers() {
        return playerService.playerRepository.findAll();
    }

    @PutMapping("/create")
    ResponseEntity<Player> createPlayer(@RequestBody String name) {
        log.info("Create player request: {}", name);
        return ResponseEntity.ok(playerService.createPlayer(name));
    }

    @DeleteMapping("/deleteAll")
    ResponseEntity<Boolean> deletePlayer() {
        log.info("Delete all players: {}");
        return ResponseEntity.ok(playerService.deletePlayers());
    }
}
