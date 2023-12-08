package com.wor.server.domain.repository;

import com.wor.server.domain.entity.voteEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface voteRepository extends JpaRepository<voteEntity, Integer> {
    List<Optional<voteInfo>> findAllByAreaNum(Integer area_num);

    List<Optional<voteEntity>> findAllByChecked(Boolean checked);

    @Transactional
    @Modifying
    @Query(value = "truncate vote_tbl", nativeQuery = true)
    void truncateSomethingTable();
}
