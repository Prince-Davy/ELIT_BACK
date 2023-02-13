package com.elit.outildedecision.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity@Builder
@Data
@AllArgsConstructor @NoArgsConstructor
public class Methodology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long methodologyId;
    private String name; /* Classic, Hybrid ou Agile */
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String disadvantages;
    @Column(columnDefinition = "TEXT")
    private String advantages;

    @OneToMany(
            mappedBy = "methodology",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JsonManagedReference("methodology_predictibility")
    private List<Method> methods;


    @Override
    public String toString() {
        return "Methodology{" +
                "methodologyId=" + methodologyId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", disadvantages='" + disadvantages + '\'' +
                ", advantages='" + advantages + '\'' +
                '}';
    }
}
