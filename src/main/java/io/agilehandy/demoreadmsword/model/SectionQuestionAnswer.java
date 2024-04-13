package io.agilehandy.demoreadmsword.model;

import java.util.ArrayList;
import java.util.List;

public class SectionQuestionAnswer {

    private String sectionName;
    private String subSectionName;
    private String subSectionDescription;
    List<QuestionAnswer> questions;

    public SectionQuestionAnswer() {}

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String name) {
        this.sectionName = name;
    }

    public String getSubSectionName() {
        return subSectionName;
    }

    public void setSubSectionName(String subsectionName) {
        this.subSectionName = subsectionName;
    }

    public String getSubSectionDescription() { return subSectionDescription; }

    public void setSubSectionDescription(String subSectionDescription) { this.subSectionDescription = subSectionDescription; }

    public void addNewQuestion(String txt) {
        QuestionAnswer questionnaire = new QuestionAnswer();
        questionnaire.appendToQuestion(txt);
        if (questions == null) { questions = new ArrayList<>(); }
        questions.add(questionnaire);
    }

    public void appendToQuestion(String txt) {
        QuestionAnswer questionnaire = questions.get(questions.size()-1);
        questionnaire.appendToQuestion(txt);
    }

    public void appendToAnswer(String txt) {
        QuestionAnswer questionnaire = questions.get(questions.size()-1);
        questionnaire.appendToAnswer(txt);
    }

    public void print() {
        System.out.println("Section: " + this.sectionName);
        if (this.subSectionName != null)
            System.out.println("\nSubSection: " + this.subSectionName + "\n\n");
        if (this.subSectionDescription != null)
            System.out.println("\nSubSectionDescription: " + this.subSectionDescription + "\n\n");
        for (QuestionAnswer questionnaire : questions) {questionnaire.print();}
    }
}
