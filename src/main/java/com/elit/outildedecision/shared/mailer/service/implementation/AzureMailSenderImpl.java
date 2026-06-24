package com.elit.outildedecision.shared.mailer.service.implementation;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.exception.AzureException;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.elit.outildedecision.shared.mailer.MailMessage;
import com.elit.outildedecision.shared.mailer.exception.MailDeliveryException;
import com.elit.outildedecision.shared.mailer.service.MailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * {@link MailSender} backed by Azure Communication Services.
 *
 * <p>Sole owner of the Azure {@code EmailClient} interaction: it submits the message and drives the
 * {@link SyncPoller} long-running operation, polling every 10 seconds up to 18 times (3 minutes)
 * before giving up. Any transport, timeout or interruption failure is normalised into a
 * {@link MailDeliveryException}.
 */
@Service
@Slf4j
public class AzureMailSenderImpl implements MailSender {

  /**
   * Number of poll attempts (10s apart) before the operation is considered timed out.
   */
  private static final int MAX_POLL_ATTEMPTS = 18;

  /**
   * Delay between two poll attempts of the Azure send operation.
   */
  private static final Duration POLLER_WAIT_TIME = Duration.ofSeconds(10);

  /**
   * Azure e-mail client used to submit the message.
   */
  private final EmailClient emailClient;

  /**
   * Sender e-mail address applied to every outbound message.
   */
  @Value("${spring.mail.username}")
  private String senderEmail;

  /**
   * Builds the sender.
   *
   * @param emailClient the Azure e-mail client bean
   */
  public AzureMailSenderImpl(final EmailClient emailClient) {
    this.emailClient = emailClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void send(final MailMessage message) {
    try {
      EmailMessage azureMessage = new EmailMessage()
          .setSenderAddress(senderEmail)
          .setToRecipients(message.to())
          .setSubject(message.subject())
          .setBodyHtml(message.htmlBody());

      SyncPoller<EmailSendResult, EmailSendResult> poller =
          emailClient.beginSend(azureMessage, null);
      PollResponse<EmailSendResult> pollResponse = poller.poll();
      Duration timeElapsed = Duration.ofSeconds(0);

      while (pollResponse.getStatus() == LongRunningOperationStatus.NOT_STARTED
          || pollResponse.getStatus() == LongRunningOperationStatus.IN_PROGRESS) {
        if (timeElapsed.compareTo(POLLER_WAIT_TIME.multipliedBy(MAX_POLL_ATTEMPTS)) >= 0) {
          throw new MailDeliveryException("Polling timed out.");
        }
        sleepBetweenPolls();
        timeElapsed = timeElapsed.plus(POLLER_WAIT_TIME);
        pollResponse = poller.poll();
      }

      EmailSendResult finalResult = poller.getFinalResult();
      if (finalResult != null && finalResult.getStatus() != EmailSendStatus.SUCCEEDED) {
        throw new MailDeliveryException(finalResult.getError().getMessage());
      }
    } catch (AzureException e) {
      log.error("Erreur lors de l'envoi du mail vers {}", message.to(), e);
      throw new MailDeliveryException("Erreur lors de l'envoi du mail", e);
    }
  }

  /**
   * Sleeps for {@link #POLLER_WAIT_TIME} between two poll attempts, restoring the interrupt flag and
   * normalising an interruption into a {@link MailDeliveryException}.
   */
  private void sleepBetweenPolls() {
    try {
      Thread.sleep(POLLER_WAIT_TIME.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new MailDeliveryException("Thread sleep interrupted", e);
    }
  }
}
