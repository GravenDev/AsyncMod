package fr.itsasync.moderation.data.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"guild_id", "level"})})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsyncGuildReward {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private AsyncGuild guild;

    private long level;

    private long roleId;
}
