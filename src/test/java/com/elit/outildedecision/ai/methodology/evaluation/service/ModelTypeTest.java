package com.elit.outildedecision.ai.methodology.evaluation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import org.junit.jupiter.api.Test;

class ModelTypeTest {

  @Test
  void shouldMapAgileWhenPredictedMethodIsAgile() throws UnsupportedModelTypeException {
    // when / then
    assertThat(ModelType.fromPredictedMethod("Agile")).isEqualTo(ModelType.AGILE);
  }

  @Test
  void shouldMapTraditionalWhenPredictedMethodIsTraditionnelle() throws UnsupportedModelTypeException {
    // when / then
    assertThat(ModelType.fromPredictedMethod("Traditionnelle")).isEqualTo(ModelType.TRADITIONAL);
  }

  @Test
  void shouldThrowUnsupportedWhenPredictedMethodIsHybride() {
    // when / then
    assertThatExceptionOfType(UnsupportedModelTypeException.class)
        .isThrownBy(() -> ModelType.fromPredictedMethod("Hybride"))
        .withMessageContaining("Hybride");
  }

  @Test
  void shouldThrowIllegalArgumentWhenPredictedMethodIsNull() {
    // when / then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> ModelType.fromPredictedMethod(null));
  }

  @Test
  void shouldThrowUnsupportedWhenPredictedMethodIsUnknown() {
    // when / then
    assertThatExceptionOfType(UnsupportedModelTypeException.class)
        .isThrownBy(() -> ModelType.fromPredictedMethod("Waterfall"))
        .withMessageContaining("Waterfall");
  }

  @Test
  void shouldExposeApiValueForEachModelType() {
    // when / then
    assertThat(ModelType.AGILE.getValue()).isEqualTo("agile");
    assertThat(ModelType.TRADITIONAL.getValue()).isEqualTo("traditional");
  }
}
