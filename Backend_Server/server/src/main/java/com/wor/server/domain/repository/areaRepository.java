package com.wor.server.domain.repository;

import com.wor.server.domain.entity.areaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface areaRepository extends JpaRepository<areaEntity, Integer> {
    Optional<areaEntity> findByRobotSerial(String robot_serial);
}
