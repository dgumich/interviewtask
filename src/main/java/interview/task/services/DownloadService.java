package interview.task.services;

import interview.task.models.Form;
import interview.task.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DownloadService {

    @Autowired
    private ArticleRepository articleRepository;

    public void downloadArticles() {

        final HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        WorkWithBD workWithBD = new WorkWithBD(httpClient, articleRepository);

        ExecutorService executorService = Executors.newFixedThreadPool(DataContainer.threadNumber);
        ArrayList<MyCallable> tasks = new ArrayList<>();
        int cycle =(int)Math.ceil((double)DataContainer.newsNumber / (DataContainer.threadNumber * DataContainer.newsPerCycleByOneThread));
        int start = 0;
        for (int i = 0; i < DataContainer.threadNumber * cycle; i++) {
            if (start >= DataContainer.newsNumber) {
                break;
            } else {
                if (start + DataContainer.newsPerCycleByOneThread > DataContainer.newsNumber) {
                    DataContainer.newsPerCycleByOneThread = DataContainer.newsNumber - start;
                }
            }
            tasks.add(new MyCallable(start, DataContainer.newsPerCycleByOneThread, httpClient, workWithBD));
            start += DataContainer.newsPerCycleByOneThread;
        }
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        ArrayList<List<Form>> allArticles = new ArrayList<>(DataContainer.pairs.values());
        for(List<Form> forms : allArticles) {
            workWithBD.writeToDB(forms);
        }

    }
    
}
