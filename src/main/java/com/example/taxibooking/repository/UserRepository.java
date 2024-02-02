package com.example.taxibooking.repository;

import com.example.taxibooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {


    boolean existsByEmail(String email);

    Optional<Object> findByEmail(String email);

}
