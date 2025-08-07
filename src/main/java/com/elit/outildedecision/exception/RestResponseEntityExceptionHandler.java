package com.elit.outildedecision.exception;

import com.elit.outildedecision.util.MethodType;
import com.elit.outildedecision.model.ErrorMessage;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A controller advice class that handles exceptions thrown by controllers in
 * the RESTful API.
 * This class provides exception handling methods for specific exceptions and
 * maps them to
 * appropriate HTTP status codes and error messages in the response.
 */
@ControllerAdvice()
@ResponseStatus
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

  /**
   * Handles MethodArgumentNotValidException and returns a response with HTTP
   * status code 400 and
   * a map of field errors as the response body.
   * <p>
   * FIXME : this is an issue (potentially severe) as it exposes the internal
   * working of the API, allowing an attacker to craft malicious parameters to try
   * to gain access to the system or tamper with data.
   * TODO : evaluate relevance to send such data back to front.
   * <p>
   * @param ex      The MethodArgumentNotValidException that was thrown.
   * @param headers The HttpHeaders to be included in the response.
   * @param status  The HttpStatus to be included in the response.
   * @param request The WebRequest to be included in the response.
   * @return A ResponseEntity containing a map of field errors and a HTTP status
   *         code 400.
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {
    Map<String, String> details = new HashMap<>();
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();
    for (FieldError error : fieldErrors) {
      details.put(error.getField(), error.getDefaultMessage());
    }
    return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles multiple exception types and returns a response with an error message
   * and HTTP status code 409 (CONFLICT).
   * <p>
   * The types handled by this method include:
   * <ul>
   * <li>{@link SQLIntegrityConstraintViolationException}</li>
   * <li>{@link ExistingBrainstormingException}</li>
   * <li>{@link FormStepAlreadyExistsException}</li>
   * <li>{@link VoteAlreadyExistException}</li>
   * <li>{@link OpinionAlreadyExistException}</li>
   * <li>{@link PredictibilityAnalysisAlreadyUpdated}</li>
   * <li>{@link ProjectFolderAlreadyExistsException}</li>
   * <li>{@link UserAlreadyExistsException}</li>
   * <li>{@link ExecutionPlanException}</li>
   * </ul>
   * </p>
   *
   * @param ex the Exception to handle.
   * @return a {@code ResponseEntity<ErrorMessage>} with an error message and HTTP
   *         status code 409 (CONFLICT).
   */
  @ExceptionHandler({
      SQLIntegrityConstraintViolationException.class, ExistingBrainstormingException.class,
      FormStepAlreadyExistsException.class,
      VoteAlreadyExistException.class, OpinionAlreadyExistException.class, PredictibilityAnalysisAlreadyUpdated.class,
      ProjectFolderAlreadyExistsException.class, UserAlreadyExistsException.class, ExecutionPlanException.class,
  })
  public ResponseEntity<ErrorMessage> handleConflictException(final Exception ex) {
    LOGGER.error("Conflict Exception occured", ex);
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }

  /**
   * Handles multiple exceptions and returns a response with an error message and
   * HTTP status code 404 (NOT FOUND).
   * <p>
   * The types handled by this method include:
   * <ul>
   * <li>{@link VoteNotFoundException}</li>
   * <li>{@link FeedBackNotFoundException}</li>
   * <li>{@link ProjectNotFoundException}</li>
   * <li>{@link UserNotFoundException}</li>
   * <li>{@link AvatarNotFoundException}</li>
   * <li>{@link ProjectOwnerException}</li>
   * <li>{@link AttachmentNotFoundException}</li>
   * <li>{@link MulticriteriaNotFoundException}</li>
   * <li>{@link PredictibilityAnalysisNotFoundException}</li>
   * <li>{@link BrainstormingNotFoundException}</li>
   * <li>{@link StepFormNotFoundException}</li>
   * <li>{@link ResponseNotFoundException}</li>
   * <li>{@link StepWeightNotFoundException}</li>
   * </ul>
   * </p>
   *
   * @param ex the Exception to handle.
   * @return a {@code ResponseEntity<ErrorMessage>} with an error message and HTTP
   *         status code 404 (NOT FOUND).
   */
  @ExceptionHandler({
      VoteNotFoundException.class, ProjectNotFoundException.class, UserNotFoundException.class,
      AvatarNotFoundException.class,
      ProjectOwnerException.class, AttachmentNotFoundException.class, MulticriteriaNotFoundException.class,
      PredictibilityAnalysisNotFoundException.class, BrainstormingNotFoundException.class,
      StepFormNotFoundException.class,
      ResponseNotFoundException.class, StepWeightNotFoundException.class, FeedBackNotFoundException.class
  })
  public ResponseEntity<ErrorMessage> handleNotFoundException(final Exception ex) {
    LOGGER.error("Not Found Exception occured", ex);
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  /**
   * Handles multiple exceptions and returns a response with an error message and
   * HTTP status code 400 (BAD REQUEST).
   * TODO : evaluate relevance of sending bad request to client
   * <p>
   * The types handled by this method include:
   * <ul>
   * <li>{@link CouldNotSaveUserException}</li>
   * <li>{@link QuestionFormatNotValidException}</li>
   * <li>{@link ResponseNotValidException}</li>
   * </ul>
   * </p>
   * @param ex the Exception to handle.
   * @return a {@code ResponseEntity<ErrorMessage>} with an error message and HTTP
   *         status code 400 (BAD REQUEST).
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      CouldNotSaveUserException.class, QuestionFormatNotValidException.class, ResponseNotValidException.class
  })
  public ResponseEntity<ErrorMessage> handleBadRequestException(final Exception ex) {
    LOGGER.error("Bad Request Exception occured", ex);
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }

  /**
   * Handles EscoreException by returning an error response with HTTP status code
   * 406.
   *
   * @param e          the EscoreException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code
   *         406
   */
  @ExceptionHandler(EscoreException.class)
  public ResponseEntity<ErrorMessage> eScoreException(final EscoreException e,
      final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorMessage);
  }

  /**
   * Handles QuestionsNotFoundException by returning an error response with HTTP
   * status code 302 (FOUND).
   *
   * @param e          the QuestionsNotFoundException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code
   *         302 (FOUND).
   */
  @ExceptionHandler(QuestionsNotFoundException.class)
  public ResponseEntity<ErrorMessage> questionNotFoundException(final QuestionsNotFoundException e,
      final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.FOUND).body(errorMessage);
  }

  /**
   * Handles UserNotVerifiedException by returning an error response with HTTP
   * status code 403.
   *
   * @param ex      the UserNotVerifiedException instance containing the error
   *                message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code
   *         403
   */
  @ExceptionHandler(UserNotVerifiedException.class)
  public ResponseEntity<ErrorMessage> userNotVerifiedException(final UserNotVerifiedException ex,
      final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN, ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
  }

  /**
   * Handles UnsupportedMethodologyTypeException by returning an error response
   * with HTTP status code 400.
   * Provides detailed information about accepted enum values.
   *
   * @param ex      the UnsupportedMethodologyTypeException instance containing
   *                the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code
   *         400 (BAD REQUEST)
   */
  @ExceptionHandler(UnsupportedMethodologyTypeException.class)
  public ResponseEntity<ErrorMessage> handleUnsupportedMethodologyType(
      final UnsupportedMethodologyTypeException ex,
      final WebRequest request) {
    LOGGER.error("Unsupported methodology type error: {}", ex.getMessage());

    String detailedMessage = String.format(
        "%s. Valeurs acceptées: [%s]",
        ex.getMessage(),
        Arrays.stream(MethodType.values())
            .map(Enum::name)
            .collect(Collectors.joining(", ")));

    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, detailedMessage);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }

  /**
   * Handles JSON parsing errors, specifically InvalidFormatException for enum
   * values.
   * Provides user-friendly error messages for enum deserialization failures.
   * Overrides the default Spring behavior to provide better enum error handling.
   *
   * @param ex      the HttpMessageNotReadableException that wraps the JSON
   *                parsing error
   * @param headers the HTTP headers
   * @param status  the HTTP status
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code
   *         400 (BAD REQUEST)
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex,
      final HttpHeaders headers,
      final HttpStatus status,
      final WebRequest request) {

    Throwable cause = ex.getCause();
    if (cause instanceof InvalidFormatException) {
      InvalidFormatException invalidFormatEx = (InvalidFormatException) cause;

      // Check if it's an enum deserialization error
      if (invalidFormatEx.getTargetType() != null && invalidFormatEx.getTargetType().isEnum()) {
        String fieldName = invalidFormatEx.getPath().isEmpty() ? "field"
            : invalidFormatEx.getPath().get(0).getFieldName();
        String invalidValue = String.valueOf(invalidFormatEx.getValue());

        String enumValues = Arrays.stream(invalidFormatEx.getTargetType().getEnumConstants())
            .map(Object::toString)
            .collect(Collectors.joining(", "));

        String errorMessage = String.format(
            "Valeur invalide '%s' pour le champ '%s'. Valeurs acceptées: [%s]",
            invalidValue, fieldName, enumValues);

        LOGGER.error("Enum deserialization error: {}", errorMessage);
        ErrorMessage error = new ErrorMessage(HttpStatus.BAD_REQUEST, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
      }
    }

    // Fallback for other JSON parsing errors
    LOGGER.error("JSON parsing error: {}", ex.getMessage());
    String errorMessage = "Format de données invalide. Veuillez vérifier la structure JSON.";
    ErrorMessage error = new ErrorMessage(HttpStatus.BAD_REQUEST, errorMessage);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Handles any unhandled exception and returns a response with an error message
   * and HTTP status code 500
   * (INTERNAL SERVER ERROR).
   *
   * @param ex the Exception instance containing the error message
   * @return the ResponseEntity containing the error message and HTTP status code
   *         500 (INTERNAL SERVER ERROR).
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleGenericException(final Exception ex) {
    LOGGER.error("Generic Exception occured", ex);
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
  }
}
