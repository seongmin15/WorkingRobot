package com.wor.server.domain.repository;

import com.wor.server.domain.entity.roomResEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface roomResRepository extends JpaRepository<roomResEntity, Integer> {
    List<Optional<roomResEntity>> findAllByUserId(String user_id, Sort sort);

    List<Optional<roomResEntity>> findAllByDateAndResStart(String date, String resStart);

    List<Optional<roomResEntity>> findAllByDateAndRoomNum(String date, Integer room_num);
}
