package com.elit.outildedecision.shared.exception.handler;

import com.elit.outildedecision.shared.exception.ApiException;
import com.elit.outildedecision.shared.exception.ErrorMessage;
import com.elit.outildedecision.shared.util.ReportTypes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global controller advice mapping exceptions to HTTP responses.
 *
 * <p>Business errors extend {@link ApiException} and are mapped generically from the status
 * they carry, so this handler imports no concrete feature exception (plan §6, Étape 5bis).
 * Cross-cutting framework exceptions (validation, security, JSON/enum parsing,
 * {@link ResponseStatusException}, integrity violations and the catch-all 500) keep dedicated
 * handlers.
 */
@ControllerAdvice()
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this handler is used before any other
@ResponseStatus
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

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
      log.error("Failed to serialize error message", e);
      return ResponseEntity.status(status).body(errorMessage);
    }
  }

  /**
   * Maps any business {@link ApiException} to the HTTP status it carries.
   *
   * <p>Detailed/dynamic messages (e.g. accepted enum values) are assembled upstream in the
   * exception constructor or service, not here, so this handler stays generic.
   *
   * @param ex the business exception that was thrown
   * @return ResponseEntity containing the error payload with the exception's HTTP status
   */
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Object> handleApiException(final ApiException ex) {
    log.warn("API exception [{}]: {}", ex.getStatus().value(), ex.getMessage());
    return build(ex.getStatus(), sanitize(ex.getMessage()));
  }

  /**
   * Handles MethodArgumentNotValidException and returns a response with HTTP
   * status code 400 and a compact summary of the field errors as the response body.
   *
   * <p>The per-field validation messages are part of the front-end contract (they are displayed
   * inline next to the offending input), so they are returned intentionally; the payload exposes
   * only field names and validation messages, never internal state.
   *
   * @param ex      The MethodArgumentNotValidException that was thrown.
   * @param headers The HttpHeaders to be included in the response.
   * @param status  The HttpStatus to be included in the response.
   * @param request The WebRequest to be included in the response.
   * @return A ResponseEntity containing a map of field errors and a HTTP status
   *         code 400.
   */
  // S2638: annotation-only nullability mismatch with the overridden Spring method, runtime behaviour unchanged.
  @SuppressWarnings("java:S2638")
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
   * Handles database integrity violations and returns HTTP 409 (CONFLICT).
   *
   * @param ex the SQLIntegrityConstraintViolationException to handle
   * @return ResponseEntity containing an error payload with HTTP status 409
   */
  @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
  public ResponseEntity<Object> handleConflictException(final SQLIntegrityConstraintViolationException ex) {
    log.error("Conflict Exception occured", ex);
    return build(HttpStatus.CONFLICT, sanitize(ex.getMessage()));
  }

  /**
   * Handles method-level validation failures (@Validated on controller parameters, e.g. body
   * constraints checked outside the @Valid model binding) and returns HTTP 400.
   *
   * <p>Like {@link #handleMethodArgumentNotValid}, the per-violation messages are part of the
   * front-end contract, so a compact {@code path: message} summary is returned.
   *
   * @param ex the ConstraintViolationException that was thrown
   * @return ResponseEntity containing the validation error payload with HTTP status 400
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex) {
    String message = ex.getConstraintViolations().stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .collect(Collectors.joining("; "));
    if (message.isEmpty()) {
      message = "Validation error";
    }
    log.warn("Constraint violation: {}", message);
    return build(HttpStatus.BAD_REQUEST, sanitize(message));
  }

  /**
   * Handle authentication errors (401 Unauthorized).
   *
   * @param ex the AuthenticationException that was thrown
   * @return ResponseEntity containing the error payload with HTTP status 401
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Object> handleAuthenticationException(final AuthenticationException ex) {
    log.warn("Authentication failed: {}", ex.getMessage());
    return build(HttpStatus.UNAUTHORIZED, sanitize(ex.getMessage()));
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
  // S2638: annotation-only nullability mismatch with the overridden Spring method, runtime behaviour unchanged.
  @SuppressWarnings("java:S2638")
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

        log.warn("Enum deserialization error: {}", errorMessage);
        return build(HttpStatus.BAD_REQUEST, sanitize(errorMessage));
      }
    }

    // Fallback for other JSON parsing errors
    log.warn("JSON parsing error: {}", ex.getMessage());
    String errorMessage = "Format de données invalide. Veuillez vérifier la structure JSON.";
    return build(HttpStatus.BAD_REQUEST, errorMessage);
  }

  /**
   * Maps invalid path variable values (including report types) to HTTP 400.
   *
   * @param ex the parameter conversion failure
   * @return a bad request response using the shared error format
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handlePathVariableTypeMismatch(
      final MethodArgumentTypeMismatchException ex) {
    String message = "Valeur invalide pour le parametre '" + ex.getName() + "'.";
    if ("type".equals(ex.getName())
        && ex.getRequiredType() == ReportTypes.class) {
      message = "Type de rapport inconnu : " + ex.getValue();
    }
    return build(HttpStatus.BAD_REQUEST, message);
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
    log.error("Unexpected error: {}", ex.getMessage(), ex);
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
    log.warn("Access denied: {}", ex.getMessage());
    return build(HttpStatus.FORBIDDEN, "Access denied: you are not allowed to perform this action");
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
    log.warn("ResponseStatusException: {} - {}", status.value(), reason);
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
