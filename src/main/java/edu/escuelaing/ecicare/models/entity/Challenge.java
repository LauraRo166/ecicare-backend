package edu.escuelaing.ecicare.models.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;

import edu.escuelaing.ecicare.premios.models.entity.Redeemable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id", nullable = false, updatable = false)
    private Long challengeId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    // @Column(name = "phrase", length = 300)
    // private String phrase;

    // @Column(name = "validator_role", length = 50)
    // private String validatorRole;

    // @Lob
    // @Column(name = "image", columnDefinition = "LONGBLOB")
    // private byte[] image;

    // @Column(name = "image_size_kb")
    // private Double imageSizeKB;

    // @Column(name = "creation_date")
    // private LocalDateTime creationDate;

    // @Column(name = "update_date")
    // private LocalDateTime updateDate;

    // @Column(name = "created_by")
    // private Long createdBy;

    // @Column(name = "updated_by")
    // private Long updatedBy;

    // @JsonBackReference
    // @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge")
    // List<Progress> progresses;

    // @JsonBackReference
    // @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge")
    // private List<Redeemable> redeemables;

    // @OneToMany(fetch = FetchType.EAGER, mappedBy = "challenge", cascade = CascadeType.ALL)
    // private List<Tip> tips;

    // @ManyToOne
    // @JoinColumn(name = "module_id", referencedColumnName = "module_id")
    // private Module module;

}