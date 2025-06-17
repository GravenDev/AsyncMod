package fr.itsasync.moderation.data.entity;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table
@IdClass(AsyncMember.AsyncMemberId.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsyncMember {

    @ManyToOne @Id private AsyncUser user;

    @ManyToOne @Id private AsyncGuild guild;

    @Builder.Default private Instant lastMessageAt = Instant.EPOCH;

    private long level;
    private long experience;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AsyncMemberId implements Serializable {
        private AsyncUser user;

        private AsyncGuild guild;
    }
}
