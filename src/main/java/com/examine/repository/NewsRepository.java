package com.examine.repository;

import com.examine.dto.NewsForEnrichment;
import com.examine.entity.News;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {

    @Query(value = """
            SELECT n.id as id, n.link as link
            FROM News n
            WHERE n.summary IS NULL
        """)
    List<NewsForEnrichment> findNewsForAIEnrichment();

    @Query(value = "UPDATE news SET summary = :summary WHERE article_id = :id", nativeQuery = true )
    void updateWithSummary(@Parameter("id") String articleId, @Parameter("summary") String summary);
}
