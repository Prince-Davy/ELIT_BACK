package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);

  @Query("SELECT u FROM User u " +
    "WHERE u.userEmail= ?1")
  User findByUserEmail(String userEmail);

  @Query("SELECT u FROM User u " +
    "WHERE u.userEmail= ?1 " +
    "or u.username = ?2")
  User findByUseremailOrUsername(String userEmail, String username);

  @Query("select u from User u " +
    "where u.userEmail like %?1% " +
    "or u.username like %?1%")
  List<User> findAllByUserEmailOrUsernameContaining(String keyword);

  @Query(
      "select u from User u " +
        "where u.userEmail= ?1 " +
        "and u.isEmailVerified = true " +
        "and u.isUserActive = true")
  User findByUserEmailAndIsVerified(String username);
}
