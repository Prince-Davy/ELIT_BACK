package com.elit.outildedecision.shared.user.repository;

import com.elit.outildedecision.shared.user.entity.UserAvatarContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the binary content of user avatars ({@code user_avatar_content} table),
 * keyed by user id.
 */
@Repository
public interface UserAvatarContentRepository extends JpaRepository<UserAvatarContent, Long> {
}
