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

@Service
public class OpinionServiceImpl implements OpinionService {

    private final UserRepository userRepository;

    private final OpinionRepository opinionRepository;

    public OpinionServiceImpl(UserRepository userRepository, OpinionRepository opinionRepository) {
        this.userRepository = userRepository;
        this.opinionRepository = opinionRepository;
    }

    @Override
    public ResponseEntity<String> createOpinion(OpinionModel opinionModel) throws OpinionAlreadyExistException {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);

        Opinion opinionFromDB = opinionRepository.findByUserId(user.getUserId());
        if (Objects.nonNull(opinionFromDB)) throw new OpinionAlreadyExistException("Cet utilisateur à déjà renseigné un avis.");
        Opinion opinion = new Opinion();
        opinion.setDescription(opinionModel.getDescription());
        opinion.setRating(opinionModel.getRating());
        opinion.setUser(user);
        opinionRepository.save(opinion);

        return ResponseEntity
                .ok()
                .body("L'avis a bien été enregistré.");
    }

    @Override
    public Boolean getOpinionIsAlreadyExist() {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);

        Opinion opinionFromDB = opinionRepository.findByUserId(user.getUserId());
        return Objects.nonNull(opinionFromDB);
    }


}
