package com.elit.outildedecision.exception;

import com.elit.outildedecision.model.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A controller advice class that handles exceptions thrown by controllers in the RESTful API.
 * This class provides exception handling methods for specific exceptions and maps them to
 * appropriate HTTP status codes and error messages in the response.
 */
@ControllerAdvice()
@ResponseStatus
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
  /**
   * Handles MethodArgumentNotValidException and returns a response with HTTP status code 400 and
   * a map of field errors as the response body.
   * @param ex The MethodArgumentNotValidException that was thrown.
   * @param headers The HttpHeaders to be included in the response.
   * @param status The HttpStatus to be included in the response.
   * @param request The WebRequest to be included in the response.
   * @return A ResponseEntity containing a map of field errors and a HTTP status code 400.
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
    return new ResponseEntity<Object>(details, HttpStatus.BAD_REQUEST);
  }
  /**
   * Handles SQLIntegrityConstraintViolationException and returns a response with an error message and HTTP status code 409 (CONFLICT).
   *
   * @param ex      The SQLIntegrityConstraintViolationException to handle.
   * @param request The WebRequest of the request.
   * @return A ResponseEntity with an error message and HTTP status code 409 (CONFLICT).
   */
  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public ResponseEntity<ErrorMessage> handleSQLIntegrityConstraintViolationException(final SQLIntegrityConstraintViolationException ex,
                                                                                     final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }
  /**
   * Handles CouldNotSaveUserException and returns a response with an error message and HTTP status code 400 (BAD REQUEST).
   *
   * @param ex      The CouldNotSaveUserException to handle.
   * @param request The WebRequest of the request.
   * @return A ResponseEntity with an error message and HTTP status code 400 (BAD REQUEST).
   */
  @ExceptionHandler(CouldNotSaveUserException.class)
  public ResponseEntity<ErrorMessage> handleCouldNotSaveUserException(final CouldNotSaveUserException ex,
                                                                      final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }
  /**
   * Handles QuestionFormatNotValidException and returns a response with an error message and HTTP status code 400 (BAD REQUEST).
   *
   * @param ex      The QuestionFormatNotValidException to handle.
   * @param request The WebRequest of the request.
   * @return A ResponseEntity with an error message and HTTP status code 400 (BAD REQUEST).
   */
  @ExceptionHandler(QuestionFormatNotValidException.class)
  public ResponseEntity<ErrorMessage> handleSQLIntegrityConstraintViolationException(final QuestionFormatNotValidException ex,
                                                                                     final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }
  /**
   * Handles ProjectNotFoundException and returns a response with an error message and HTTP status code 404 (NOT FOUND).
   *
   * @param e         The ProjectNotFoundException to handle.
   * @param webRequest The WebRequest of the request.
   * @return A ResponseEntity with an error message and HTTP status code 404 (NOT FOUND).
   */
  @ExceptionHandler(ProjectNotFoundException.class)
  public ResponseEntity<ErrorMessage> projectNotFoundException(final ProjectNotFoundException e,
                                                               final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles UserNotFoundException and returns a response with an error message and HTTP status code 404 (NOT FOUND).
   *
   * @param e         The UserNotFoundException to handle.
   * @param webRequest The WebRequest of the request.
   * @return A ResponseEntity with an error message and HTTP status code 404 (NOT FOUND).
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorMessage> userNotFoundException(final UserNotFoundException e,
                                                            final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles ProjectOwnerException by returning an error response with HTTP status code 404.
   *
   * @param e the ProjectOwnerException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(ProjectOwnerException.class)
  public ResponseEntity<ErrorMessage> projectOwnerException(final ProjectOwnerException e,
                                                            final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles EscoreException by returning an error response with HTTP status code 406.
   *
   * @param e the EscoreException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 406
   */
  @ExceptionHandler(EscoreException.class)
  public ResponseEntity<ErrorMessage> eScoreException(final EscoreException e,
                                                      final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorMessage);
  }
  /**
   * Handles AttachmentNotFoundException by returning an error response with HTTP status code 404.
   *
   * @param e the AttachmentNotFoundException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(AttachmentNotFoundException.class)
  public ResponseEntity<ErrorMessage> attachmentNotFoundException(final AttachmentNotFoundException e,
                                                                  final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles MulticriteriaNotFoundException by returning an error response with HTTP status code 404.
   *
   * @param e the MulticriteriaNotFoundException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(MulticriteriaNotFoundException.class)
  public ResponseEntity<ErrorMessage> multicriteriaNotFoundException(final MulticriteriaNotFoundException e,
                                                                     final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles PredictibilityAnalysisNotFoundException by returning an error response with HTTP status code 404.
   *
   * @param e the PredictibilityAnalysisNotFoundException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(PredictibilityAnalysisNotFoundException.class)
  public ResponseEntity<ErrorMessage> predictibilityAnaliseNotFoundException(final PredictibilityAnalysisNotFoundException e,
                                                                             final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles BrainstormingNotFoundException by returning an error response with HTTP status code 404.
   *
   * @param e the BrainstormingNotFoundException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(BrainstormingNotFoundException.class)
  public ResponseEntity<ErrorMessage> brainstormingNotFoundException(final BrainstormingNotFoundException e,
                                                                     final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles ExistingBrainstormingException by returning an error response with HTTP status code 404.
   *
   * @param e the ExistingBrainstormingException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(ExistingBrainstormingException.class)
  public ResponseEntity<ErrorMessage> existingBrainstormingException(final ExistingBrainstormingException e,
                                                                     final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles QuestionsNotFoundException by returning an error response with HTTP status code 302.
   *
   * @param e the QuestionsNotFoundException instance
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 302
   */
  @ExceptionHandler(QuestionsNotFoundException.class)
  public ResponseEntity<ErrorMessage> questionNotFoundException(final QuestionsNotFoundException e,
                                                                final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.FOUND).body(errorMessage);
  }
  /**
   * Handles StepFormNotFoundException by returning an error response with HTTP status code 404.
   *
   * @param e the StepFormNotFoundException instance containing the error message
   * @param webRequest the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(StepFormNotFoundException.class)
  public ResponseEntity<ErrorMessage> stepFormNotFoundException(final StepFormNotFoundException e,
                                                                final WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles FormStepAlreadyExistsException by returning an error response with HTTP status code 409.
   *
   * @param ex the FormStepAlreadyExistsException instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 409
   */
  @ExceptionHandler(FormStepAlreadyExistsException.class)
  public ResponseEntity<ErrorMessage> formStepAlreadyExistsException(final FormStepAlreadyExistsException ex,
                                                                     final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }
  /**
   * Handles UserNotVerifiedException by returning an error response with HTTP status code 403.
   *
   * @param ex the UserNotVerifiedException instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 403
   */
  @ExceptionHandler(UserNotVerifiedException.class)
  public ResponseEntity<ErrorMessage> userNotVerifiedException(final UserNotVerifiedException ex,
                                                               final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN, ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
  }
  /**
   * Handles OpinionAlreadyExistException by returning an error response with HTTP status code 409.
   *
   * @param ex the OpinionAlreadyExistException instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 409
   */
  @ExceptionHandler(OpinionAlreadyExistException.class)
  public ResponseEntity<ErrorMessage> handleOpinionAlreadyExistException(final OpinionAlreadyExistException ex,
                                                                   final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }
  /**
   * Handles ResponseNotFoundException by returning an error response with HTTP status code 404.
   *
   * @param ex the ResponseNotFoundException instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(ResponseNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleResponseNotFoundException(final ResponseNotFoundException ex,
                                                                final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles ResponseNotValidException by returning an error response with HTTP status code 400.
   *
   * @param ex the ResponseNotValidException instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 400
   */
  @ExceptionHandler(ResponseNotValidException.class)
  public ResponseEntity<ErrorMessage> handleResponseNotValidException(final ResponseNotValidException ex,
                                                                final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }
  /**
   * Handles StepWeightNotFoundException by returning an error response with HTTP status code 404.
   *
   * @param ex the StepWeightNotFoundException instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 404
   */
  @ExceptionHandler(StepWeightNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleStepWeightNotFoundException(final StepWeightNotFoundException ex, final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }
  /**
   * Handles PredictibilityAnalysisAlreadyUpdated by returning an error response with HTTP status code 409.
   *
   * @param ex the PredictibilityAnalysisAlreadyUpdated instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 409
   */
  @ExceptionHandler(PredictibilityAnalysisAlreadyUpdated.class)
  public ResponseEntity<ErrorMessage> handlePredictabilityAnalysisAlreadyUpdated(final PredictibilityAnalysisAlreadyUpdated ex, final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }
  /**
   * Handles ProjectFolderAlreadyExistsException by returning an error response with HTTP status code 409.
   *
   * @param ex the ProjectFolderAlreadyExistsException instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 409
   */
  @ExceptionHandler(ProjectFolderAlreadyExistsException.class)
  public ResponseEntity<ErrorMessage> handleProjectFolderAlreadyExistsException(final ProjectFolderAlreadyExistsException ex,
                                                                  final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }
  /**
   * Handles UserAlreadyExistsException by returning an error response with HTTP status code 409.
   *
   * @param ex the UserAlreadyExistsException instance containing the error message
   * @param request the web request
   * @return the ResponseEntity containing the error message and HTTP status code 409
   */
  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorMessage> handleUserAlreadyExistsException(final UserAlreadyExistsException ex,
                                                                  final WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }
}
