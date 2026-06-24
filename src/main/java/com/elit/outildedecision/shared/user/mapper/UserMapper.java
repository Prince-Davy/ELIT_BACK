package com.elit.outildedecision.shared.user.mapper;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper converting {@link User} entities to {@link UserResponse} DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  /**
   * Maps a user entity to its public response representation.
   * The avatar bytes are resolved by the service (file access) and set afterwards.
   *
   * @param user the user entity to map
   * @return the corresponding {@link UserResponse}
   */
  @Mapping(target = "userAvatar", ignore = true)
  UserResponse toResponse(User user);
}
