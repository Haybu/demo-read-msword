package io.agilehandy.demoreadmsword.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.common.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionAnswer {

    private final char  CELL_SEPARATOR = ':';

    private StringBuffer section;
    private StringBuffer subSection;
    private StringBuffer subSectionDescription;
    private StringBuffer subSubSection;
    private StringBuffer subSubSectionDescription;
    private StringBuffer subSubSubSection;
    private StringBuffer subSubSubSectionDescription;

    private StringBuffer topic;
    private StringBuffer guide;

    private StringBuffer question;
    private StringBuffer answer;

    public QuestionAnswer() {}

    @JsonGetter(value = "question")
    public String getQuestion() {
        return question == null? null : question.toString().trim();
    }

    @JsonGetter(value = "answer")
    public String getAnswer() {
        return answer == null? null : answer.toString().trim();
    }

    @JsonGetter(value = "topic")
    public String getTopic() {
        return topic == null? null : topic.toString().trim();
    }

    @JsonGetter(value = "guide")
    public String getGuide() {
        return guide == null? null : guide.toString().trim();
    }

    @JsonIgnore
    //@JsonGetter(value = "section")
    public String getSection() {
        return section == null? null : section.toString().trim();
    }

    @JsonIgnore
    //@JsonGetter(value = "sub-section")
    public String getSubSection() {
        return subSection == null? null : subSection.toString().trim();
    }

    @JsonIgnore
   // @JsonGetter(value = "sub-section-comment")
    public String getSubSectionDescription() {
        return subSectionDescription == null? null : subSectionDescription.toString().trim();
    }

    @JsonIgnore
    //@JsonGetter(value = "sub-sub-section")
    public String getSubSubSection() {
        return subSubSection == null? null : subSubSection.toString().trim();
    }

    @JsonIgnore
    //@JsonGetter(value = "sub-sub-section-comment")
    public String getSubSubSectionDescription() {
        return subSubSectionDescription == null? null : subSubSectionDescription.toString().trim();
    }

    @JsonIgnore
    //@JsonGetter(value = "sub-sub-sub-section")
    public String getSubSubSubSection() {
        return subSubSubSection == null? null : subSubSubSection.toString().trim();
    }

    @JsonIgnore
    //@JsonGetter(value = "sub-sub-sub-section-comment")
    public String getSubSubSubSectionDescription() {
        return subSubSubSectionDescription == null? null : subSubSubSectionDescription.toString().trim();
    }

    public void appendToQuestion(String txt) {
        if (question == null) question = new StringBuffer();
        question.append(this.cleanQuestion(txt) + " ");
    }
    public void appendToAnswer(String txt) {
        if (answer == null) answer = new StringBuffer();
        answer.append(txt + " ");
    }

    public void appendToSection(String txt) {
        if (section == null) section = new StringBuffer();
        section.append(txt + " ");
        // also add as topic metadata
        if (topic == null) topic = new StringBuffer();
        if (!StringUtils.isEmpty(txt)) topic.append(this.cleanSection(txt));
    }

    public void appendToSubSection(String txt) {
        if (subSection == null) subSection = new StringBuffer();
        subSection.append(txt + " ");
        if (topic == null) topic = new StringBuffer();
        if (!StringUtils.isEmpty(txt)) topic.append(" " + CELL_SEPARATOR + " " + this.cleanSection(txt));
    }

    public void appendToSubSectionDescription(String txt) {
        if (subSectionDescription == null) subSectionDescription = new StringBuffer();
        subSectionDescription.append(txt + " ");
        if (guide == null) guide = new StringBuffer();
        if (!StringUtils.isEmpty(txt)) guide.append(txt + "\n\n" );
    }

    public void appendToSubSubSection(String txt) {
        if (subSubSection == null) subSubSection = new StringBuffer();
        subSubSection.append(txt + " ");
        if (topic == null) topic = new StringBuffer();
        if (!StringUtils.isEmpty(txt)) topic.append(" " + CELL_SEPARATOR + " " + this.cleanSection(txt));
    }

    public void appendToSubSubSectionDescription(String txt) {
        if (subSubSectionDescription == null) subSubSectionDescription = new StringBuffer();
        subSubSectionDescription.append(txt + " ");
        if (guide == null) guide = new StringBuffer();
        if (!StringUtils.isEmpty(txt)) guide.append(txt + "\n\n" );
    }

    public void appendToSubSubSubSection(String txt) {
        if (subSubSubSection == null) subSubSubSection = new StringBuffer();
        subSubSubSection.append(txt + " ");
        if (topic == null) topic = new StringBuffer();
        if (!StringUtils.isEmpty(txt)) topic.append(" " + CELL_SEPARATOR + " " + this.cleanSection(txt));
    }

    public void appendToSubSubSubSectionDescription(String txt) {
        if (subSubSubSectionDescription == null) subSubSubSectionDescription = new StringBuffer();
        subSubSubSectionDescription.append(txt + " ");
        if (guide == null) guide = new StringBuffer();
        if (!StringUtils.isEmpty(txt)) guide.append(txt);
    }

    public void print() {
        System.out.println("\nSection: " + this.section);
        if (this.subSection != null)
            System.out.println("\nSubSection: " + this.subSection);
        if (this.subSectionDescription != null)
            System.out.println("\nSubSectionDescription: " + this.subSectionDescription);
        System.out.println("\n\nQuestion: " + question.toString() + "\n\nAnswer: " + answer.toString());
    }

    public String cleanSection(String txt) {
        String result = this.stripFirstNumbers(txt);
        return result.substring(0, result.indexOf(CELL_SEPARATOR)-1);
    }

    public String cleanQuestion(String txt) {
       return this.stripFirstNumbers(txt);
    }

    private String stripFirstNumbers(String txt) {
        String regex = "^(\\d[\\.0-9]*)(.*)";
        Pattern pattern = Pattern.compile(regex);
        String result = "";
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()) {
            result = matcher.group(2);
        }
        return result.trim();
    }

}
