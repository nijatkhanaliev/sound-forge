package com.company.repository;


import com.company.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.playLists IS EMPTY AND u.createdAt < :cutOffDate")
    List<Long> findUserWithOnePlaylistAndCreatedBefore(@Param("cutOffDate")LocalDateTime localDateTime);
}
