package com.examine.entity;

import com.examine.dto.enums.Source;
import com.examine.entity.enums.EnrichmentStatus;
import com.examine.entity.enums.NewsType;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Serdeable
public class News {

    @Id
    @Column(name = "article_id")
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, length = 500)
    private String link;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private Source source;

    @Column(name = "extracted_at")
    private LocalDateTime extractedAt;

    @Column(name = "enrichment_status")
    @Enumerated(EnumType.STRING)
    private EnrichmentStatus status = EnrichmentStatus.NEW;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NewsType type = NewsType.FREE;
}
