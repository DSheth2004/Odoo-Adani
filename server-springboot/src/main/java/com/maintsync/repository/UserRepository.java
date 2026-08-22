package com.maintsync.repository;

import com.maintsync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE (:role IS NULL OR LOWER(u.role) = LOWER(:role)) " +
           "AND (:q IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY u.fullName ASC")
    List<User> searchUsers(@Param("role") String role, @Param("q") String q);

    long countByRole(String role);
}
