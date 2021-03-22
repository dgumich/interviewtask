package interview.task.repositories;

import interview.task.models.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findAll();

    @Transactional
    Article findArticleById(Long id);

    @Transactional
    List<Article> findArticlesByNewsSite(String newsSite);


}
