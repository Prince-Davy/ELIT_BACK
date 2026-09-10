
package com.elit.outildedecision.shared.user.repository;

import com.elit.outildedecision.shared.kernel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * This interface represents a Spring repository for accessing and managing User
 * entities.
 * It extends the JpaRepository interface and is used for CRUD operations on
 * User objects.
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
         * Returns the permission labels granted to a user on a given project.
         * <p>
         * Projection query used by the method-security expressions: it returns the
         * permission strings
         * directly, without navigating the lazy {@code User#userProjectPermissions}
         * graph, so it works
         * while {@code spring.jpa.open-in-view=false} (the expressions are evaluated
         * outside any transaction).
         *
         * @param userEmail the email of the authenticated user
         * @param projectId the id of the project to check permissions for
         * @return the permission labels held by the user on the project (possibly
         *         empty)
         */
        @Query("SELECT p.permission FROM User u JOIN u.userProjectPermissions p "
                        + "WHERE u.userEmail = ?1 AND p.project.projectId = ?2")
        List<String> findProjectPermissions(String userEmail, Long projectId);

        /**
         * Returns the permission labels granted to a user on the project that owns the
         * given audit.
         * <p>
         * Audit-scoped counterpart of {@link #findProjectPermissions}, used by
         * {@code isProjectOwnerByAuditId}.
         *
         * @param userEmail the email of the authenticated user
         * @param auditId   the id of the audit whose owning project is checked
         * @return the permission labels held by the user on that project (possibly
         *         empty)
         */
        @Query("SELECT p.permission FROM User u JOIN u.userProjectPermissions p JOIN p.project pr "
                        + "JOIN pr.audits a WHERE u.userEmail = ?1 AND a.auditId = ?2")
        List<String> findProjectPermissionsByAuditId(String userEmail, Long auditId);

        /**
         * Returns users matching an optional keyword without filtering their account
         * status.
         *
         * @param keyword the keyword to search for in email addresses and usernames
         *                (null to match all users)
         * @return the matching users
         */
        @Query("SELECT u FROM User u "
                        + "WHERE (:keyword IS NULL "
                        + "OR u.userEmail LIKE CONCAT('%', :keyword, '%') "
                        + "OR u.username LIKE CONCAT('%', :keyword, '%'))")
        List<User> findUsersByKeyword(@Param("keyword") String keyword);

        /**
         * Returns active and email-verified users matching an optional keyword.
         *
         * @param keyword the keyword to search for in email addresses and usernames
         *                (null to match all active users)
         * @return the matching active and email-verified users
         */
        @Query("SELECT u FROM User u "
                        + "WHERE (:keyword IS NULL "
                        + "OR u.userEmail LIKE CONCAT('%', :keyword, '%') "
                        + "OR u.username LIKE CONCAT('%', :keyword, '%')) "
                        + "AND u.isUserActive = true AND u.isEmailVerified = true")
        List<User> findActiveUsersByKeyword(@Param("keyword") String keyword);

        /**
         * Returns the user with the specified email address if they are verified and
         * active.
         *
         * @param userEmail the email address of the user to retrieve
         * @return the verified and active user with the specified email address
         */
        @Query("SELECT u FROM User u WHERE ( u.userEmail LIKE %?1% OR u.username LIKE %?1% ) "
                        + "AND u.isEmailVerified = true "
                        + " AND u.isUserActive = true")
        User findByUserEmailAndIsVerified(String userEmail);

        /**
         * Checks if a user with the specified email address or username exists.
         *
         * @param userEmail the email address to check for
         * @return true if a user with the specified email address or username exists,
         *         false otherwise
         */
        boolean existsByUserEmail(String userEmail);

        /**
         * Returns the ids of pending accounts (never activated nor email-verified)
         * whose confirmation
         * window has fully elapsed, i.e. with no {@code confirm-account} token still
         * valid.
         *
         * @param now the reference instant against which token expiry is evaluated
         * @return the ids of the purgeable pending accounts
         */
        @Query("SELECT u.userId FROM User u WHERE u.isUserActive = false AND u.isEmailVerified = false "
                        + "AND NOT EXISTS (SELECT t FROM VerificationToken t WHERE t.user = u "
                        + "AND t.type = 'confirm-account' AND t.expiryDate > :now)")
        List<Long> findExpiredPendingAccountIds(@Param("now") Date now);

        /**
         * Bulk-deletes the users with the specified ids.
         *
         * @param ids the ids of the users to delete
         */
        @Modifying
        @Query("DELETE FROM User u WHERE u.userId IN :ids")
        void deleteAllByUserIdIn(@Param("ids") List<Long> ids);
}
