package com.elit.outildedecision.util;

import com.elit.outildedecision.enumeration.MethodologyType;
import com.elit.outildedecision.exception.UnsupportedMethodologyTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodologyTypeTest {

    @Test
    void testToString(){
        MethodologyType mt = MethodologyType.AGILE;
        final String mtString = "agile";
        assertEquals(mt.toString(), mtString);
    }

   @Test
   void find() throws UnsupportedMethodologyTypeException {
     assertNotNull(MethodologyType.find("agile"));
     assertNotNull(MethodologyType.find("AgIlE"));
     assertNotNull(MethodologyType.find("classic"));
     assertNotNull(MethodologyType.find("classique"));
     assertNotNull(MethodologyType.find("hybrid"));
     assertNotNull(MethodologyType.find("hybride"));
     assertNotNull(MethodologyType.find("Hybrid"));
     assertNotNull(MethodologyType.find("Hybride"));
     assertThrows(UnsupportedMethodologyTypeException.class, () -> MethodologyType.find(""));
     assertThrows(UnsupportedMethodologyTypeException.class, () -> MethodologyType.find("randomstring"));
     assertThrows(UnsupportedMethodologyTypeException.class, () -> MethodologyType.find(null));
   }
 }
