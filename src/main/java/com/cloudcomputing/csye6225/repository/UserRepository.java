package com.cloudcomputing.csye6225.repository;

import com.cloudcomputing.csye6225.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

   /* @Modifying
    @Query("UPDATE User u SET u.firstName = ?1, u.lastName = ?2, u.password = ?3 where u.username = ?4")
    void updateUserDetailsByUsername(String firstname, String lastname, String password, String username);*/

}