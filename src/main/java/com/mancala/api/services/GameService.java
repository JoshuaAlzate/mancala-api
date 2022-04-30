package com.mancala.api.services;

import java.util.Optional;

import com.mancala.api.enums.PlayerTurnEnum;
import com.mancala.api.exceptions.GameException;
import com.mancala.api.exceptions.RoomException;
import com.mancala.api.models.Game;
import com.mancala.api.models.Pit;
import com.mancala.api.models.Room;
import com.mancala.api.repository.GameRepository;
import com.mancala.api.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GameService {

    @Autowired
    private final GameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public Iterable<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game createGame(String roomID) {
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        optionalRoom.orElseThrow(() -> new GameException("The room ID provided is not existing"));

        Room room = optionalRoom.get();
        Game game = new Game();
        game.setRoom(room);
        game.setFirstPlayer(room.getFirstPlayer());
        game.setSecondPlayer(room.getSecondPlayer());
        gameRepository.save(game);
        return game;
    }

    public boolean deleteGames() {
        gameRepository.deleteAll();
        return true;
    }

    public Game getGameDetails(String gameID) {
        Optional<Game> optionalGame = gameRepository.findById(gameID);
        optionalGame.orElseThrow(() -> new GameException("The game ID provided is not existing"));

        Game game = optionalGame.get();
        return game;
    }

    // Game Logic
    public void playerTurn(String gameID, Integer pitIndex) {
        Game game = getGameDetails(gameID);
        Pit selectedPit = game.getPitByIndex(pitIndex);

        Integer selectedPitStones = selectedPit.getStones();
        selectedPit.setStones(0);

        for (int x = selectedPitStones; x != 0; x--) {
            pitIndex = getNextPitIndex(pitIndex++);
            Pit pit = game.getPitByIndex(pitIndex);
            if (pit.isHouse()) {
                if ((game.getPlayerTurn().equals(PlayerTurnEnum.FIRST_PLAYER) && pitIndex == 6) ||
                        (game.getPlayerTurn().equals(PlayerTurnEnum.SECOND_PLAYER) && pitIndex == 13)) {
                    pit.addStone();
                }
            } else {
                pit.addStone();
            }
        }

        // Capture
        Pit lastPit = game.getPitByIndex(pitIndex);
        capturePit(pitIndex, game);

        if (!lastPit.isHouse()) {
            switchPlayerTurn(game, lastPit);
        }
        checkEndGame(game);

        gameRepository.save(game);
        socketGameProgress(game.getId());

    }

    private Integer getNextPitIndex(Integer currentPitIndex) {
        currentPitIndex = (currentPitIndex + 1) % 14;
        return currentPitIndex;
    }

    private Pit capturePit(Integer pitIndex, Game game) {
        Pit lastPit = game.getPitByIndex(pitIndex);
        Pit oppositePit = game.getPitByIndex(12 - pitIndex >= 0 ? 12 - pitIndex : 7);
        if (!lastPit.getId().equals(Game.FIRST_PLAYER_HOUSE) &&
                !lastPit.getId().equals(Game.SECOND_PLAYER_HOUSE) &&
                lastPit.getStones() == 1 &&
                ((game.getPlayerTurn().equals(PlayerTurnEnum.FIRST_PLAYER) && lastPit.getId() < 7) ||
                        (game.getPlayerTurn().equals(PlayerTurnEnum.SECOND_PLAYER) && lastPit.getId() > 8))) {

            Pit housePit = pitIndex < Game.FIRST_PLAYER_HOUSE ? game.getPitByIndex(Game.FIRST_PLAYER_HOUSE)
                    : game.getPitByIndex(Game.SECOND_PLAYER_HOUSE);
            housePit.addStones(oppositePit.getStones() + lastPit.getStones());
            lastPit.setStones(0);
            oppositePit.setStones(0);
        }

        return lastPit;
    }

    private void switchPlayerTurn(Game game, Pit lastPin) {
        if (!((game.getPlayerTurn().equals(PlayerTurnEnum.FIRST_PLAYER)
                && lastPin.getId().equals(Game.FIRST_PLAYER_HOUSE)) ||
                (game.getPlayerTurn().equals(PlayerTurnEnum.SECOND_PLAYER)
                        && lastPin.getId().equals(Game.SECOND_PLAYER_HOUSE)))) {
            game.setPlayerTurn(PlayerTurnEnum.togglePlayerTurn(game.getPlayerTurn()));
        }
    }

    private void checkEndGame(Game game) {
        Integer stoneSumOfFirstPlayer = 0;
        for (int x = 0; x < Game.FIRST_PLAYER_HOUSE; x++) {
            stoneSumOfFirstPlayer = stoneSumOfFirstPlayer + game.getPitByIndex(x).getStones();
        }

        Integer stoneSumOfSecondPlayer = 0;
        for (int x = 7; x < Game.SECOND_PLAYER_HOUSE; x++) {
            stoneSumOfSecondPlayer = stoneSumOfSecondPlayer + game.getPitByIndex(x).getStones();
        }

        Pit firstPlayerHouse = game.getPitByIndex(Game.FIRST_PLAYER_HOUSE);
        Pit secondPlayerHouse = game.getPitByIndex(Game.SECOND_PLAYER_HOUSE);

        if (stoneSumOfFirstPlayer == 0 || stoneSumOfSecondPlayer == 0) {

            for (int x = 1; x < Game.FIRST_PLAYER_HOUSE; x++) {
                game.getPitByIndex(x).setStones(0);
            }
            firstPlayerHouse.setStones(firstPlayerHouse.getStones() + stoneSumOfFirstPlayer);

            for (int x = 8; x < Game.SECOND_PLAYER_HOUSE; x++) {
                game.getPitByIndex(x).setStones(0);
            }
            secondPlayerHouse.setStones(secondPlayerHouse.getStones() + stoneSumOfSecondPlayer);

            game.setWinnerPlayer(firstPlayerHouse.getStones() > secondPlayerHouse.getStones() ? game.getFirstPlayer()
                    : game.getSecondPlayer());

            if (firstPlayerHouse.getStones().equals(secondPlayerHouse.getStones())) {
                // TODO: Draw
            }
            resetPlayerReadiness(game.getRoom().getId());
        }
    }

    private void resetPlayerReadiness(String roomID) {
        Optional<Room> optionalRoom = roomRepository.findById(roomID);
        optionalRoom.orElseThrow(() -> new RoomException("The given room ID does not exist"));

        Room room = optionalRoom.get();
        room.firstPlayer.setReady(false);
        room.secondPlayer.setReady(false);
        room.setGameID(null);
        roomRepository.save(room);
    }

    // Sockets
    private void socketGameProgress(String gameID) {
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameID, gameRepository.findById(gameID));
    }

}
