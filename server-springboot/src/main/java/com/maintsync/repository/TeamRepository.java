package com.maintsync.repository;

import com.maintsync.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN t.members m WHERE m.id = :userId OR t.memberUser.id = :userId ORDER BY t.name ASC")
    List<Team> findTeamsByMemberUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Team t ORDER BY t.name ASC")
    List<Team> findAllOrderedByName();
}
