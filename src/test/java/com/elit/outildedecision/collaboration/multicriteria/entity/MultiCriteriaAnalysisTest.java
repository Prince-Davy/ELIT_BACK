package com.elit.outildedecision.collaboration.multicriteria.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

 class MultiCriteriaAnalysisTest {
    @Test
    void testToString(){
        final Timestamp date = Mockito.mock(Timestamp.class);
        MultiCriteriaAnalysis mca = new MultiCriteriaAnalysis();
        mca.setMultiCriteriaAnalysisId(1L);
        mca.setCreatedAt(date);
        mca.setUpdatedAt(date);
        mca.setIsFinished(true);
        mca.setMultiCriteriaAnalysisIteration(1);
        mca.setFormSteps(null);
        final String mcaString = "MultiCriteriaAnalysis{"
        + "multiCriteriaAnalysisId="
        + 1
        + ", createdAt="
        + date
        + ", updatedAt="
        + date
        + ", multiCriteriaAnalysisIteration="
        + 1
        + ", isFinished="
        + true
        + ", formSteps="
        + "null"
        + '}';
        assertEquals(mcaString, mca.toString());
    }
}
