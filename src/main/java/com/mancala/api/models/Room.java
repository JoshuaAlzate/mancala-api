package com.mancala.api.models;

import com.mancala.api.enums.RoomStatusEnum;

import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("Room")
public class Room {
    public String id;
    public String name;
    public RoomStatusEnum status = RoomStatusEnum.WAITING_FOR_OTHER_PLAYERS;
    public RoomPlayer firstPlayer;
    public RoomPlayer secondPlayer;
    public String gameID;

    public void setSecondPlayer(RoomPlayer roomPlayer) {
        secondPlayer = roomPlayer;
        if (secondPlayer != null)
            secondPlayer.setSecondPlayer(true);
    }
}
