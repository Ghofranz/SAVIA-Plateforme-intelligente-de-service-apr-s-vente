package com.savia.sav_service.entity;

import com.savia.sav_service.enums.SavCaseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sav_case_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavCaseStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Relation interne au sav-service.
     * Ici c'est autorisé car SavCase et SavCaseStatusHistory appartiennent
     * à la même base sav_db.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sav_case_id", nullable = false)
    private SavCase savCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private SavCaseStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private SavCaseStatus newStatus;

    /*
     * ID du user dans auth-service qui a fait le changement.
     */
    @Column(name = "changed_by_auth_user_id", nullable = false)
    private Long changedByAuthUserId;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    public void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}