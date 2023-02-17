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

@ControllerAdvice()
@ResponseStatus
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return ResponseEntity<Object>
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    Map<String, String> details = new HashMap<>();
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();
    for (FieldError error : fieldErrors) {
      details.put(error.getField(), error.getDefaultMessage());
    }
    return new ResponseEntity(details, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public ResponseEntity<ErrorMessage> handleSQLIntegrityConstraintViolationException(
      SQLIntegrityConstraintViolationException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }

  @ExceptionHandler(CouldNotSaveUserException.class)
  public ResponseEntity<ErrorMessage> handleCouldNotSaveUserException(
      CouldNotSaveUserException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }

  @ExceptionHandler(QuestionFormatNotValidException.class)
  public ResponseEntity<ErrorMessage> handleSQLIntegrityConstraintViolationException(
      QuestionFormatNotValidException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }

  @ExceptionHandler(ProjectNotFoundException.class)
  public ResponseEntity<ErrorMessage> projectNotFoundException(
      ProjectNotFoundException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorMessage> userNotFoundException(
      UserNotFoundException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(ProjectOwnerException.class)
  public ResponseEntity<ErrorMessage> projectOwnerException(
      ProjectOwnerException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(EscoreException.class)
  public ResponseEntity<ErrorMessage> eScoreException(EscoreException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorMessage);
  }

  @ExceptionHandler(AttachmentNotFoundException.class)
  public ResponseEntity<ErrorMessage> attachmentNotFoundException(
      AttachmentNotFoundException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(MulticriteriaNotFoundException.class)
  public ResponseEntity<ErrorMessage> multicriteriaNotFoundException(
      MulticriteriaNotFoundException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(PredictibilityAnaliseNotFoundException.class)
  public ResponseEntity<ErrorMessage> predictibilityAnaliseNotFoundException(
      PredictibilityAnaliseNotFoundException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(BrainstormingNotFoundException.class)
  public ResponseEntity<ErrorMessage> brainstormingNotFoundException(
      BrainstormingNotFoundException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(ExistingBrainstormingException.class)
  public ResponseEntity<ErrorMessage> existingBrainstormingException(
      ExistingBrainstormingException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(FormAlreadyExistsException.class)
  public ResponseEntity<ErrorMessage> formAlreadyExistsException(
      FormAlreadyExistsException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.FOUND).body(errorMessage);
  }

  @ExceptionHandler(QuestionsNotFoundException.class)
  public ResponseEntity<ErrorMessage> questionNotFoundException(
      QuestionsNotFoundException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.FOUND).body(errorMessage);
  }

  @ExceptionHandler(StepFormNotFoundException.class)
  public ResponseEntity<ErrorMessage> stepFormNotFoundException(
      StepFormNotFoundException e, WebRequest webRequest) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(FormStepAlreadyExistsException.class)
  public ResponseEntity<ErrorMessage> formStepAlreadyExistsException(
      FormStepAlreadyExistsException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }

  @ExceptionHandler(UserNotVerifiedException.class)
  public ResponseEntity<ErrorMessage> userNotVerifiedException(
      UserNotVerifiedException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN, ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
  }

  @ExceptionHandler(UserNotActiveException.class)
  public ResponseEntity<ErrorMessage> userNotActiveException(
      UserNotActiveException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN, ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
  }

  @ExceptionHandler(OpinionAlreadyExistException.class)
  public ResponseEntity<ErrorMessage> opinionAlreadyExistException(
      OpinionAlreadyExistException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.CONFLICT, ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
  }

  @ExceptionHandler(ResponseNotFoundException.class)
  public ResponseEntity<ErrorMessage> responseNotFoundException(
      ResponseNotFoundException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
  }

  @ExceptionHandler(ResponseNotValidException.class)
  public ResponseEntity<ErrorMessage> responseNotValidException(
      ResponseNotValidException ex, WebRequest request) {
    ErrorMessage errorMessage = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
  }
}
