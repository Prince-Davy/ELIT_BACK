package com.elit.outildedecision.exception.handler;

import com.elit.outildedecision.ai.answer.exception.AiAnswerNotFoundException;
import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import com.elit.outildedecision.exception.AttachmentNotFoundException;
import com.elit.outildedecision.exception.AvatarNotFoundException;
import com.elit.outildedecision.exception.BrainstormingNotFoundException;
import com.elit.outildedecision.exception.CouldNotSaveUserException;
import com.elit.outildedecision.exception.EscoreException;
import com.elit.outildedecision.exception.ExecutionPlanException;
import com.elit.outildedecision.exception.ExistingBrainstormingException;
import com.elit.outildedecision.exception.FeedBackNotFoundException;
import com.elit.outildedecision.exception.FormStepAlreadyExistsException;
import com.elit.outildedecision.exception.MulticriteriaNotFoundException;
import com.elit.outildedecision.exception.OpinionAlreadyExistException;
import com.elit.outildedecision.exception.PredictibilityAnalysisAlreadyUpdated;
import com.elit.outildedecision.exception.PredictibilityAnalysisNotFoundException;
import com.elit.outildedecision.exception.ProjectFolderAlreadyExistsException;
import com.elit.outildedecision.exception.ProjectNotFoundException;
import com.elit.outildedecision.exception.ProjectOwnerException;
import com.elit.outildedecision.exception.QuestionsNotFoundException;
import com.elit.outildedecision.exception.ReportNotFoundException;
import com.elit.outildedecision.exception.ResponseNotFoundException;
import com.elit.outildedecision.exception.ResponseNotValidException;
import com.elit.outildedecision.exception.ResultAiNotFoundException;
import com.elit.outildedecision.exception.ResultAlreadyExistsException;
import com.elit.outildedecision.exception.QuestionFormatNotValidException;
import com.elit.outildedecision.exception.StepFormNotFoundException;
import com.elit.outildedecision.exception.StepWeightNotFoundException;
import com.elit.outildedecision.exception.UnsupportedMethodologyTypeException;
import com.elit.outildedecision.exception.UserAlreadyExistsException;
import com.elit.outildedecision.exception.UserNotFoundException;
import com.elit.outildedecision.exception.UserNotVerifiedException;
import com.elit.outildedecision.exception.VoteAlreadyExistException;
import com.elit.outildedecision.exception.VoteNotFoundException;
import com.elit.outildedecision.model.ErrorMessage;
import com.elit.outildedecision.util.MethodType;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.MediaType;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * A controller advice class that handles exceptions thrown by controllers in
 * the RESTful API.
 * This class provides exception handling methods for specific exceptions and
 * maps them to
 * appropriate HTTP status codes and error messages in the response.
 */
