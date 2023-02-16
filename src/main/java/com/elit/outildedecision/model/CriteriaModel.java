package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaModel implements Serializable {
    private Long criteriaId;

    private String criteriaName;

    private int criteriaValue;

    // private float score;

    private Long questionId;
}
