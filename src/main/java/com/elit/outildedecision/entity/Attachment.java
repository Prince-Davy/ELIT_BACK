package com.elit.outildedecision.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;


@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;
    private String fileName;
    private String filePath;
    private String contentType;


    @ManyToOne
    @JoinColumn(
            name = "project_Id",
            referencedColumnName = "projectId"
    )
    private Project project;

    @JsonBackReference("project_attachment")
    public Project getProject(){
        return this.project;
    }

    public void deleteAttachment(){
        this.setProject(null);
    }
}
