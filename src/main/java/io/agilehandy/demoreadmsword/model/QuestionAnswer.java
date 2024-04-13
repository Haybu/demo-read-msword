package io.agilehandy.demoreadmsword.model;

public class QuestionAnswer {

    private StringBuffer question;
    private StringBuffer answer;

    public QuestionAnswer() {}

    public String getAnswer() {
        return answer.toString();
    }

    public void setAnswer(StringBuffer answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question.toString();
    }

    public void setQuestion(StringBuffer question) {
        this.question = question;
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

    public void print() {
        System.out.println("Question: " + question.toString() + "\n\n Answer: " + answer.toString() + "\n\n");
    }

}
