package com.vaibhav.ecom.UserMicroservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaibhav.ecom.UserMicroservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

	Optional<User> findByExternalSub(String externalSub);

}
