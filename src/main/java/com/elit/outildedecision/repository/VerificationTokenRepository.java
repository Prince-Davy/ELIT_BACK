
package com.elit.outildedecision.repository;

import com.elit.outildedecision.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository interface for managing verification tokens for user registration.
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {

  /**
   * Finds a verification token by its unique token string.
   *
   * @param token The unique token string
   * @return The verification token, or null if not found
   */
  VerificationToken findByToken(String token);

  /**
   * Removes a verification token with the specified token string.
   *
   * @param token The unique token string
   */
  void removeByToken(String token);
}

