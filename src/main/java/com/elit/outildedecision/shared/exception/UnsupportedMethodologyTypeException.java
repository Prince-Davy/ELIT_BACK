package com.elit.outildedecision.shared.exception;

import com.elit.outildedecision.shared.util.MethodType;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

/**
 * Exception mapped to HTTP 400 when a methodology cannot be converted to a known type.
 *
 * <p>The accepted enum values are appended to the message here (instead of in the global
 * handler) so the handler stays generic (plan §6, Étape 5bis).
 */
public class UnsupportedMethodologyTypeException extends ApiException {

  /**
   * Builds a 400 exception whose message lists the accepted methodology values.
   *
   * @param message the message to be displayed
   */
  public UnsupportedMethodologyTypeException(final String message) {
    super(HttpStatus.BAD_REQUEST, String.format(
        "%s. Valeurs acceptées: [%s]",
        message,
        Arrays.stream(MethodType.values())
            .map(Enum::name)
            .collect(Collectors.joining(", "))));
  }
}
