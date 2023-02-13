package com.elit.outildedecision.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity@Builder
@Data
@AllArgsConstructor @NoArgsConstructor
public class Method {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; /* Scrum, Kanban ... */
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String disadvantages;
    @Column(columnDefinition = "TEXT")
    private String advantages;

//    @OneToMany(
//            mappedBy = "formStep",
//            cascade = CascadeType.ALL
//    )
//    private List<ExecutionPlanVote> executionPlanVotes;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name ="methodology_id",
            referencedColumnName = "methodologyId"
    )
    @JsonBackReference("methodology_predictibility")
    private Methodology methodology;

    @Override
    public String toString() {
        return "Method{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", disadvantages='" + disadvantages + '\'' +
                ", advantages='" + advantages + '\'' +
                '}';
    }

    /* @OneToMany(
            mappedBy = "formStep",
            cascade = CascadeType.ALL
    )
    private List<ExecutionPlanVote> executionPlanVotes; */
}
