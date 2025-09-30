package edu.escuelaing.ecicare.premios.models.entity;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "award_id", nullable = false, updatable = false)
    private Long awardId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "in_stock")
    private Integer inStock;

    @Column(name = "image_url")
    private String imageUrl;

    @JsonManagedReference
    @OneToMany(mappedBy = "award", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Redeemable> redeemables;

}