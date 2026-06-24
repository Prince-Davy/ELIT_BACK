package com.elit.outildedecision.shared.mailer.service.implementation;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.exception.AzureException;
import com.azure.core.models.ResponseError;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.elit.outildedecision.shared.mailer.MailMessage;
import com.elit.outildedecision.shared.mailer.exception.MailDeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AzureMailSenderImplTest {

  @Mock
  private EmailClient emailClient;

  @Captor
  private ArgumentCaptor<EmailMessage> messageCaptor;

  private AzureMailSenderImpl mailSender;

  private final MailMessage message =
      new MailMessage("user@example.com", "Subject", "<html>body</html>");

  @BeforeEach
  void setUp() {
    mailSender = new AzureMailSenderImpl(emailClient);
    ReflectionTestUtils.setField(mailSender, "senderEmail", "no-reply@elit.com");
  }

  @SuppressWarnings("unchecked")
  private SyncPoller<EmailSendResult, EmailSendResult> stubPoller(final EmailSendResult result) {
    PollResponse<EmailSendResult> pollResponse =
        new PollResponse<>(LongRunningOperationStatus.SUCCESSFULLY_COMPLETED, result);
    SyncPoller<EmailSendResult, EmailSendResult> poller = mock(SyncPoller.class);
    lenient().when(poller.poll()).thenReturn(pollResponse);
    lenient().when(poller.getFinalResult()).thenReturn(result);
    when(emailClient.beginSend(any(EmailMessage.class), eq(null))).thenReturn(poller);
    return poller;
  }

  @Test
  void shouldSendMessageWithSenderAddressWhenDeliverySucceeds() {
    // given
    stubPoller(new EmailSendResult("id", EmailSendStatus.SUCCEEDED, null));

    // when / then
    assertThatNoException().isThrownBy(() -> mailSender.send(message));
    verifyCapturedMessage();
  }

  @Test
  void shouldThrowMailDeliveryExceptionWhenFinalStatusIsNotSucceeded() {
    // given
    stubPoller(new EmailSendResult("id", EmailSendStatus.FAILED, new ResponseError("code", "boom")));

    // when / then
    assertThatExceptionOfType(MailDeliveryException.class)
        .isThrownBy(() -> mailSender.send(message));
  }

  @Test
  void shouldWrapAzureExceptionInMailDeliveryException() {
    // given
    when(emailClient.beginSend(any(EmailMessage.class), eq(null)))
        .thenThrow(new AzureException("transport down"));

    // when / then
    assertThatExceptionOfType(MailDeliveryException.class)
        .isThrownBy(() -> mailSender.send(message));
  }

  private void verifyCapturedMessage() {
    verify(emailClient).beginSend(messageCaptor.capture(), eq(null));
    EmailMessage sent = messageCaptor.getValue();
    assertThat(sent.getSenderAddress()).isEqualTo("no-reply@elit.com");
    assertThat(sent.getToRecipients()).extracting("address").containsExactly("user@example.com");
    assertThat(sent.getSubject()).isEqualTo("Subject");
    assertThat(sent.getBodyHtml()).isEqualTo("<html>body</html>");
  }
}
