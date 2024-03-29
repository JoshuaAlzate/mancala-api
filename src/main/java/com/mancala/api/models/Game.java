package com.mancala.api.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import com.mancala.api.enums.PlayerTurnEnum;

import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("Game")
public class Game implements Serializable {
    String id;
    Room room;
    ArrayList<Pit> pits = new ArrayList<Pit>();
    PlayerTurnEnum playerTurn = PlayerTurnEnum.FIRST_PLAYER;
    RoomPlayer firstPlayer;
    RoomPlayer secondPlayer;
    RoomPlayer winnerPlayer;

    private Integer initialStoneCount = 3;

    public final static Integer FIRST_PLAYER_HOUSE = 6;
    public final static Integer SECOND_PLAYER_HOUSE = 13;

    public Game() {
        id = UUID.randomUUID().toString();
        for (Integer x = 0; x < SECOND_PLAYER_HOUSE + 1; x++) {
            if ((x + 1) % (FIRST_PLAYER_HOUSE + 1 )== 0) {
                pits.add(new Pit(x, 0, true));
            } else {
                pits.add(new Pit(x, initialStoneCount, false));
            }
        }
    }

    public Pit getPitByIndex(Integer index) {
        return pits.get(index);
    }
}
