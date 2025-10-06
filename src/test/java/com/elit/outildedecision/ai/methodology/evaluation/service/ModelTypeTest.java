package com.elit.outildedecision.ai.methodology.evaluation.service;

import com.elit.outildedecision.ai.methodology.evaluation.exception.UnsupportedModelTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelTypeTest {

  @Test
  void testFromPredictedMethod_Agile() throws UnsupportedModelTypeException {
    ModelType type = ModelType.fromPredictedMethod("Agile");
    assertEquals(ModelType.AGILE, type);
    assertEquals("agile", type.getValue());
  }

  @Test
  void testFromPredictedMethod_Traditionnelle() throws UnsupportedModelTypeException {
    ModelType type = ModelType.fromPredictedMethod("Traditionnelle");
    assertEquals(ModelType.TRADITIONAL, type);
    assertEquals("traditional", type.getValue());
  }

  @Test
  void testFromPredictedMethod_Hybride_ThrowsUnsupportedModelTypeException() {
    UnsupportedModelTypeException exception = assertThrows(UnsupportedModelTypeException.class, () ->
      ModelType.fromPredictedMethod("Hybride"));

    assertEquals("Hybride is not supported yet", exception.getMessage());
  }

  @Test
  void testFromPredictedMethod_UnknownValue_ThrowsUnsupportedModelTypeException() {
    UnsupportedModelTypeException exception = assertThrows(UnsupportedModelTypeException.class, () ->
      ModelType.fromPredictedMethod("Scrum"));

    assertEquals("Predicted methodology 'Scrum' is not supported", exception.getMessage());
  }

  @Test
  void testFromPredictedMethod_Null_ThrowsIllegalArgumentException() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
      ModelType.fromPredictedMethod(null));

    assertEquals("Predicted methodology is absent", exception.getMessage());
  }
}
