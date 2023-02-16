package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseModel implements Serializable {
    private Long projectId;
    private String name;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String confirmationState;
    private Long chefId;
    private Boolean isArchived;
    private ArrayList<ContributerModel> contributors;
    private ArrayList<AttachmentResponseModel> attachments;
    //Multi-criteria analyse
    private ArrayList<MulticriteriaAnalyseResponseModel> multiCriteriaAnalysisList;

}
