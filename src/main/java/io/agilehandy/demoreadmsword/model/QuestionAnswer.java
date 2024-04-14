package io.agilehandy.demoreadmsword.model;

import com.fasterxml.jackson.annotation.JsonGetter;

public class QuestionAnswer {

    private StringBuffer section;
    private StringBuffer subSection;
    private StringBuffer subSectionDescription;
    private StringBuffer subSubSection;
    private StringBuffer subSubSectionDescription;
    private StringBuffer subSubSubSection;
    private StringBuffer subSubSubSectionDescription;
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

    @JsonGetter(value = "section")
    public String getSection() {
        return section == null? null : section.toString();
    }

    @JsonGetter(value = "sub-section")
    public String getSubSection() {
        return subSection == null? null : subSection.toString();
    }

    @JsonGetter(value = "sub-section-comment")
    public String getSubSectionDescription() {
        return subSectionDescription == null? null : subSectionDescription.toString();
    }

    @JsonGetter(value = "sub-sub-section")
    public String getSubSubSection() {
        return subSubSection == null? null : subSubSection.toString();
    }

    @JsonGetter(value = "sub-sub-section-comment")
    public String getSubSubSectionDescription() {
        return subSubSectionDescription == null? null : subSubSectionDescription.toString();
    }

    @JsonGetter(value = "sub-sub-sub-section")
    public String getSubSubSubSection() {
        return subSubSubSection == null? null : subSubSubSection.toString();
    }

    @JsonGetter(value = "sub-sub-sub-section-comment")
    public String getSubSubSubSectionDescription() {
        return subSubSubSectionDescription == null? null : subSubSubSectionDescription.toString();
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

    public void appendToSubSectionDescription(String txt) {
        if (subSectionDescription == null) {
            subSectionDescription = new StringBuffer(txt);
        } else {
            subSectionDescription.append(txt + " ");
        }
    }

    public void appendToSubSubSection(String txt) {
        if (subSubSection == null) {
            subSubSection = new StringBuffer(txt);
        } else {
            subSubSection.append(txt + " ");
        }
    }

    public void appendToSubSubSectionDescription(String txt) {
        if (subSubSectionDescription == null) {
            subSubSectionDescription = new StringBuffer(txt);
        } else {
            subSubSectionDescription.append(txt + " ");
        }
    }

    public void appendToSubSubSubSection(String txt) {
        if (subSubSubSection == null) {
            subSubSubSection = new StringBuffer(txt);
        } else {
            subSubSubSection.append(txt + " ");
        }
    }

    public void appendToSubSubSubSectionDescription(String txt) {
        if (subSubSubSectionDescription == null) {
            subSubSubSectionDescription = new StringBuffer(txt);
        } else {
            subSubSubSectionDescription.append(txt + " ");
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
