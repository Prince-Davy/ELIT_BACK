package com.elit.outildedecision.audit.question;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    private Question question;

    @BeforeEach
    void setUp() {
        question = new Question();
        question.setWeight(2); // valeur par défaut pour les tests
    }

    @Test
    void testCalculateAndSetScore_WhenAuditAnswerValueIsNotNull() {
        question.setAuditAnswerValue(3);
        question.calculateAndSetScore();
        assertEquals(3, question.getCurrentScore());
    }

    @Test
    void testCalculateAndSetScore_WhenAuditAnswerValueIsNull() {
        question.setAuditAnswerValue(null);
        question.calculateAndSetScore();
        assertEquals(0, question.getCurrentScore());
    }

    @Test
    void testGetMaxPossibleScore_WhenAuditAnswerValueIsNull() {
        question.setAuditAnswerValue(null);
        assertEquals(0, question.getMaxPossibleScore());
    }

    @Test
    void testGetMaxPossibleScore_WhenAuditAnswerValueIsNotNull() {
        question.setAuditAnswerValue(2);
        question.setWeight(3);
        assertEquals(6, question.getMaxPossibleScore());
    }
}
