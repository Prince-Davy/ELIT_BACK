package com.elit.outildedecision.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * This class represents a paginated response for projects.
 * It contains a list of project content, the current page number, page size,
 * total number of elements, and total number of pages.
 * It uses Lombok annotations to generate constructors, getters, and setters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedProjectsResponse {
    private List<ProjectResponseModel> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
