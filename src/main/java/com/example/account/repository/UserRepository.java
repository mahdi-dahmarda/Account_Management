package com.example.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.account.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findOneByEmailIgnoreCase(String email);
}
