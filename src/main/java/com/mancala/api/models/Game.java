package com.mancala.api.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("Game")
public class Game implements Serializable {
    String id;
    Room room;
    ArrayList<Pit> pits = new ArrayList<Pit>();
    Player winnerPlayer;

    private Integer initialStoneCount = 3;

    public Game() {
        id = UUID.randomUUID().toString();
        for (Integer x = 0; x < 14; x++) {
            if ((x + 1) % 7 == 0) {
                pits.add(new Pit(x, 0, true));
            } else {
                pits.add(new Pit(x, initialStoneCount, false));
            }
        }
    }
}
