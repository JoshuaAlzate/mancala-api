package com.mancala.api.services;

import java.lang.StackWalker.Option;
import java.util.Optional;

import com.mancala.api.exceptions.GameException;
import com.mancala.api.models.Game;
import com.mancala.api.models.Room;
import com.mancala.api.repository.GameRepository;
import com.mancala.api.repository.RoomRepository;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final RoomRepository roomRepository;

    public Iterable<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game createGame(String roomID) {
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        optionalRoom.orElseThrow(() -> new GameException("The room ID provided is not existing"));

        Room room = optionalRoom.get();
        Game game = new Game();
        game.setRoom(room);
        gameRepository.save(game);
        return game;
    }

    public boolean deleteGames() {
        gameRepository.deleteAll();
        return true;
    }

    public Game getGameDetails(String roomID) {
        Optional<Game> optionalGame = gameRepository.findById(roomID);
        optionalGame.orElseThrow(() -> new GameException("The game ID provided is not existing"));

        Game game = optionalGame.get();
        return game;
    }
}
