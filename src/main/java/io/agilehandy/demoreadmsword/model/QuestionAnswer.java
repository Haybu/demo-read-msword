package io.agilehandy.demoreadmsword.model;

import com.fasterxml.jackson.annotation.JsonGetter;

public class QuestionAnswer {

    private StringBuffer section;
    private StringBuffer subSection;
    private StringBuffer subSectionDescription;
    private StringBuffer subSubSection;
    private StringBuffer question;
    private StringBuffer answer;

    public QuestionAnswer() {}

    @JsonGetter(value = "answer")
    public String getAnswer() {
        return answer == null? null : answer.toString();
    }

    @JsonGetter(value = "question")
    public String getQuestion() {
        return question == null? null : question.toString();
    }

    @JsonGetter(value = "subsection-comment")
    public String getSubSectionDescription() {
        return subSectionDescription == null? null : subSectionDescription.toString();
    }

    @JsonGetter(value = "sub-section")
    public String getSubSection() {
        return subSection == null? null : subSection.toString();
    }

    @JsonGetter(value = "sub-sub-section")
    public String getSubSubSection() {
        return subSubSection == null? null : subSubSection.toString();
    }

    @JsonGetter(value = "section")
    public String getSection() {
        return section == null? null : section.toString();
    }

    public void appendToQuestion(String txt) {
        if (question == null) {
            question = new StringBuffer(txt);
        } else {
            question.append(txt + " ");
        }
    }
    public void appendToAnswer(String txt) {
        if (answer == null) {
            answer = new StringBuffer(txt);
        } else {
            answer.append(txt + " ");
        }
    }

    public void appendToSection(String txt) {
        if (section == null) {
            section = new StringBuffer(txt);
        } else {
            section.append(txt + " ");
        }
    }

    public void appendToSubSection(String txt) {
        if (subSection == null) {
            subSection = new StringBuffer(txt);
        } else {
            subSection.append(txt + " ");
        }
    }

    public void appendToSubSubSection(String txt) {
        if (subSubSection == null) {
            subSubSection = new StringBuffer(txt);
        } else {
            subSubSection.append(txt + " ");
        }
    }

    public void appendToSubSectionDescription(String txt) {
        if (subSectionDescription == null) {
            subSectionDescription = new StringBuffer(txt);
        } else {
            subSectionDescription.append(txt + " ");
        }
    }

    public void print() {
        System.out.println("\nSection: " + this.section);
        if (this.subSection != null)
            System.out.println("\nSubSection: " + this.subSection);
        if (this.subSectionDescription != null)
            System.out.println("\nSubSectionDescription: " + this.subSectionDescription);
        System.out.println("\n\nQuestion: " + question.toString() + "\n\nAnswer: " + answer.toString());
    }

}
