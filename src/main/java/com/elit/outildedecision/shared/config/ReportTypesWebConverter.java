package com.elit.outildedecision.shared.config;

import com.elit.outildedecision.shared.util.ReportTypes;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts legacy report type strings in URL path variables to typed domain values.
 * Jackson's JsonCreator does not apply to Spring MVC path variable conversion.
 */
@Component
public final class ReportTypesWebConverter implements Converter<String, ReportTypes> {
  /**
   * Resolves the external report category.
   *
   * @param source the URL path segment
   * @return the matching report type
   */
  @Override
  public ReportTypes convert(final String source) {
    return ReportTypes.fromValue(source);
  }
}
