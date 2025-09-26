package edu.escuelaing.ecicare.retos.models.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Represents a training or learning module within the Ecicare system.
 * A module serves as a container for a set of {@link Challenge} entities
 * that are grouped by a common theme, such as health, wellness, or fitness.
 *
 * @author Byte Programming
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "module")
public class Module {

    @Id
    @Column(name="name", nullable = false, updatable = false)
    private String name; //Serves as the primary key in the {@code module} table.

    @Column(name = "description")
    private String description; //Optional description providing additional information

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Challenge> challenges; //List of challenges associated with this module.

    @Column(name = "image_url")
    private String imageUrl; //Url of image from module

    /**
     * Convenience constructor that creates a module with only a name.
     * Useful when only the identifier is needed.
     *
     * @param name the unique name of the module
     */
    public Module(String name) {
        this.name = name;
    }
}
