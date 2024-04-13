package io.agilehandy.demoreadmsword.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RFPDocument {

    private String title;
    List<QuestionAnswer> questions;

    public RFPDocument() {}

    public void addNewQuestion(String txt, String section, String subSection, String subSectionDescription) {
        QuestionAnswer questionnaire = new QuestionAnswer();
        questionnaire.appendToQuestion(txt);
        if (!StringUtils.isEmpty(section)) questionnaire.appendToSection(section);
        if (!StringUtils.isEmpty(subSection)) questionnaire.appendToSubSection(subSection);
        if (!StringUtils.isEmpty(subSectionDescription)) questionnaire.appendToSubSectionDescription(subSectionDescription);
        if (questions == null) { questions = new ArrayList<>(); }
        questions.add(questionnaire);
    }

    public void appendSection(String txt) {
        QuestionAnswer questionnaire = questions.get(questions.size()-1);
        questionnaire.appendToSection(txt);
    }

    public void appendSubSection(String txt) {
        QuestionAnswer questionnaire = questions.get(questions.size()-1);
        questionnaire.appendToSubSection(txt);
    }

    public void appendSubSectionDescription(String txt) {
        QuestionAnswer questionnaire = questions.get(questions.size()-1);
        questionnaire.appendToSubSectionDescription(txt);
    }

    public void appendToQuestion(String txt) {
        QuestionAnswer questionnaire = questions.get(questions.size()-1);
        questionnaire.appendToQuestion(txt);
    }

    public void appendToAnswer(String txt) {
        QuestionAnswer questionnaire = questions.get(questions.size()-1);
        questionnaire.appendToAnswer(txt);
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<QuestionAnswer> getQuestions() { return questions; }
    public void setQuestions(List<QuestionAnswer> questions) { this.questions = questions; }

    public void print() {
        System.out.println("\nTitle: " + title);
        for (QuestionAnswer questionnaire : questions) {questionnaire.print();}
    }

    public String prettyString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        //String jsonBook = mapper.writeValueAsString(this);
        //return jsonBook;
        String indentedBook = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        return indentedBook;
    }

    public void prettyPrint() throws JsonProcessingException {
        System.out.println(prettyString());
    }

    public void saveToFile(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(prettyString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
