package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePageProject {
    private Page<ProjectResponseModel> projectsPage;
    private long totalProjetCreated;
    private long totalProjetAffected;

}
