package io.agilehandy.demoreadmsword;

import io.agilehandy.demoreadmsword.model.PartLabels;
import io.agilehandy.demoreadmsword.model.RFPDocument;
import io.agilehandy.demoreadmsword.model.SectionQuestionAnswer;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * observation:
 *  sections and subsections and answers are on cells
 *  title and questions are in paragraph
 *
 */

@Component
public class MSWordReader {

    private PartLabels label = PartLabels.NONE;
    private RFPDocument rfpDocument = new RFPDocument();

    private Pattern normalTextPattern, sectionTextPattern,  subSectionTextPattern, questionTextPattern;

    //private final String normalTextRegex = "^(?!\\d).+$";
    //private final String normalTextRegex = "[a-zA-Z0-9-_:]+";
    private final String normalTextRegex = ".+";
    private final String SectionRegex = "^\\d(?!(\\.))\\s*.*(Answers|Questions)";
    private final String subSectionRegex = "^\\d[\\.\\d]+\\s*.*(Answers|Questions)";
    private final String questionRegex = "^\\d[\\.\\d]+\\s((?!(Answers|Questions)).)*";

    private final char  CELL_SEPARATOR = ':';

    @PostConstruct
    public void init() {
        normalTextPattern = Pattern.compile(normalTextRegex, Pattern.CASE_INSENSITIVE);
        sectionTextPattern = Pattern.compile(SectionRegex, Pattern.CASE_INSENSITIVE);
        subSectionTextPattern = Pattern.compile(subSectionRegex, Pattern.CASE_INSENSITIVE);
        questionTextPattern = Pattern.compile(questionRegex, Pattern.CASE_INSENSITIVE);
    }

    public void read() {
        final String FILE_PATH = "/Users/hmohamed/Downloads/Credence NationalMedicalRFI2023_LargeMarket_Extract.docx";
        try {
            XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(FILE_PATH)));
            List<IBodyElement> bodyElements = doc.getBodyElements();
            this.readBodyElements(bodyElements);
            rfpDocument.print();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readBodyElements(List<IBodyElement> bodyElements) {
        for (IBodyElement bodyElement : bodyElements) {
            if (bodyElement instanceof XWPFParagraph) {
                this.readParagraph((XWPFParagraph) bodyElement);
            }  else if (bodyElement instanceof XWPFTable) {
                this.readTable((XWPFTable) bodyElement);
            }
            //else if (bodyElement instanceof XWPFRun) {
                //this.readRun((XWPFRun) bodyElement);
            //}
        }
    }

    /**
    private void readRun(XWPFRun run) {
        System.out.println("=== Run ===");
        System.out.println(run.getParagraph().getText());
    }
     */

    // title or question
    private void readParagraph(XWPFParagraph paragraph) {
        //System.out.println("=== Paragraph ===");
        String txt = paragraph.getText();
        if (!StringUtils.isEmpty(txt)) {
            setLabelForParagraph(txt.trim());
            if (label == PartLabels.TITLE) {
                rfpDocument.setTitle(txt);
            } else if (label == PartLabels.QUESTION) {
                rfpDocument.addNewQuestion(txt);
            } else if (label == PartLabels.QUESTION_CONT) {
                rfpDocument.appendToQuestion(txt);
            }
            //System.out.println(paragraph.getText());
        }

    }

