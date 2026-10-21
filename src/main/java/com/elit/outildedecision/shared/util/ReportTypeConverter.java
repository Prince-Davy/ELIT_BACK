package com.elit.outildedecision.shared.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores enum values using the legacy report.type strings already present in MySQL.
 * For example MULTI_CRITERIA_ANALYSIS is persisted as "multicriteria_report".
 */
@Converter(autoApply = false)
public class ReportTypeConverter implements AttributeConverter<ReportTypes, String> {

  @Override
  public String convertToDatabaseColumn(final ReportTypes attribute) {
    return attribute == null ? null : attribute.toString();
  }

  @Override
  public ReportTypes convertToEntityAttribute(final String dbData) {
    if (dbData == null) {
      return null;
    }
    try {
      return ReportTypes.fromValue(dbData);
    } catch (IllegalArgumentException exception) {
      // Read values mistakenly persisted as enum names by an intermediate version.
      // All new writes still use the stable legacy API value.
      return ReportTypes.valueOf(dbData);
    }
  }
}
