package com.wor.server.domain.repository;

import com.wor.server.domain.entity.elecEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface elecRepository extends JpaRepository<elecEntity, Integer> {
    List<Optional<elecEntity>> findAllByRoomNum(Integer room_num);

    List<Optional<elecEntity>> findAllByAreaNum(Integer area_num);
}
