package com.savia.sav_service.entity;

import com.savia.sav_service.enums.SavCaseStatus;
import com.savia.sav_service.enums.SavPriority;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sav_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_reference", nullable = false, unique = true)
    private String caseReference;

    /*
     * ID du client dans customer-service.
     * Pas de relation JPA directe car customer-service possède sa propre base.
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /*
     * ID du produit client dans customer-service.
     */
    @Column(name = "customer_product_id", nullable = false)
    private Long customerProductId;

    /*
     * ID du user dans auth-service qui a créé la demande.
     */
    @Column(name = "created_by_auth_user_id", nullable = false)
    private Long createdByAuthUserId;

    /*
     * ID de l'agent SAV assigné, venant de auth-service.
     */
    @Column(name = "assigned_agent_auth_user_id")
    private Long assignedAgentAuthUserId;

    /*
     * ID du technicien assigné, venant de auth-service.
     */
    @Column(name = "assigned_technician_auth_user_id")
    private Long assignedTechnicianAuthUserId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavCaseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavPriority priority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = SavCaseStatus.CREATED;
        }

        if (this.priority == null) {
            this.priority = SavPriority.MEDIUM;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}