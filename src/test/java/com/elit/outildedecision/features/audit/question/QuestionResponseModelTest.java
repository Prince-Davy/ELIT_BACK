package com.elit.outildedecision.features.audit.question;

import com.elit.outildedecision.enumeration.QuestionCategory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestionResponseModelTest {

    @Test
    void testCalculateScoreMetrics_WithValidValues() {
        QuestionResponseModel model = new QuestionResponseModel();
        model.setCurrentScore(3);
        model.setWeight(2);

        model.calculateScoreMetrics();

        assertEquals(6, model.getWeightedScore());
        assertEquals(4, model.getMaxPossibleScore());
        assertEquals(100, model.getScorePercentage());
    }

    @Test
    void testCalculateScoreMetrics_WithNullCurrentScore() {
        QuestionResponseModel model = new QuestionResponseModel();
        model.setCurrentScore(null);
        model.setWeight(2);

        model.calculateScoreMetrics();

        assertEquals(0, model.getWeightedScore());
        assertEquals(4, model.getMaxPossibleScore());
        assertEquals(0.0, model.getScorePercentage());
    }

    @Test
    void testCalculateScoreMetrics_WithNullWeight() {
        QuestionResponseModel model = new QuestionResponseModel();
        model.setCurrentScore(3);
        model.setWeight(null);

        model.calculateScoreMetrics();

        assertEquals(0, model.getWeightedScore());
        assertEquals(0, model.getMaxPossibleScore());
        assertEquals(0.0, model.getScorePercentage());
    }

    @Test
    void testSetCategory_WithNonNullCategory() {
        QuestionResponseModel model = new QuestionResponseModel();
        model.setCategory(QuestionCategory.OBJECTIF_ET_PERIMETRES);
        assertEquals(QuestionCategory.OBJECTIF_ET_PERIMETRES, model.getCategory());
        assertEquals(QuestionCategory.OBJECTIF_ET_PERIMETRES.getDisplayName(), model.getCategoryDisplayName());
    }

    @Test
    void testSetCategory_WithNullCategory() {
        QuestionResponseModel model = new QuestionResponseModel();

        model.setCategory(null);

        assertNull(model.getCategory());
        assertNull(model.getCategoryDisplayName());
    }
}
