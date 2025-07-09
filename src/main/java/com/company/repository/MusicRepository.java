package com.company.repository;

import com.company.models.entity.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MusicRepository extends JpaRepository<Music, Long>, JpaSpecificationExecutor<Music> {
    @Query(value = "select m from Music m where m.user.id!=:userId")
    Page<Music> findAll(Pageable pageable,@Param("userId") long id);
}
