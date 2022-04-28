package com.mancala.api.models;

import org.springframework.data.redis.core.RedisHash;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@RedisHash("RoomPlayer")
public class RoomPlayer extends Player {
    public boolean isReady = false;

    public void setPlayerDetails(Player player) {
        setId(player.id);
        setName(player.name);
        setLevel(player.level);
    }
}
