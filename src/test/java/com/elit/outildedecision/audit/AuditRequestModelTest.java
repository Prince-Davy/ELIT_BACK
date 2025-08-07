package com.elit.outildedecision.audit;

import com.elit.outildedecision.audit.model.AuditRequestModel;
import com.elit.outildedecision.audit.model.QuestionRequestModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.elit.outildedecision.util.MethodType;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class AuditRequestModelTest {

    @Test
    void testSetAndGetProjectId() {
        AuditRequestModel model = new AuditRequestModel();
        model.setProjectId(123L);
        Assertions.assertEquals(123L, model.getProjectId());
    }

    @Test
    void testSetAndGetMethod() {
        AuditRequestModel model = new AuditRequestModel();
        model.setMethod(MethodType.SCRUM); // Remplace par une valeur valide de ton enum
        Assertions.assertEquals(MethodType.SCRUM, model.getMethod());
    }

    @Test
    void testAddQuestionToList() {
        AuditRequestModel model = new AuditRequestModel();
        QuestionRequestModel question = new QuestionRequestModel();
        model.getQuestions().add(question);
        Assertions.assertEquals(1, model.getQuestions().size());
        Assertions.assertSame(question, model.getQuestions().get(0));
    }

    @Test
    void testQuestionsListIsInitialized() {
        AuditRequestModel model = new AuditRequestModel();
        Assertions.assertNotNull(model.getQuestions());
        Assertions.assertTrue(model.getQuestions().isEmpty());
    }
}
