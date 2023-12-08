package com.wor.server.domain.repository;

import com.wor.server.domain.entity.robotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface robotRepository extends JpaRepository<robotEntity, String> {
}
