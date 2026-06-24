package com.elit.outildedecision.shared.user.event;

import com.elit.outildedecision.shared.auth.service.VerificationMailService;
import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.user.exception.UserNotFoundException;
import com.elit.outildedecision.shared.user.repository.UserRepository;
import com.elit.outildedecision.shared.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sends the registration confirmation email once the account-creation transaction has committed.
 *
 * <p>The confirmation token is already persisted (atomically with the account) by the time this
 * runs, so the listener only reloads the user and sends the mail — no DB write. Listening on
 * {@link TransactionPhase#AFTER_COMMIT} keeps the slow Azure polling out of the creation
 * transaction, and {@link Async} runs it off the request thread so registration returns immediately.
 * A mail failure here must never undo an already committed account: the error is logged and
 * swallowed on purpose so the user stays created and can request a new email.
 */
@Component
@Slf4j
public class UserRegistrationListener {

  /**
   * Service composing and sending the confirmation email.
   */
  private final VerificationMailService verificationMailService;

  /**
   * Repository used to reload the committed user before sending the email.
   */
  private final UserRepository userRepository;

  /**
   * Instantiates the listener.
   *
   * @param verificationMailService the service sending the confirmation email
   * @param userRepository          the repository reloading the committed user
   */
  public UserRegistrationListener(final VerificationMailService verificationMailService,
                                  final UserRepository userRepository) {
    this.verificationMailService = verificationMailService;
    this.userRepository = userRepository;
  }

  /**
   * Sends the confirmation email for a freshly committed registration.
   *
   * @param event the user-registered event carrying the persisted user id
   */
  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onUserRegistered(final UserRegisteredEvent event) {
    try {
      User user = userRepository.findById(event.userId())
          .orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND));
      verificationMailService.sendAccountConfirmationEmail(user, event.confirmationToken());
    } catch (RuntimeException e) {
      log.error("Failed to send registration confirmation email for userId={}", event.userId(), e);
    }
  }
}
