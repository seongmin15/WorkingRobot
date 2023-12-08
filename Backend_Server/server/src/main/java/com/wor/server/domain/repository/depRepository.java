package com.wor.server.domain.repository;

import com.wor.server.domain.entity.depEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface depRepository extends JpaRepository<depEntity, Integer> {
    List<depEntity> findAllByComName(String com_name);

    Optional<depEntity> findByComNameAndDepName(String com_name, String dep_name);
}
