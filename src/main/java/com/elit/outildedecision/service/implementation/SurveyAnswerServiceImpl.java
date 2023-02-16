package com.elit.outildedecision.service.implementation;

import com.elit.outildedecision.entity.SurveyAnswer;
import com.elit.outildedecision.entity.SurveyQuestion;
import com.elit.outildedecision.entity.User;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResponseNotValidException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.model.SurveyAnswerRequestModel;
import com.elit.outildedecision.model.SurveyAnswerResponseModel;
import com.elit.outildedecision.repository.SurveyAnswerRepository;
import com.elit.outildedecision.repository.SurveyQuestionRepository;
import com.elit.outildedecision.repository.UserRepository;
import com.elit.outildedecision.service.SurveyAnswerService;
import com.elit.outildedecision.util.AnswerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * class that allows to manage survey responses.
 * Contains methods to answer to the survey, to get user answers and to get the complete survey answers.
 */
@Service("SurveyAnswerServiceImpl")
public class SurveyAnswerServiceImpl implements SurveyAnswerService {

    private final SurveyAnswerRepository answerRepo;
    private final SurveyQuestionRepository questionRepository;
    private final UserRepository userRepository;

    public SurveyAnswerServiceImpl(SurveyAnswerRepository answerRepo, SurveyQuestionRepository questionRepository, UserRepository userRepository) {
        this.answerRepo = answerRepo;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    /**
     * method used to answer to a survey
     * @param userAnswerList represents a list of questions that user will respond on
     * @return a list of questions with their answers.
     */
    @Override
    @Transactional
    public ResponseEntity<String> replyToQuestion(List<SurveyAnswerRequestModel> userAnswerList) throws UserNotFoundException, QuestionsNotFoundException, ResponseNotFoundException, ResponseNotValidException {

        for (SurveyAnswerRequestModel userAnswer : userAnswerList) {
            SurveyAnswer response = new SurveyAnswer();
            User user = userRepository.findById(userAnswer.getUserId()).orElseThrow(() -> new UserNotFoundException("Cet utilisateur n'existe pas"));
            SurveyQuestion question = questionRepository.findById(userAnswer.getQuestionId()).orElseThrow(() -> new QuestionsNotFoundException("questionnaire non existant"));
            //sauvegarde de la réponse dans base de données :
            response.setUser(user);
            response.setQuestion(question);

            AnswerType answerType = userAnswer.getAnswerType();
            switch (answerType) {
                case SELECTED_ANSWER :
                    if (isAnswerValidated(userAnswer.getAnswerValue())) {
                        response.setType(AnswerType.SELECTED_ANSWER);
                        response.setAnswerValue(userAnswer.getAnswerValue());
                        break;
                    }
                    throw new ResponseNotValidException("la réponse doit être un entier compris entre 1 et 5");
                case FREE_ANSWER :
                    response.setType(AnswerType.FREE_ANSWER);
                    response.setAnswerValue(userAnswer.getAnswerValue());
                    break;
                default:
                    throw new ResponseNotFoundException("reponse manquante");
            }
            answerRepo.save(response);
        }
        return ResponseEntity.ok().body("reponses enregistrées avec succès");
    }


    private boolean isAnswerValidated(String answerValue) throws ResponseNotValidException {
        try {
            int value = Integer.parseInt(answerValue);
            if (value < 1 || value > 5) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new ResponseNotValidException("la réponse doit être un entier compris entre 1 et 5");
        }
    }


    /**
     * method used to display answers for one user
     * @param userId given in parameter and represents user for whom is asked to display survey answers
     * @return a list of user answers
     */
    @Override
    public List<SurveyAnswerResponseModel> getUserAnswer(Long userId) throws UserNotFoundException, ResponseNotFoundException {

        List<SurveyAnswerResponseModel> userAnswerList = new ArrayList<>();

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Utilisateur non identifiable");
        }

        List<SurveyAnswer> answers = answerRepo.findAll().stream()
                .filter(e -> e.getUser().getUserId().equals(userId))
                .collect(Collectors.toList());

        for (SurveyAnswer answer : answers) {
            SurveyAnswerResponseModel surveyAnswerResponseModel = new SurveyAnswerResponseModel();
            surveyAnswerResponseModel.setUserId(userId);
            surveyAnswerResponseModel.setQuestionId(answer.getQuestion().getId());
            surveyAnswerResponseModel.setAnswer(answer.getAnswerValue());
            userAnswerList.add(surveyAnswerResponseModel);
        }
        return userAnswerList;
    }

    /**
     * method used to display all result of survey
     *
     * @return a list of survey result
     */
    @Override
    public List<SurveyAnswerResponseModel> getSurveyReport(int pageNumber) {
        Page<SurveyAnswer>  surveyAnswers = answerRepo.findAll(PageRequest.of(pageNumber - 1, 10)); // je récupère toutes mes réponse
        List<SurveyAnswerResponseModel> surveyAnswerResponseModelList = new ArrayList<>();
        List<SurveyAnswer> surveyAnswersList = surveyAnswers.getContent();
        for (SurveyAnswer surveyAnswer : surveyAnswersList) {
            SurveyAnswerResponseModel surveyAnswerResponseModel = new SurveyAnswerResponseModel(); // je crée un nouvel object de mon model
//            surveyAnswerResponseModel.setAnswer(surveyAnswer.getAnswer()); // je viens lui attribuer une réponse

            surveyAnswerResponseModel.setQuestionId(surveyAnswer.getQuestion().getId()); // je viens lui attribuer l'id d'une question
            surveyAnswerResponseModel.setUserId(surveyAnswer.getUser().getUserId()); // je viens lui attribuer l'id d'un user
            surveyAnswerResponseModel.setAnswer(surveyAnswer.getAnswerValue());

            surveyAnswerResponseModelList.add(surveyAnswerResponseModel); // j'ajoute ce nouvel object dans mon tableau
        }
        return surveyAnswerResponseModelList; // je retourne mon tableau avec mes différents objects.
    }
}
