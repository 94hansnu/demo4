package com.example.demo4.repository;

import com.example.demo4.payload.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Detta är en Spring Data JPA-repositoriy klass för att interagera med databasen och hantera User-entiteter.
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
}