@ControllerAdvice()
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this handler is used before any other
@ResponseStatus
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

  // ObjectMapper configured to pretty-print only for error payloads
  private static final ObjectMapper PRETTY_MAPPER = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  // Helper to build ErrorMessage consistently and return pretty JSON for errors
  private ResponseEntity<Object> build(final HttpStatus status, final String message) {
    ErrorMessage errorMessage = new ErrorMessage(status, message);
    try {
      String pretty = PRETTY_MAPPER.writeValueAsString(errorMessage);
      return ResponseEntity.status(status)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(pretty);
    } catch (JsonProcessingException e) {
      // Fallback to object body if serialization fails
      LOGGER.error("Failed to serialize error message", e);
      return ResponseEntity.status(status).body(errorMessage);
    }
  }

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
      final HttpStatusCode status,
      final WebRequest request) {
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();
    // Build a compact message: field1: message1; field2: message2 ...
    String message = fieldErrors.stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining("; "));
    if (message.isEmpty()) {
      message = "Validation error";
    }
    return build(HttpStatus.BAD_REQUEST, sanitize(message));
  }

  /**
   * Handles multiple exception types and returns a response with an error message
   * and HTTP status code 409 (CONFLICT).
   *
   * @param ex the Exception to handle
   * @return ResponseEntity containing an error payload with HTTP status 409
   */
  @ExceptionHandler({
      SQLIntegrityConstraintViolationException.class, ExistingBrainstormingException.class,
      FormStepAlreadyExistsException.class,
      VoteAlreadyExistException.class, OpinionAlreadyExistException.class, PredictibilityAnalysisAlreadyUpdated.class,
      ProjectFolderAlreadyExistsException.class, UserAlreadyExistsException.class, ExecutionPlanException.class,
  })
  public ResponseEntity<Object> handleConflictException(final Exception ex) {
     LOGGER.error("Conflict Exception occured", ex);
    return build(HttpStatus.CONFLICT, sanitize(ex.getMessage()));
  }

  /**
   * Handles multiple 'not found' exceptions and returns a response with HTTP
   * status 404.
   *
   * @param ex the Exception to handle
   * @return ResponseEntity containing an error payload with HTTP status 404
   */
  @ExceptionHandler({
      VoteNotFoundException.class, ProjectNotFoundException.class, UserNotFoundException.class,
      AvatarNotFoundException.class,
      ProjectOwnerException.class, AttachmentNotFoundException.class, MulticriteriaNotFoundException.class,
      PredictibilityAnalysisNotFoundException.class, BrainstormingNotFoundException.class,
      StepFormNotFoundException.class,
      ResponseNotFoundException.class, StepWeightNotFoundException.class, FeedBackNotFoundException.class,
      ResultAiNotFoundException.class, AiAnswerNotFoundException.class, ReportNotFoundException.class
  })
  public ResponseEntity<Object> handleNotFoundException(final Exception ex) {
     // Log at WARN to reduce noise, client will receive a clean error payload
     LOGGER.warn("Not Found: {}", ex.getMessage());
    return build(HttpStatus.NOT_FOUND, sanitize(ex.getMessage()));
  }

  /**
   * Handles multiple bad request type exceptions and returns HTTP 400.
   *
   * @param ex the Exception to handle
   * @return ResponseEntity containing an error payload with HTTP status 400
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      CouldNotSaveUserException.class, QuestionFormatNotValidException.class, ResponseNotValidException.class
  })
  public ResponseEntity<Object> handleBadRequestException(final Exception ex) {
    LOGGER.warn("Bad Request: {}", ex.getMessage());
    return build(HttpStatus.BAD_REQUEST, sanitize(ex.getMessage()));
  }

  /**
   * Handles EscoreException and returns HTTP 406 (Not Acceptable).
   *
   * @param e the EscoreException instance
   * @param webRequest the WebRequest (unused)
   * @return ResponseEntity containing an error payload with HTTP status 406
   */
  @ExceptionHandler(EscoreException.class)
  public ResponseEntity<Object> eScoreException(final EscoreException e,
      final WebRequest webRequest) {
    LOGGER.warn("Not acceptable: {}", e.getMessage());
    return build(HttpStatus.NOT_ACCEPTABLE, sanitize(e.getMessage()));
  }

  /**
   * Handle authentication errors (401 Unauthorized).
   *
   * @param ex the AuthenticationException that was thrown
   * @return ResponseEntity containing the error payload with HTTP status 401
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Object> handleAuthenticationException(final AuthenticationException ex) {
    LOGGER.warn("Authentication failed: {}", ex.getMessage());
    return build(HttpStatus.UNAUTHORIZED, sanitize(ex.getMessage()));
  }

  /**
   * Handle the case where an AI analysis result already exists for a project.
   * Returns HTTP 401 as requested by the caller (though 409 CONFLICT is more
   * semantically appropriate for "already exists").
   *
   * @param ex the ResultAlreadyExistsException that was thrown
   * @return ResponseEntity containing the error payload with HTTP status 401
   */
  @ExceptionHandler(ResultAlreadyExistsException.class)
  public ResponseEntity<Object> handleResultAlreadyExists(final ResultAlreadyExistsException ex) {
    LOGGER.warn("Result already exists: {}", ex.getMessage());
    // Retourner 401 selon demande ; normalement 409 serait plus sémantique mais on suit la demande
    return build(HttpStatus.UNAUTHORIZED, sanitize(ex.getMessage()));
  }

  /**
   * Handles QuestionsNotFoundException by returning an error response with HTTP
   * status code 302 (FOUND).
   *
   * @param e the QuestionsNotFoundException instance
   * @param webRequest the WebRequest (unused)
   * @return ResponseEntity containing the error payload with HTTP status 302
   */
  @ExceptionHandler(QuestionsNotFoundException.class)
  public ResponseEntity<Object> questionNotFoundException(final QuestionsNotFoundException e,
      final WebRequest webRequest) {
    LOGGER.warn("Questions not found: {}", e.getMessage());
    return build(HttpStatus.FOUND, sanitize(e.getMessage()));
  }

  /**
   * Handles UserNotVerifiedException by returning an error response with HTTP
   * status code 403.
   *
   * @param ex the UserNotVerifiedException instance containing the error message
   * @param request the WebRequest (unused)
   * @return ResponseEntity containing the error payload with HTTP status 403
   */
  @ExceptionHandler(UserNotVerifiedException.class)
  public ResponseEntity<Object> userNotVerifiedException(final UserNotVerifiedException ex,
      final WebRequest request) {
    LOGGER.warn("User not verified: {}", ex.getMessage());
    return build(HttpStatus.FORBIDDEN, sanitize(ex.getMessage()));
  }

  /**
   * Handles UnsupportedMethodologyTypeException by returning an error response
   * with HTTP status code 400. Provides detailed information about accepted enum
   * values.
   *
   * @param ex the UnsupportedMethodologyTypeException instance containing the error message
   * @param request the WebRequest (unused)
   * @return ResponseEntity containing the error payload with HTTP status 400
   */
  @ExceptionHandler(UnsupportedMethodologyTypeException.class)
  public ResponseEntity<Object> handleUnsupportedMethodologyType(
      final UnsupportedMethodologyTypeException ex,
      final WebRequest request) {
     LOGGER.error("Unsupported methodology type error: {}", ex.getMessage());

     String detailedMessage = String.format(
         "%s. Valeurs acceptées: [%s]",
         ex.getMessage(),
         Arrays.stream(MethodType.values())
             .map(Enum::name)
             .collect(Collectors.joining(", ")));

    return build(HttpStatus.BAD_REQUEST, sanitize(detailedMessage));
  }

  /**
   * Handles JSON parsing errors, specifically InvalidFormatException for enum
   * values. Provides user-friendly error messages for enum deserialization
   * failures. Overrides the default Spring behavior to provide better enum error
   * handling.
   *
   * @param ex the HttpMessageNotReadableException that wraps the JSON parsing error
   * @param headers the HTTP headers
   * @param status the HTTP status
   * @param request the WebRequest (unused)
   * @return ResponseEntity containing the error payload with HTTP status 400
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex,
      final HttpHeaders headers,
      final HttpStatusCode status,
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

        LOGGER.warn("Enum deserialization error: {}", errorMessage);
        return build(HttpStatus.BAD_REQUEST, sanitize(errorMessage));
      }
    }

    // Fallback for other JSON parsing errors
    LOGGER.warn("JSON parsing error: {}", ex.getMessage());
    String errorMessage = "Format de données invalide. Veuillez vérifier la structure JSON.";
    return build(HttpStatus.BAD_REQUEST, errorMessage);
  }

  /**
   * Handles any unhandled exception and returns a response with an error message
   * and HTTP status code 500 (INTERNAL SERVER ERROR).
   *
   * @param ex the Exception instance containing the error message
   * @return ResponseEntity containing the error payload with HTTP status 500
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGenericException(final Exception ex) {
    // For unexpected exceptions we keep full stack trace in logs but return a safe message
    LOGGER.error("Unexpected error: {}", ex.getMessage(), ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.");
  }

  /**
   * Handles AccessDeniedException and returns a response with HTTP status code 403
   * (FORBIDDEN).
   *
   * @param ex the AccessDeniedException that was thrown
   * @return ResponseEntity containing the error payload with HTTP status 403
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDenied(final AccessDeniedException ex) {
    LOGGER.warn("Access denied: {}", ex.getMessage());
    return build(HttpStatus.FORBIDDEN, "Access denied: you are not allowed to perform this action");
  }

  /**
   * Handles UnsupportedModelTypeException and returns a response with HTTP status
   * code 422 (UNPROCESSABLE ENTITY).
   *
   * @param ex the UnsupportedModelTypeException that was thrown
   * @return ResponseEntity containing the error payload with HTTP status 422
   */
  @ExceptionHandler(UnsupportedModelTypeException.class)
  public ResponseEntity<Object> handleUnsupportedModelType(final UnsupportedModelTypeException ex) {
    LOGGER.info("Unsupported model type: {}", ex.getMessage());
    return build(HttpStatus.UNPROCESSABLE_ENTITY, sanitize(ex.getMessage()));
  }

  /**
   * Handles ResponseStatusException and maps it to the appropriate HTTP status
   * code and error message.
   *
   * @param ex the ResponseStatusException that was thrown
   * @return ResponseEntity containing the error payload with the corresponding HTTP status
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Object> handleResponseStatus(final ResponseStatusException ex) {
     HttpStatus status;
     try {
       status = (HttpStatus) ex.getStatusCode();
     } catch (ClassCastException e) {
       status = HttpStatus.BAD_REQUEST;
     }
    // ResponseStatusException often contains a message like: "404 NOT_FOUND \"...\""
    // Use getReason() if present, otherwise sanitize the exception message to avoid duplicating status name.
    String reason = ex.getReason();
    if (reason == null || reason.isBlank()) {
      // fall back to the message but strip any leading status text (e.g. "404 NOT_FOUND \"")
      reason = sanitize(ex.getMessage());
    }
    LOGGER.warn("ResponseStatusException: {} - {}", status.value(), reason);
    return build(status, reason);
  }

  // Remove HTTP status prefix if present (e.g. "404 NOT_FOUND \"No AI answers...\"")
   private String sanitize(final String raw) {
    if (raw == null) {
      return "";
    }
    String s = raw.trim();

    // Common pattern produced by ResponseStatusException or similar:
    // 404 NOT_FOUND "Message here"
    // 422 UNPROCESSABLE_ENTITY: Message here
    // 400 "Message here"
    // We want to extract only the readable message.

    // If there's a quoted substring, prefer the quoted content (covers cases with double quotes).
    int firstQuote = s.indexOf('"');
    int lastQuote = s.lastIndexOf('"');
    if (firstQuote >= 0 && lastQuote > firstQuote) {
      return s.substring(firstQuote + 1, lastQuote).trim();
    }

    // If the string starts with a 3-digit HTTP status code, remove the leading code and optional status name.
    // This avoids stripping meaningful leading words like "AI" from messages.
    if (s.matches("^\\s*\\d{3}.*")) {
      // Regex explanation:
      // ^\s*           - optional leading whitespace
      // (?:\d{3})?     - optional 3-digit HTTP code (we already matched presence)
      // [\s:_-]*       - separators (space, colon, underscore, dash)
      // (?:[A-Z_]+)?    - optional status name in caps with underscores
      // [\s:\-]*      - optional separators after status word
      String cleaned = s.replaceFirst("^\\s*(?:\\d{3})?[\\s:_-]*(?:[A-Z_]+(?=[\\s:_-]))?[\\s:-]*", "").trim();
      if (cleaned.isEmpty()) {
        return s;
      }
      return cleaned;
    }

    // If it does not start with a numeric status, keep the string as-is (do not remove leading all-caps words like "AI").
    return s;
  }
 }
