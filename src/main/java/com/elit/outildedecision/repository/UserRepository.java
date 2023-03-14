
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This interface represents a Spring repository for accessing and managing User entities.
 * It extends the JpaRepository interface and is used for CRUD operations on User objects.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Returns the user with the specified username.
   *
   * @param username the username of the user to retrieve
   * @return the user with the specified username
   */
  User findByUsername(String username);

  /**
   * Returns the user with the specified email address.
   *
   * @param userEmail the email address of the user to retrieve
   * @return the user with the specified email address
   */
  @Query("SELECT u FROM User u WHERE u.userEmail = ?1")
  User findByUserEmail(String userEmail);

  /**
   * Returns a list of users whose email address or username contains the specified keyword.
   *
   * @param keyword the keyword to search for in email addresses and usernames
   * @return a list of users whose email address or username contains the specified keyword
   */
  @Query("SELECT u FROM User u WHERE u.userEmail LIKE %?1% OR u.username LIKE %?1%")
  List<User> findAllByUserEmailOrUsernameContaining(String keyword);

  /**
   * Returns the user with the specified email address if they are verified and active.
   *
   * @param userEmail the email address of the user to retrieve
   * @return the verified and active user with the specified email address
   */
  @Query("SELECT u FROM User u WHERE u.userEmail = ?1 AND u.isEmailVerified = true AND u.isUserActive = true")
  User findByUserEmailAndIsVerified(String userEmail);

  /**
   * Checks if a user with the specified email address or username exists.
   *
   * @param userEmail the email address to check for
   * @param username the username to check for
   * @return true if a user with the specified email address or username exists, false otherwise
   */
  boolean existsByUserEmailOrUsername(String userEmail, String username);
}
