package com.mancala.api.models;

import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("Room")
public class Room {
    public String id;
    public String name;
    public Player[] players;
}
