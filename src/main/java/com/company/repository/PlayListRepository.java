package com.company.repository;

import com.company.models.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    Boolean existsByUserIdAndName(long id, String name);

    Optional<PlayList> findByName(String name);

    List<PlayList> findAllByUserId(long id);

    Optional<PlayList> findByIdAndUserId(Long id, long id1);
}
