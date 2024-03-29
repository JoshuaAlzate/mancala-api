package com.mancala.api.services;

import java.util.Optional;
import java.util.UUID;

import com.mancala.api.enums.RoomStatusEnum;
import com.mancala.api.exceptions.RoomException;
import com.mancala.api.models.Game;
import com.mancala.api.models.Player;
import com.mancala.api.models.Room;
import com.mancala.api.models.RoomPlayer;
import com.mancala.api.repository.RoomRepository;

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
    private final PlayerService playerService;
    private final GameService gameService;

    public Room createRoom(Player player) {
        Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setName(player.name + "'s Room");

        RoomPlayer roomPlayer = new RoomPlayer();
        roomPlayer.setPlayerDetails(player);

        room.setFirstPlayer(roomPlayer);
        roomRepository.save(room);
        socketRoomList();
        return room;
    }

    public boolean deleteRooms() {
        roomRepository.deleteAll();
        socketRoomList();
        return true;
    }

    public Room enterRoom(String roomID, String playerID) {
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        optionalRoom.orElseThrow(() -> new RoomException("The given room ID does not exist"));

        Room room = optionalRoom.get();
        Player player = playerService.getPlayer(playerID);
        RoomPlayer roomPlayer = new RoomPlayer();
        roomPlayer.setPlayerDetails(player);

        room.setSecondPlayer(roomPlayer);
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

    public void checkRoomPlayers(String roomID, String playerID) {
        Room room = getRoomDetails(roomID);
        RoomPlayer firstPlayer = room.getFirstPlayer();
        RoomPlayer secondPlayer = room.getSecondPlayer();
        if (secondPlayer == null) {
            roomRepository.deleteById(room.id);
            socketRoomList();
            return;
        }

        if (playerID.equals(firstPlayer.id)) {
            room.setFirstPlayer(secondPlayer);
            room.setSecondPlayer(null);
            room.setName(secondPlayer.name + "'s Room");
        } else if (playerID.equals(secondPlayer.id)) {
            room.setSecondPlayer(null);
        }
        roomRepository.save(room);
        socketRoomUpdate(room.getId());
        socketRoomList();
    }

    public Room setPlayerReady(String roomID, String roomPlayerID, Boolean isReady) {
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        optionalRoom.orElseThrow(() -> new RoomException("The given room ID does not exist"));

        Room room = optionalRoom.get();
        if (roomPlayerID.equals(room.getFirstPlayer().getId())) {
            room.getFirstPlayer().setReady(isReady);
        } else if (roomPlayerID.equals(room.getSecondPlayer().getId())) {
            room.getSecondPlayer().setReady(isReady);
        }

        if (room.getFirstPlayer().isReady() && room.getSecondPlayer().isReady()) {
            Game game = gameService.createGame(room.getId());
            room.setGameID(game.getId());
        }
        roomRepository.save(room);
        socketRoomUpdate(room.getId());
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
