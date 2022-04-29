package com.mancala.api.services;

import java.util.Optional;

import com.mancala.api.enums.PlayerTurnEnum;
import com.mancala.api.exceptions.GameException;
import com.mancala.api.models.Game;
import com.mancala.api.models.Pit;
import com.mancala.api.models.Player;
import com.mancala.api.models.Room;
import com.mancala.api.repository.GameRepository;
import com.mancala.api.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GameService {

    @Autowired
    private final GameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final PlayerService playerService;

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
    public void playerTurn(String gameID, String playerID, Integer pitIndex) {
        Game game = getGameDetails(gameID);
        Player player = playerService.getPlayer(playerID);
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

        gameRepository.save(game);

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

}
