package interview.task.controllers;

import interview.task.models.Article;
import interview.task.repositories.ArticleRepository;
import interview.task.services.DataContainer;
import interview.task.services.DownloadService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class FirstController {

    @Autowired
    DownloadService downloadService;

    @Autowired
    ArticleRepository articleRepository;

    @GetMapping("/hello")
    public String greeting() {
        return "hello";
    }

    @GetMapping("/parameters")
    public String setParameters(@RequestParam(name="threadNumber", required=false, defaultValue= "1") Integer threadNumber,
                                @RequestParam(name="newsPerCylcle", required=false, defaultValue= "2") Integer newsPerCycle,
                                @RequestParam(name="newsNumber", required=false, defaultValue= "3") Integer newsNumber,
                                @RequestParam(name="bufferLimit", required=false, defaultValue= "2") Integer bufferLimit,
                                @RequestParam(name="banWords", required=false, defaultValue= "Hello") String banWords
                                ) {

        DataContainer.threadNumber = threadNumber;
        DataContainer.newsNumber = newsNumber;
        DataContainer.bufferLimit = bufferLimit;
        DataContainer.newsPerCycleByOneThread = newsPerCycle;
        Collections.addAll(DataContainer.banWords, banWords.split(","));
        downloadService.downloadArticles();
        return "choose article";
    }

    @GetMapping("/newschosen")
    public String findArticles(@RequestParam(name= "chosenOption", required = false, defaultValue = "all") String chosenOption, Model model) {
        List<Article> articles = new ArrayList<>();
        if ("all".equals(chosenOption)) {
            articles = articleRepository.findAll();
        } else if (NumberUtils.isCreatable(chosenOption)) {
            Article article = articleRepository.findArticleById(Long.parseLong(chosenOption));
            articles.add(article);
        } else {
            articles = articleRepository.findArticlesByNewsSite(chosenOption);
        }

        model.addAttribute("articles", articles);

        return "articles";

    }

}
