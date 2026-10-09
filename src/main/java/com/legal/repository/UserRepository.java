package com.legal.repository;

import com.legal.model.Role;
import com.legal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for User entity persistence and credential lookups.
 * Owned by Ayush Rathore (User, Auth & Case Management Module).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}
