package com.mancala.api.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mancala.api.models.Game;
import com.mancala.api.services.GameService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    @GetMapping("/getAll")
    Iterable<Game> getAllRooms() {
        return gameService.getAllGames();
    }

    @PutMapping("/create")
    ResponseEntity<Game> createGame(@RequestBody String roomID) {
        log.info("Create game session with respect to room ID: {}", roomID);
        return ResponseEntity.ok(gameService.createGame(roomID));
    }

    @DeleteMapping("/deleteAll")
    ResponseEntity<Boolean> deleteRooms() {
        log.info("Delete all rooms: {}");
        return ResponseEntity.ok(gameService.deleteGames());
    }

    @GetMapping("/getDetails/{gameID}")
    ResponseEntity<Game> getGameDetails(@PathVariable("gameID") String gameID) {
        log.info("Getting room details of id: {}", gameID);
        return ResponseEntity.ok(gameService.getGameDetails(gameID));
    }

    @PutMapping("/move")
    ResponseEntity<Void> makeAMove(@RequestBody ObjectNode json) {
        String gameID = json.get("gameID").asText();
        String playerID = json.get("playerID").asText();
        Integer pitIndex = json.get("pitIndex").asInt();

        gameService.playerTurn(gameID, playerID, pitIndex);
        return ResponseEntity.ok().build();
    }
}
