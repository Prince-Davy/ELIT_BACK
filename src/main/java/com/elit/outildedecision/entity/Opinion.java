package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Opinion {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long opinionId;

    private Integer rating;

    private String description;

    @OneToOne
    @JoinColumn(
            name = "userId",
            referencedColumnName = "userId"
    )
    private User user;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Paris")
    private Date createdAt;
}
