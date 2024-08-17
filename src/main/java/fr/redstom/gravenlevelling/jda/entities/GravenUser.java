package fr.redstom.gravenlevelling.jda.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table

@Builder
@AllArgsConstructor
@NoArgsConstructor

@Getter
@Setter
public class GravenUser {

    @Id
    private Long id;

}
