package com.mancala.api.controllers;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mancala.api.models.Player;
import com.mancala.api.services.PlayerService;
import com.mancala.api.services.RoomService;

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
    private final RoomService roomService;

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

    @PutMapping("/setReady")
    ResponseEntity<Player> setReadyPlayer(@RequestBody ObjectNode json) {
        String roomID = json.get("roomID").asText();
        String playerID = json.get("playerID").asText();
        Boolean isReady = json.get("isReady").asBoolean();
        log.info("Set ready of player request: {}", playerID);
        Player player = playerService.setPlayerReadiness(playerID, isReady);
        roomService.setPlayerReady(roomID, player);
        return ResponseEntity.ok(player);
    }
}
