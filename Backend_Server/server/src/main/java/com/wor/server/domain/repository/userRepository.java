package com.wor.server.domain.repository;

import com.wor.server.domain.entity.userEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userRepository extends JpaRepository<userEntity, String> {
    Optional<userEntity> findByUserId(String user_id);

    Optional<userEntity> findByUserName(String user_name);
}
