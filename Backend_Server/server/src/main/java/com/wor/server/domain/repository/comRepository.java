package com.wor.server.domain.repository;

import com.wor.server.domain.entity.comEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface comRepository extends JpaRepository<comEntity, Integer> {
    Optional<comEntity> findByComName(String com_name);
}
