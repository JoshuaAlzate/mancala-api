package com.mancala.api.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Pit {

    private Integer id;
    private Integer stones;
    private boolean isHouse;

    @JsonIgnore
    public boolean isEmpty() {
        return stones == 0;
    }

    public void addStone() {
        stones++;
    }

    public void addStones(Integer i) {
        stones = stones + i;
    }

    public Integer countStones() {
        return stones;
    }

    @Override
    public String toString() {
        return id.toString() + ":" + countStones().toString();
    }
}
