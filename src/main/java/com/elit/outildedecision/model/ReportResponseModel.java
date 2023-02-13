package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class ReportResponseModel {
    private Long id;
    private String title;
    private String type;
    private Integer iteration;
    private Date creationDate;
}
