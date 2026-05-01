package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.User;
import com.ecommerce.analytics.model.enums.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findByRole(RoleType role, Pageable pageable);
    Page<User> findByRoleNot(RoleType role, Pageable pageable);
}
