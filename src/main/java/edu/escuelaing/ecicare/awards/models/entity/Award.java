package edu.escuelaing.ecicare.awards.models.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "awards")
public class Award {

    // Unique identifier of the award (primary key, auto-generated).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_id", nullable = false, updatable = false)
    private Long awardId;

    // Award name (required, max length 50).
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // Award description (optional, max length 500).
    @Column(name = "description", length = 500)
    private String description;

    // Quantity of items available in stock for this award.
    @Column(name = "in_stock")
    private Integer inStock;

    // URL of the award image (optional).
    @Column(name = "image_url")
    private String imageUrl;

    // Redeemables linked to this award (one-to-many relationship).
    // Managed reference avoids circular serialization issues with Jackson.
    @JsonManagedReference
    @OneToMany(mappedBy = "award", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Redeemable> redeemables;

}
