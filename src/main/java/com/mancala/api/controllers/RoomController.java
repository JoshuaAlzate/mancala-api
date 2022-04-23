package com.mancala.api.controllers;

import com.mancala.api.models.Player;
import com.mancala.api.models.Room;
import com.mancala.api.services.RoomService;

import org.springframework.http.ResponseEntity;
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
}
