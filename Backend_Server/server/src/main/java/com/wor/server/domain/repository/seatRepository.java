package com.wor.server.domain.repository;
import com.wor.server.domain.entity.seatEntity;
import com.wor.server.domain.entity.seatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface seatRepository extends JpaRepository<seatEntity, seatId> {
    List<Optional<seatEntity>> findAllByAreaNum(Integer area_num);
}
