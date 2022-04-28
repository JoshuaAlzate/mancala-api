package com.mancala.api.models;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

@Data
@RedisHash("Player")
public class Player implements Serializable {
    public String id;
    public String name;
    public int level;
}
