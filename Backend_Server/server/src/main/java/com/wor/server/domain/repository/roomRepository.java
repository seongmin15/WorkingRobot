package com.wor.server.domain.repository;

import com.wor.server.domain.entity.roomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface roomRepository extends JpaRepository<roomEntity, Integer> {
    List<Optional<roomEntity>> findAllByComNum(Integer com_num);

    List<Optional<roomEntity>> findAllByRobotSerial(String robot_serial);
}
