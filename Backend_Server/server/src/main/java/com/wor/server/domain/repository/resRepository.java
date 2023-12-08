package com.wor.server.domain.repository;

import com.wor.server.domain.entity.resEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface resRepository extends JpaRepository<resEntity, Integer> {
    Optional<resEntity> findFirstByRobotSerialAndType(String robotSerial, String type);
    void deleteAllByProcess(Integer process);

    @Transactional
    @Modifying
    @Query(value = "truncate res_tbl", nativeQuery = true)
    void truncateSomethingTable();
}
