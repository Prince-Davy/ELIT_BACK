package com.elit.outildedecision.shared.mailer.template;

import com.elit.outildedecision.shared.exception.FileOperationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class BrandedMailTemplateTest {

  private final BrandedMailTemplate template = new BrandedMailTemplate();

  @Test
  void shouldRenderGreetingBodyAndCallToActionWhenLogoExists() {
    // when
    String html = template.build(
        "Alice", "Cliquez ici.", "Activer", "https://elit.test/go", "avatars/logoImage.png");

    // then
    assertThat(html)
        .contains("Bonjour Alice,")
        .contains("Cliquez ici.")
        .contains("Activer")
        .contains("href='https://elit.test/go'")
        .contains("data:image/png;base64,");
  }

  @Test
  void shouldThrowFileOperationExceptionWhenLogoIsMissing() {
    // when / then
    assertThatExceptionOfType(FileOperationException.class).isThrownBy(() ->
        template.build("Alice", "body", "cta", "url", "avatars/does-not-exist.png"));
  }
}
