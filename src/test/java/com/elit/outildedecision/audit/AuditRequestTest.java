package com.elit.outildedecision.audit;

import com.elit.outildedecision.audit.dto.AuditRequest;
import com.elit.outildedecision.audit.dto.QuestionRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.elit.outildedecision.shared.util.MethodType;

class AuditRequestTest {

    @Test
    void testSetAndGetProjectId() {
        AuditRequest model = new AuditRequest();
        model.setProjectId(123L);
        Assertions.assertEquals(123L, model.getProjectId());
    }

    @Test
    void testSetAndGetMethod() {
        AuditRequest model = new AuditRequest();
        model.setMethod(MethodType.SCRUM);
        Assertions.assertEquals(MethodType.SCRUM, model.getMethod());
    }

    @Test
    void testAddQuestionToList() {
        AuditRequest model = new AuditRequest();
        QuestionRequest question = new QuestionRequest();
        model.getQuestions().add(question);
        Assertions.assertEquals(1, model.getQuestions().size());
        Assertions.assertSame(question, model.getQuestions().get(0));
    }

    @Test
    void testQuestionsListIsInitialized() {
        AuditRequest model = new AuditRequest();
        Assertions.assertNotNull(model.getQuestions());
        Assertions.assertTrue(model.getQuestions().isEmpty());
    }
}
