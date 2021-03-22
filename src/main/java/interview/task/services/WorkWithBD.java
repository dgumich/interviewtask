package interview.task.services;

import interview.task.models.Article;
import interview.task.models.Form;
import interview.task.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class WorkWithBD {

    private ArticleRepository articleRepository;

    public  HttpClient httpClient;

    public WorkWithBD(HttpClient httpClient, ArticleRepository articleRepository) {
        this.httpClient = httpClient;
        this.articleRepository = articleRepository;
    }

    public void writeToDB(List<Form> forms) {

        for (Form form: forms) {
            String url = form.url;
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();

            HttpResponse<String> response = null;
            try {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            String articleText = response.body();
            Article article = new Article();
            article.setTitle(form.title);
            article.setNewsSite(form.newsSite);
            article.setPublishedDate(form.publishedAt);
            article.setArticle(articleText);
            articleRepository.save(article);
        }
    }
}
