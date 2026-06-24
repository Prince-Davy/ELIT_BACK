package com.elit.outildedecision.collaboration.predictibility.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class PredictibilityAnalysisTest {
    @Test
    void testToString(){
        PredictibilityAnalysis pa = new PredictibilityAnalysis();
        pa.setPredictibilityAnalysisId(1L);
        pa.setIsFinished(true);
        pa.setPredictibilityAnalysisIteration(1);
        final String paString= "PredictibilityAnalysis{"
        + "predictibilityAnalysisId="
        + 1
        + ", predictibilityAnalysisIteration='"
        + 1
        + '\''
        + ", isFinished='"
        + true
        + '\''
        + '}';
        assertEquals(paString, pa.toString());
    }
}
