package com.elit.outildedecision.shared.auth.service.implementation;

import com.elit.outildedecision.shared.kernel.entity.User;
import com.elit.outildedecision.shared.mailer.MailMessage;
import com.elit.outildedecision.shared.mailer.service.MailSender;
import com.elit.outildedecision.shared.mailer.template.BrandedMailTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationMailServiceImplTest {

  @Mock
  private MailSender mailSender;

  @Mock
  private BrandedMailTemplate template;

  @Captor
  private ArgumentCaptor<MailMessage> messageCaptor;

  private VerificationMailServiceImpl service;

  private User user;

  @BeforeEach
  void setUp() {
    service = new VerificationMailServiceImpl(mailSender, template);
    ReflectionTestUtils.setField(service, "frontEndUrl", "https://elit.test");
    user = new User();
    user.setUserId(7L);
    user.setUserFirstName("Alice");
    user.setUserEmail("alice@example.com");
    when(template.build(any(), any(), any(), any(), any())).thenReturn("<html>body</html>");
  }

  @Test
  void shouldSendAccountConfirmationEmailWithConfirmationUrlAndSubject() {
    // when
    service.sendAccountConfirmationEmail(user, "tok-123");

    // then
    verify(template).build(eq("Alice"), any(), any(),
        eq("https://elit.test/confirm-account?token=tok-123&id=7"), any());
    verify(mailSender).send(messageCaptor.capture());
    MailMessage sent = messageCaptor.getValue();
    assertThat(sent.to()).isEqualTo("alice@example.com");
    assertThat(sent.subject()).isEqualTo("ELIT : Activez votre compte");
    assertThat(sent.htmlBody()).isEqualTo("<html>body</html>");
  }

  @Test
  void shouldSendPasswordResetEmailWithRecoveryUrlAndSubject() {
    // when
    service.sendPasswordResetEmail(user, "tok-456");

    // then
    verify(template).build(eq("Alice"), any(), any(),
        eq("https://elit.test/reset-password?token=tok-456"), any());
    verify(mailSender).send(messageCaptor.capture());
    MailMessage sent = messageCaptor.getValue();
    assertThat(sent.to()).isEqualTo("alice@example.com");
    assertThat(sent.subject()).isEqualTo("ELIT : Réinitialisez votre mot de passe");
  }
}
