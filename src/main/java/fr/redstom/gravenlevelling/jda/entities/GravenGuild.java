package fr.redstom.gravenlevelling.jda.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Table

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class GravenGuild {

    @Id
    private Long id;

}
