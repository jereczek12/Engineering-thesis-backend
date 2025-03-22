package com.jereczek.checkers.repositories;

import com.jereczek.checkers.model.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardStateRepo extends JpaRepository<BoardEntity, Integer> {
}
