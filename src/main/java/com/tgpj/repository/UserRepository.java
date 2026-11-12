package com.tgpj.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tgpj.entity.User;

public interface UserRepository extends MongoRepository<User, String> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
