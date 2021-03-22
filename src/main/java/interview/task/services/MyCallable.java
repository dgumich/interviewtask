package interview.task.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import interview.task.models.Form;
import interview.task.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MyCallable implements Callable<Boolean> {

    private final String URL_PART_1 = "https://test.spaceflightnewsapi.net/api/v2/articles?_limit=";
    private final String URL_PART_2 = "&_start=";

    private int newsToPass;
    private int newsToDownload;

    private final HttpClient httpClient;

    private  WorkWithBD workWithBD;


    public MyCallable(int newsToPass, int newsToDownload, HttpClient httpClient, WorkWithBD workWithBD) {
        this.newsToDownload = newsToDownload;
        this.newsToPass = newsToPass;
        this.httpClient = httpClient;
        this.workWithBD = workWithBD;
    }


    @Override
    public Boolean call() {

        String url = URL_PART_1 + newsToDownload + URL_PART_2 + newsToPass;

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

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Form> forms = null;
        try {
            forms = objectMapper.readValue(response.body(), new TypeReference<List<Form>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < forms.size(); i++) {
            for (int j = 0; j < DataContainer.banWords.size(); j++) {
                if (forms.get(i).title.contains(DataContainer.banWords.get(j))) {
                    forms.remove(i);
                    i--;
                    break;
                }
            }
        }
        FormComparator formComparator = new FormComparator();
        synchronized (DataContainer.pairs){
            for (Form form : forms) {
                if (DataContainer.pairs.containsKey(form.newsSite)) {
                    List<Form> formsFromBuffer = DataContainer.pairs.get(form.newsSite);
                    formsFromBuffer.add(form);
                    formsFromBuffer.sort(formComparator);
                    if (formsFromBuffer.size() == DataContainer.bufferLimit) {
                        workWithBD.writeToDB(formsFromBuffer);
                        DataContainer.pairs.remove(form.newsSite);
                    } else {
                        DataContainer.pairs.put(form.newsSite, formsFromBuffer);
                    }
                } else {
                    List<Form> formsForBuffer = new ArrayList<>();
                    formsForBuffer.add(form);
                    DataContainer.pairs.put(form.newsSite, formsForBuffer);
                }
            }
        }
        return true;
    }
}
