package interview.task.services;

import interview.task.models.Form;

import java.util.Comparator;

public class FormComparator implements Comparator<Form> {
    @Override
    public int compare(Form form, Form t1) {
        return form.publishedAt.compareTo(t1.publishedAt);
    }
}
