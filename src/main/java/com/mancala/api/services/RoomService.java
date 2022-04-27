package com.mancala.api.services;

import java.util.Optional;
import java.util.UUID;

import com.mancala.api.enums.RoomStatusEnum;
import com.mancala.api.exceptions.RoomException;
import com.mancala.api.models.Player;
import com.mancala.api.models.Room;
import com.mancala.api.respository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoomService {

    @Autowired
    public final RoomRepository roomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public Room createRoom(Player player) {
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setName(player.name + "'s Room");
        room.setFirstPlayer(player);
        roomRepository.save(room);
        socketRoomList();
        return room;
    }

    public boolean deleteRooms() {
        roomRepository.deleteAll();
        socketRoomList();
        return true;
    }

    public Room enterRoom(String roomID, Player player) {
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        optionalRoom.orElseThrow(() -> new RoomException("The given room ID does not exist"));

        Room room = optionalRoom.get();
        room.setSecondPlayer(player);
        room.setStatus(RoomStatusEnum.FULL);
        roomRepository.save(room);
        socketRoomUpdate(room.id);
        socketRoomList();
        return room;
    }

    public Room getRoomDetails(String roomID) {
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        optionalRoom.orElseThrow(() -> new RoomException("The given room ID does not exist"));

        Room room = optionalRoom.get();
        return room;
    }

        return room;
    }

    // Sockets
    private void socketRoomList() {
        simpMessagingTemplate.convertAndSend("/topic/room-list-update", roomRepository.findAll());
    }

    private void socketRoomUpdate(String roomID) {
        simpMessagingTemplate.convertAndSend("/topic/room-update/" + roomID, roomRepository.findById(roomID));
    }

}
