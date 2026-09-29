package hu.wcag.wcagbackend.model;

import hu.wcag.wcagbackend.types.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="errors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WcagError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "wcag_criterion", length = 50)
    private String wcagCriterion;
}
