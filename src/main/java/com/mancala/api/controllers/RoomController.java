package com.mancala.api.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mancala.api.models.Player;
import com.mancala.api.models.Room;
import com.mancala.api.services.RoomService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/getAll")
    Iterable<Room> getAllRooms() {
        return roomService.roomRepository.findAll();
    }

    @PutMapping("/create")
    ResponseEntity<Room> createRoom(@RequestBody Player player) {
        log.info("Create game request: {}", player);
        return ResponseEntity.ok(roomService.createRoom(player));
    }

    @DeleteMapping("/deleteAll")
    ResponseEntity<Boolean> deleteRooms() {
        log.info("Delete all rooms: {}");
        return ResponseEntity.ok(roomService.deleteRooms());
    }

    @PutMapping("/enterRoom")
    ResponseEntity<Room> enterRoom(@RequestBody ObjectNode json) {
        String roomID = json.get("roomID").asText();
        String playerID = json.get("playerID").asText();
        log.info("Player : {} is entering the room : {}", playerID, roomID);
        return ResponseEntity.ok(roomService.enterRoom(roomID, playerID));
    }

    @GetMapping("/getDetails/{roomID}")
    ResponseEntity<Room> getRoomDetails(@PathVariable("roomID") String roomID) {
        log.info("Getting room details of id: {}", roomID);
        return ResponseEntity.ok(roomService.getRoomDetails(roomID));
    }

    @PostMapping("/checkRoomPlayers")
    ResponseEntity<Void> checkRoomPlayers(@RequestBody ObjectNode json) {
        String roomID = json.get("roomID").asText();
        String playerID = json.get("playerID").asText();
        log.info("Checking players in the room: {}", roomID);
        roomService.checkRoomPlayers(roomID, playerID);
        return ResponseEntity.noContent().build();
    }
}
