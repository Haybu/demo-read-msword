package io.agilehandy.demoreadmsword.model;

import java.util.ArrayList;
import java.util.List;

public class RFPDocument {

    private String title;
    private List<SectionQuestionAnswer> sections;

    public RFPDocument() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SectionQuestionAnswer> getSections() {
        return sections;
    }

    public void setSections(List<SectionQuestionAnswer> sections) {
        this.sections = sections;
    }

    public void addNewSection(String sectionName) {
        if (this.sections == null) {
            sections = new ArrayList<>();
        }
        SectionQuestionAnswer newSection = new SectionQuestionAnswer();
        newSection.setSectionName(sectionName);
        this.sections.add(newSection);
    }

    public void setSubSectionName(String subSectionName) {
        SectionQuestionAnswer sectionQuestionAnswer = sections.get(sections.size() - 1);
        sectionQuestionAnswer.setSubSectionName(subSectionName);
    }

    public void setSubSectionDescription(String desc) {
        SectionQuestionAnswer sectionQuestionAnswer = sections.get(sections.size() - 1);
        sectionQuestionAnswer.setSubSectionDescription(desc);
    }

    public void addNewQuestion(String txt) {
        SectionQuestionAnswer sectionQuestionAnswer = sections.get(sections.size() - 1);
        sectionQuestionAnswer.addNewQuestion(txt);
    }

    public void appendToQuestion(String txt) {
        SectionQuestionAnswer sectionQuestionAnswer = sections.get(sections.size() - 1);
        sectionQuestionAnswer.appendToQuestion(txt);
    }

    public void appendToAnswer(String txt) {
        SectionQuestionAnswer sectionQuestionAnswer = sections.get(sections.size() - 1);
        sectionQuestionAnswer.appendToAnswer(txt);
    }

    public void print() {
        System.out.printf("Title: %s\n\n", title);
        for (SectionQuestionAnswer sectionQuestionAnswer : sections) {sectionQuestionAnswer.print();}
    }

}
