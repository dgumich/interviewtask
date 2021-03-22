package interview.task.services;

import interview.task.models.Form;

import java.util.ArrayList;
import java.util.List;

public class Pair {

    public final String name;

    public final List<Form> forms;

    public Pair(String name, Form form) {
        this.name = name;
        this.forms = new ArrayList<>();
        this.forms.add(form);
    }

    public void addForm (Form form) {
        this.forms.add(form);
    }

}
