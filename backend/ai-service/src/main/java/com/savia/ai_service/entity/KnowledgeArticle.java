package com.savia.ai_service.entity;

import com.savia.ai_service.enums.KnowledgeArticleStatus;
import jakarta.persistence.*;
        import lombok.*;

        import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_articles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long savCaseId;

    @Column(nullable = false)
    private String caseReference;

    private Long customerProductId;

    @Column(nullable = false)
    private String sourceStatus;

    @Column(columnDefinition = "TEXT")
    private String originalProblem;

    @Column(columnDefinition = "TEXT")
    private String resolutionComment;

    @Column(columnDefinition = "TEXT")
    private String symptomSummary;

    @Column(columnDefinition = "TEXT")
    private String confirmedCause;

    @Column(columnDefinition = "TEXT")
    private String appliedSolution;

    @Column(columnDefinition = "TEXT")
    private String reusableKnowledge;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KnowledgeArticleStatus status;

    @Column(nullable = false)
    private boolean indexedInRag;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        indexedInRag = false;

        if (status == null) {
            status = KnowledgeArticleStatus.DRAFT;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}