package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.Opinion;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.OpinionAlreadyExistException;
import com.elit.outildedecision.model.OpinionModel;
import com.elit.outildedecision.repository.OpinionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.OpinionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * The OpinionServiceImpl class implements the OpinionService interface and provides the
 * implementation of the service methods for managing opinions.
 */
@Service
public class OpinionServiceImpl implements OpinionService {
  /**
   * The userRepository field is used to access the user database.
   */
  private final UserRepository userRepository;
  /**
   * The opinionRepository field is used to access the opinion database.
   */
  private final OpinionRepository opinionRepository;

  /**
   * Constructor for OpinionServiceImpl class.
   *
   * @param userRepository    The UserRepository object.
   * @param opinionRepository The OpinionRepository object.
   */
  public OpinionServiceImpl(final UserRepository userRepository, final OpinionRepository opinionRepository) {
    this.userRepository = userRepository;
    this.opinionRepository = opinionRepository;
  }

  /**
   * Create an opinion for the current user.
   *
   * @param opinionModel The OpinionModel object containing the opinion details.
   * @return A ResponseEntity object with the success message.
   * @throws OpinionAlreadyExistException If the user has already submitted an opinion.
   */
  @Override
  public ResponseEntity<String> createOpinion(final OpinionModel opinionModel)
    throws OpinionAlreadyExistException {
    String userEmail =
      (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    Opinion opinionFromDB = opinionRepository.findByUserId(user.getUserId());
    if (Objects.nonNull(opinionFromDB)) {
      throw new OpinionAlreadyExistException("Cet utilisateur à déjà renseigné un avis.");
    }
    Opinion opinion = new Opinion();
    opinion.setDescription(opinionModel.getDescription());
    opinion.setRating(opinionModel.getRating());
    opinion.setUser(user);
    opinionRepository.save(opinion);

    return ResponseEntity.ok().body("L'avis a bien été enregistré.");
  }

  /**
   * Check if the current user has already submitted an opinion.
   *
   * @return A Boolean value indicating if the user has already submitted an opinion.
   */
  @Override
  public Boolean getOpinionIsAlreadyExist() {
    String userEmail =
      (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User user = userRepository.findByUserEmail(userEmail);

    Opinion opinionFromDB = opinionRepository.findByUserId(user.getUserId());
    return Objects.nonNull(opinionFromDB);
  }
}
