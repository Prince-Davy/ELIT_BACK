package com.elit.outildedecision.shared.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

 class ReportTypeTest {
    @Test
    void testToString(){
        ReportTypes rt = ReportTypes.MULTI_CRITERIA_ANALYSIS;
        final String rtString= "multicriteria_report";
        assertEquals(rtString, rt.toString());
    }

    @Test
    void testReportTypes(){
        ReportTypes rt = ReportTypes.EXECUTION_PLAN;
        assertNotNull(rt);
    }
}
