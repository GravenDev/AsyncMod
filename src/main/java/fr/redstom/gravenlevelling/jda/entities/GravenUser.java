package fr.redstom.gravenlevelling.jda.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GravenUser {

    @Id
    private Long id;

}
