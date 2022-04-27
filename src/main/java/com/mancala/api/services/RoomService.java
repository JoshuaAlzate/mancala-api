package com.mancala.api.services;

import java.util.UUID;

import com.mancala.api.models.Player;
import com.mancala.api.models.Room;
import com.mancala.api.respository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoomService {

    @Autowired
    public final RoomRepository roomRepository;

    public Room createRoom(Player player) {
        Player[] players = new Player[] { player };
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setName("name");
        room.setPlayers(players);
        roomRepository.save(room);
        return room;
    }
}
