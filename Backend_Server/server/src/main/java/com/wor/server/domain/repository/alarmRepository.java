package com.wor.server.domain.repository;

import com.wor.server.domain.entity.alarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface alarmRepository extends JpaRepository<alarmEntity, Integer> {
    List<Optional<alarmEntity>> findAllByUserId(String user_id);
}
