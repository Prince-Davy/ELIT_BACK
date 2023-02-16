package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.*;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.model.VoteModelRequest;
import com.elit.outildedecision.model.VoteModelResponse;
import com.elit.outildedecision.repository.*;
import com.elit.outildedecision.service.VoteService;
import com.elit.outildedecision.util.ProjectPermession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PredictibilityAnalysisRepository predictibilityAnalysisRepository;
    private final ProjectUserPermissionRepository projectUserPermissionRepository;

    public VoteServiceImpl(VoteRepository voteRepository, UserRepository userRepository, ProjectRepository projectRepository, PredictibilityAnalysisRepository predictibilityAnalysisRepository, MultiCriteriaAnalysisRepository multiCriteriaAnalysisRepository, ProjectUserPermissionRepository projectUserPermissionRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.predictibilityAnalysisRepository = predictibilityAnalysisRepository;
        this.projectUserPermissionRepository = projectUserPermissionRepository;
    }

    @Override
    public ResponseEntity<String> createVote(VoteModelRequest voteModelRequest) throws VoteAlreadyExistException {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);

        Vote voteFromDB = voteRepository.findByPredictibilityAnalysisAndUserId(voteModelRequest.getPredictibilityAnalysisId(), user.getUserId());
        if (Objects.nonNull(voteFromDB)) {
            throw new VoteAlreadyExistException("Cet utilisateur à déjà soumis un vote.");
        }
        Vote vote = new Vote();
        vote.setChosenPMM(voteModelRequest.getChosenPMM());
        vote.setJustification(voteModelRequest.getJustification());
        vote.setPredictibilityValue(voteModelRequest.getPredictibilityValue());
        PredictibilityAnalysis analysis = predictibilityAnalysisRepository.findById(voteModelRequest.getPredictibilityAnalysisId())
                .orElseThrow(() -> new RuntimeException("Cet analyse n'existe pas"));
        vote.setPredictibilityAnalysis(analysis);
        vote.setIsConfirmed(voteModelRequest.getIsConfirmed());
        vote.setUser(user);
        voteRepository.save(vote);

        return ResponseEntity
                .ok()
                .body("Le vote a bien été enregistré.");
    }

    public List<String> checkPermissionByProjectIdAndUserId(Long projectId, Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable."));
        List<String> permissions = new ArrayList<>();
        user.getUserProjectPermissions().stream()
                .filter(p -> p.getProject().getProjectId().equals(projectId))
                .forEach(p -> permissions.add(p.getPermission()));
        return permissions;
    }

    @Override
    public List<VoteModelResponse> getVote(Long predictibilityAnalysisId) throws UserNotFoundException {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("userEmail" + userEmail);
        User user = userRepository.findByUserEmail(userEmail);
        System.out.println("user" + user);
        PredictibilityAnalysis predictibilityAnalysis = predictibilityAnalysisRepository.findById(predictibilityAnalysisId)
                .orElseThrow(() -> new RuntimeException("Predictibility Analysis Id non trouvé."));

        Project project = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject();

        List<ProjectUserPermission> projectUserPermission = project.getProjectUserPermissions();

        if (Objects.isNull(projectUserPermission)) {
            throw new UserNotFoundException("Cet utilisateur n'existe pas dans le projet !");
        }
        List<VoteModelResponse> result = new ArrayList<>();

        List<String> userPermission = checkPermissionByProjectIdAndUserId(project.getProjectId(), user.getUserId());
        if (userPermission.contains(ProjectPermession.CDP.name())) {
            List<ProjectUserPermission> usersPermission = projectUserPermissionRepository.findAllByProjectId(project.getProjectId());
            for (ProjectUserPermission userP : usersPermission) {
                if (userP.getPermission().equals(ProjectPermession.CONTRIBUTER.toString()) || userP.getPermission().equals(ProjectPermession.CDP.name())) {
                    Vote userVote = voteRepository.findByPredictibilityAnalysisAndUserId(predictibilityAnalysisId, userP.getUser().getUserId());
                    if (Objects.isNull(userVote)) {
                        result.add(VoteModelResponse.builder()
                                .voteId(null)
                                .isConfirmed(null)
                                .predictibilityAnalysisId(predictibilityAnalysisId)
                                .justification(null)
                                .chosenPMM(null)
                                .predictibilityValue(null)
                                .userId(userP.getUser().getUserId())
                                .build());
                    } else {
                        result.add(VoteModelResponse.builder()
                                .voteId(userVote.getVoteId())
                                .isConfirmed(userVote.getIsConfirmed())
                                .predictibilityAnalysisId(predictibilityAnalysisId)
                                .justification(userVote.getJustification())
                                .chosenPMM(userVote.getChosenPMM())
                                .predictibilityValue(userVote.getPredictibilityValue())
                                .userId(userP.getUser().getUserId())
                                .build());
                    }
                }
            }
        } else {
            Vote vote = voteRepository.findByPredictibilityAnalysisAndUserId(predictibilityAnalysisId, user.getUserId());
            if (Objects.isNull(vote)) {
                VoteModelResponse voteUser = VoteModelResponse.builder()
                        .voteId(null)
                        .isConfirmed(null)
                        .justification(null)
                        .chosenPMM(null)
                        .predictibilityValue(null)
                        .predictibilityAnalysisId(predictibilityAnalysisId)
                        .userId(user.getUserId())
                        .build();

                result.add(voteUser);
                return result;
            } else {
                result.add(new VoteModelResponse(vote.getVoteId(), vote.getChosenPMM(), vote.getJustification(), vote.getPredictibilityValue(), predictibilityAnalysisId, vote.getIsConfirmed(), vote.getUser().getUserId()));
            }
        }
        return result;
    }

    @Override
    public ResponseEntity<String> updateVote(Long voteId, VoteModelRequest voteModelRequest) {
        String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUserEmail(userEmail);

        Vote voteFromDB = voteRepository.findById(voteId)
                .orElseThrow(() -> new RuntimeException("Ce vote n'existe pas!"));

        PredictibilityAnalysis predictibilityAnalysis = voteFromDB.getPredictibilityAnalysis();
        Long projectId = predictibilityAnalysis.getMultiCriteriaAnalysis().getProject().getProjectId();

        ProjectUserPermission projectUserPermission = projectUserPermissionRepository.findByProjectIdUserID(projectId, user.getUserId());

        if (user.getUserId() == voteFromDB.getUser().getUserId() || projectUserPermission.getPermission().contains(ProjectPermession.CDP.name())) {
            voteFromDB.setPredictibilityValue(voteModelRequest.getPredictibilityValue());
            voteFromDB.setChosenPMM(voteModelRequest.getChosenPMM());
            voteFromDB.setJustification(voteModelRequest.getJustification());
            voteRepository.save(voteFromDB);
            return ResponseEntity.status(HttpStatus.OK).body("Vote mis à jour.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Vous n'êtes pas autorisé à modifier ce vote.");
        }
    }
}