    // section, subsection, answer
    private void readTable(XWPFTable table) {
        //System.out.println("=== Table ===");
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            StringBuffer rowBuffer = this.getRowContent(row);
            String txt = rowBuffer.toString().trim();
            //txt = this.trimLastCharacter(txt, CELL_SEPARATOR);
            this.setLabelForRow(txt);
            if (label == PartLabels.SECTION) {
                rfpDocument.addNewSection(txt);
            } else if (label == PartLabels.SUBSECTION) {
                rfpDocument.setSubSectionName(txt);
            } else if (label == PartLabels.ANSWER || label == PartLabels.ANSWER_CONT) {
                rfpDocument.appendToAnswer(txt + "\n");
            }
        }
    }

    private StringBuffer getRowContent(XWPFTableRow row) {
        //System.out.println("=== Table Row ===");
        StringBuffer rowTextBuffer = new StringBuffer();
        List<XWPFTableCell> cells = row.getTableCells();
        String cellContent;
        int cellCount = 0;
        for (XWPFTableCell cell : cells) {
            cellCount++;
            cellContent = this.getCellContent(cell);
            if (!StringUtils.isEmpty(cellContent))
                rowTextBuffer.append(cellContent);
            if (cellCount > 1)
                rowTextBuffer.append(" " + CELL_SEPARATOR + " ");
        }
        //System.out.println(rowTextBuffer);
        return rowTextBuffer;
    }

    private String getCellContent(XWPFTableCell cell) {
        String txt = cell.getTextRecursively();
        if (!StringUtils.isEmpty(txt)) {
            //System.out.println("=== Table Cell ===");
            return txt;
        }
        return "";
    }

    // title, questions
    private void setLabelForParagraph(String txt) {
        System.out.println("set label for paragraph: " + txt);
        Matcher matcher = normalTextPattern.matcher(txt);
        boolean found = false;
        boolean match = matcher.find();
        if (match && label == PartLabels.NONE) {
            label = PartLabels.TITLE;
            found = true;
        } else if (match && (label == PartLabels.QUESTION || label == PartLabels.QUESTION_CONT)) {
            label = PartLabels.QUESTION_CONT;
            found = true;
        }
        if (!found) {
            matcher = questionTextPattern.matcher(txt);
            if (matcher.find()) {
                label = PartLabels.QUESTION;
                found = true;
            }
        }
        if(!found) {
            label = PartLabels.NOT_VALID;
        }
        System.out.println("label: " + label.name());
    }

    // section, subsection, subsectionDescription, answer
    private void setLabelForRow(String txt) {
        System.out.println("set label for row: " + txt);
        boolean found = false;

        Matcher matcher = subSectionTextPattern.matcher(txt);
        if (!found) {
            if (matcher.find() && (label == PartLabels.SECTION || label == PartLabels.SUBSECTION)) {
                label = PartLabels.SUBSECTION;
                found = true;
            }
        }

        if (!found) {
            matcher = sectionTextPattern.matcher(txt);
            if (matcher.find() && label != PartLabels.QUESTION && label != PartLabels.SUBSECTION) {
                label = PartLabels.SECTION;
                found = true;
            }
        }

        if (!found) {
            matcher = normalTextPattern.matcher(txt);
            boolean match = matcher.find();
            if (match && (label == PartLabels.QUESTION || label == PartLabels.QUESTION_CONT)) {
                label = PartLabels.ANSWER;
                found = true;
            } else if (match && (label == PartLabels.ANSWER || label == PartLabels.ANSWER_CONT)) {
                label = PartLabels.ANSWER_CONT;
                found = true;
            } else if (match && label == PartLabels.SUBSECTION ) {
                label = PartLabels.SUBSECTION_DESCRIPTION;
                found = true;
            }
        }

        if(!found) {
            label = PartLabels.NOT_VALID;
        }
        System.out.println("label: " + label.name());
    }

    /**
    @Deprecated
    private void setLabel(String txt) {
        Matcher matcher = normalTextPattern.matcher(txt);
        boolean found = false;
        if (matcher.find() && label == PartLabels.NONE) {
            label = PartLabels.TITLE;
            found = true;
        }

        if (!found) {
            matcher = sectionTextPattern.matcher(txt);
            if (matcher.find()) {
                label = PartLabels.SECTION;
                found = true;
            }
        }

        if (!found) {
            matcher = subSectionTextPattern.matcher(txt);
            if (matcher.find()) {
                label = PartLabels.SUBSECTION;
                found = true;
            }
        }

        if (!found) {
            matcher = questionTextPattern.matcher(txt);
            if (matcher.find()) {
                label = PartLabels.QUESTION;
                found = true;
            }
        }
    }

    private String trimLastCharacter(String txt, char character) {
        if (StringUtils.isEmpty(txt))
            return txt;
        else if (txt.trim().charAt(txt.trim().length() - 1) == character)
            return trimLastCharacter(txt.trim().substring(0, txt.trim().length() - 1), character);
        else
            return txt.trim();
    }
    */

}

