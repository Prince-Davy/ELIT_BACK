package com.elit.outildedecision.shared.mailer.template;

import com.elit.outildedecision.shared.exception.FileOperationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Base64;

/**
 * Builds the ELIT branded HTML e-mail (logo header, greeting, body paragraph, call-to-action button
 * and footer).
 *
 * <p>Generic on purpose: it carries no notion of token, user or bounded context. Callers provide the
 * recipient first name, the body paragraph, the button label/URL and the classpath of the logo image.
 */
@Component
public class BrandedMailTemplate {

  /**
   * Closing tag of an HTML division element.
   */
  private static final String DIV_CLOSE = "</div> ";

  /**
   * Footer tagline displayed under the separator.
   */
  private static final String FOOTER_TAGLINE = "La méthodologie à la mesure de votre projet";

  /**
   * Validity notice shown above the signature.
   */
  private static final String VALIDITY_NOTICE = "Attention, le lien a une durée de validité de 48 heures.";

  /**
   * Builds the full HTML body of a branded ELIT e-mail.
   *
   * @param firstName     the recipient first name shown in the greeting
   * @param bodyParagraph the action-specific sentence shown above the button
   * @param ctaLabel      the call-to-action button label
   * @param ctaUrl        the call-to-action target URL
   * @param logoClasspath the classpath location of the logo image (PNG)
   * @return the rendered HTML body
   * @throws FileOperationException if the logo image cannot be read
   */
  public String build(final String firstName,
                      final String bodyParagraph,
                      final String ctaLabel,
                      final String ctaUrl,
                      final String logoClasspath) {
    String base64Image = encodeLogo(logoClasspath);
    return "<html><body style='margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;'> "
        + "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden;'> "
        + "<div style='background-color: #e9f2fb; padding: 50px; text-align: center;'> "
        + "<img src='data:image/png;base64," + base64Image + "' alt='ELIT' style='display: block; margin: auto;'> "
        + DIV_CLOSE
        + "<div style='padding: 20px; text-align: left; font-size: medium;'> "
        + "<p>Bonjour " + firstName + ",</p> "
        + "<p>Vous y êtes presque ! </p> "
        + "<p>" + bodyParagraph + "</p> "
        + "<div style='text-align: center;'> "
        + "<a href='" + ctaUrl + "' style='display: inline-block; padding: 14px 20px; margin: 20px 0; font-size: 16px; color: #ffffff; background-color: #116E9C; text-decoration: none; border-radius: 8px;'>" + ctaLabel + "</a> "
        + DIV_CLOSE
        + "<p>" + VALIDITY_NOTICE + "</p> "
        + "<p>Cordialement, L'équipe ELIT</p> "
        + DIV_CLOSE
        + "<hr style='border: none; height: 5px; background-color: #12abdb; width: calc(100% - 40px);'> "
        + "<div style='display: flex; align-items: center; padding: 2px 20px 20px 2px;'> "
        + "<img src='data:image/png;base64," + base64Image + "' alt='ELIT' style='margin-right: 20px; height: 30px;'> "
        + "<span style='font-size: 12px; color: #666666;'>" + FOOTER_TAGLINE + "</span> "
        + DIV_CLOSE
        + DIV_CLOSE
        + "</body></html>";
  }

  /**
   * Reads the logo image from the classpath and encodes it in Base64.
   *
   * @param logoClasspath the classpath location of the logo image
   * @return the Base64-encoded image
   * @throws FileOperationException if the image cannot be read
   */
  private String encodeLogo(final String logoClasspath) {
    ClassPathResource logoFile = new ClassPathResource(logoClasspath);
    try {
      byte[] bytes = StreamUtils.copyToByteArray(logoFile.getInputStream());
      return Base64.getEncoder().encodeToString(bytes);
    } catch (IOException e) {
      throw new FileOperationException("Erreur lors de la lecture du fichier image", e);
    }
  }
}
