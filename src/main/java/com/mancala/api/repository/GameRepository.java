package com.mancala.api.repository;

import com.mancala.api.models.Game;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, String>{
    
}
