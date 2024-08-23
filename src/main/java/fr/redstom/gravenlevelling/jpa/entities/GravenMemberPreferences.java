package fr.redstom.gravenlevelling.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.awt.*;

@Entity
@Table

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GravenMemberPreferences {

    @Id
    @OneToOne
    private GravenMember gravenMember;

    private Color color;



}
