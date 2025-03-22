package com.jereczek.checkers.repositories;

import com.jereczek.checkers.model.GameData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameDataRepo extends JpaRepository<GameData, Integer> {

}
