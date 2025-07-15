package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriModel {
    private String key = "";
    private String status;
    private String sort;
    private boolean isActiveTab;
    private int page;
    private int size;
}
