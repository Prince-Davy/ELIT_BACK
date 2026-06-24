
package com.elit.outildedecision.shared.auth.repository;

import com.elit.outildedecision.shared.auth.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

  /**
   * Bulk-deletes every verification token owned by any of the specified users.
   *
   * @param userIds the ids of the users whose tokens must be deleted
   */
  @Modifying
  @Query("DELETE FROM VerificationToken t WHERE t.user.userId IN :userIds")
  void deleteAllByUserIdIn(@Param("userIds") List<Long> userIds);
}

