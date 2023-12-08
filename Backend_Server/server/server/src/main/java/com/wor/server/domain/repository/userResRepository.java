package com.wor.server.domain.repository;

import com.wor.server.domain.entity.userResEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface userResRepository extends JpaRepository<userResEntity, String> {
    List<Optional<userResEntity>> findAllByRobotSerial(String serial);
}
