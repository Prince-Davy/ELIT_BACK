package com.elit.outildedecision.snapshot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.email.models.EmailSendStatus;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import org.mockito.Mockito;

/**
 * Deterministic stub for the Azure Communication Services e-mail client (plan §10.1).
 *
 * <p>Decision actée: no test sends a real e-mail nor depends on SMTP/Azure credentials.
 * The mail-sending services ({@code UserServiceImpl}, {@code VerificationTokenServiceImpl})
 * drive a {@link SyncPoller} in a polling loop that sleeps 10s between polls; the stub
 * resolves to {@code SUCCEEDED} on the first poll so the loop exits immediately with no
 * network and no wait. This keeps the auth-journey snapshots fast and deterministic.
 */
final class MailMocks {

  private MailMocks() {
  }

  /**
   * Stubs the mocked {@link EmailClient} so any {@code beginSend} returns a poller that
   * completes successfully on the first poll.
   *
   * @param emailClient the Mockito mock injected via {@code @MockitoBean}
   */
  @SuppressWarnings("unchecked")
  static void stubSuccessfulSend(final EmailClient emailClient) {
    EmailSendResult result =
        new EmailSendResult("test-message-id", EmailSendStatus.SUCCEEDED, null);

    PollResponse<EmailSendResult> pollResponse =
        new PollResponse<>(LongRunningOperationStatus.SUCCESSFULLY_COMPLETED, result);

    SyncPoller<EmailSendResult, EmailSendResult> poller = Mockito.mock(SyncPoller.class);
    lenient().when(poller.poll()).thenReturn(pollResponse);
    lenient().when(poller.getFinalResult()).thenReturn(result);

    lenient().when(emailClient.beginSend(any(EmailMessage.class), any())).thenReturn(poller);
  }
}
