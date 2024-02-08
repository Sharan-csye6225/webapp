package com.cloudcomputing.csye6225.repository;

import com.cloudcomputing.csye6225.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

}