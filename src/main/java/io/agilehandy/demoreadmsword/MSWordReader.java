package io.agilehandy.demoreadmsword;

import io.agilehandy.demoreadmsword.model.PartLabels;
import io.agilehandy.demoreadmsword.model.RFPDocument;
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

    private Pattern normalTextPattern, sectionTextPattern
            ,  subSectionTextPattern, subSubSectionTextPattern, questionTextPattern;
    private String section, subSection, subSectionDescription
            , subSubSection, subSubSectionDescription
            , subSubSubSection, subSubSubSectionDescription;

    private final String normalTextRegex = ".+";
    private final String SectionRegex = "^\\d(?!(\\.))\\s*.*(Answers|Questions)";
    private final String subSectionRegex = "^\\d\\.\\d+(?!(\\.\\d))\\s*.*(Answers|Questions)";
    private final String subSubSectionRegex = "^[\\.\\d]+\\s*.*(Answers|Questions)";
    private final String questionRegex = "^\\d[\\.\\d]+\\s((?!(Answers|Questions)).)*";

    private final char  CELL_SEPARATOR = ':';

    @PostConstruct
    public void init() {
        normalTextPattern = Pattern.compile(normalTextRegex, Pattern.CASE_INSENSITIVE);
        sectionTextPattern = Pattern.compile(SectionRegex, Pattern.CASE_INSENSITIVE);
        subSectionTextPattern = Pattern.compile(subSectionRegex, Pattern.CASE_INSENSITIVE);
        subSubSectionTextPattern = Pattern.compile(subSubSectionRegex, Pattern.CASE_INSENSITIVE);
        questionTextPattern = Pattern.compile(questionRegex, Pattern.CASE_INSENSITIVE);
    }

    public void read() {
        final String INPUT_FILE_PATH = "/Users/hmohamed/Downloads/Credence sectioned NationalMedicalRFI2023_LargeMarket_Extract.docx";
        final String OUTPUT_FILE_PATH = "/Users/hmohamed/Downloads/Credence NationalMedicalRFI2023_LargeMarket_Extract.json";
        try {
            XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(INPUT_FILE_PATH)));
            List<IBodyElement> bodyElements = doc.getBodyElements();
            this.readBodyElements(bodyElements);
            rfpDocument.prettyPrint();
            rfpDocument.saveToFile(OUTPUT_FILE_PATH);
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
        }
    }

    // title or question
    private void readParagraph(XWPFParagraph paragraph) {
        //System.out.println("=== Paragraph ===");
        String txt = paragraph.getText();
        if (!StringUtils.isEmpty(txt)) {
            setLabelForParagraph(txt.trim());
            if (label == PartLabels.TITLE) {
                rfpDocument.setTitle(txt);
            } else if (label == PartLabels.QUESTION) {
                rfpDocument.addNewQuestion(txt, section, subSection, subSectionDescription
                        , subSubSection, subSubSectionDescription
                        , subSubSubSection, subSubSubSectionDescription);
            } else if (label == PartLabels.QUESTION_CONT) {
                rfpDocument.appendToQuestion(txt);
            }
            //System.out.println(paragraph.getText());
        }

    }

    // section, subSection, subSectionDescription, subSubSection, SubSubSubSection, answer
    private void readTable(XWPFTable table) {
        //System.out.println("=== Table ===");
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            StringBuffer rowBuffer = this.getRowContent(row);
            String txt = rowBuffer.toString().trim();
            //txt = this.trimLastCharacter(txt, CELL_SEPARATOR);
            this.setLabelForRow(txt);
            if (label == PartLabels.SECTION) {
                section = txt;
                this.resetSections();
            } else if (label == PartLabels.SUB_SECTION) {
                subSection = txt;
                this.resetSubSections();
            } else if (label == PartLabels.SUB_SUB_SECTION) {
                    subSubSection = txt;
                this.resetSubSubSections();
            } else if (label == PartLabels.SUB_SUB_SUB_SECTION) {
                subSubSubSection = txt;
            } else if (label == PartLabels.SUB_SECTION_DESCRIPTION) {
                    subSectionDescription = txt;
            } else if (label == PartLabels.SUB_SUB_SECTION_DESCRIPTION) {
                subSubSectionDescription = txt;
            } else if (label == PartLabels.SUB_SUB_SUB_SECTION_DESCRIPTION) {
                subSubSubSectionDescription = txt;
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
        //System.out.println("set label for paragraph: " + txt);
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
        //System.out.println("label: " + label.name());
    }

    // section, subsection, subsectionDescription, subsubsection, answer
    private void setLabelForRow(String txt) {
        //System.out.println("set label for row: " + txt);
        boolean found = false;

        Matcher matcher = sectionTextPattern.matcher(txt);
        if (matcher.find() && label != PartLabels.QUESTION && label != PartLabels.SUB_SECTION) {
            label = PartLabels.SECTION;
            found = true;
        }

        if (!found) {
            matcher = subSectionTextPattern.matcher(txt);
            if (matcher.find() && (label == PartLabels.SECTION || label == PartLabels.SUB_SECTION
                    || label == PartLabels.ANSWER)) {
                label = PartLabels.SUB_SECTION;
                found = true;
            }
        }

        if (!found) {
            matcher = subSubSectionTextPattern.matcher(txt);
            if (matcher.find() && (label == PartLabels.SUB_SECTION
                    || label == PartLabels.ANSWER || label == PartLabels.ANSWER_CONT
                    || label == PartLabels.SUB_SECTION_DESCRIPTION)) {
                label = PartLabels.SUB_SUB_SECTION;
                found = true;
            }
        }

        if (!found) {
            matcher = subSubSectionTextPattern.matcher(txt);  // 2 subs still for the 3 subs pattern
            if (matcher.find() && label == PartLabels.SUB_SUB_SECTION) {
                label = PartLabels.SUB_SUB_SUB_SECTION;
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
            } else if (match && label == PartLabels.SUB_SECTION ) {
                //System.out.println("Found sub_section description");//////
                label = PartLabels.SUB_SECTION_DESCRIPTION;
                found = true;
            } else if (match && label == PartLabels.SUB_SUB_SECTION ) {
                //System.out.println("Found sub_sub_section description");//////
                label = PartLabels.SUB_SUB_SECTION_DESCRIPTION;
                found = true;
            } else if (match && label == PartLabels.SUB_SUB_SUB_SECTION ) {
                //System.out.println("Found sub_sub_sub_section description");//////
                label = PartLabels.SUB_SUB_SUB_SECTION_DESCRIPTION;
                found = true;
            }
        }

        if(!found) {
            label = PartLabels.NOT_VALID;
        }
        //System.out.println("label: " + label.name());
    }

    private void resetSections() {
        subSection = subSectionDescription
                = subSubSection = subSubSectionDescription
                = subSubSubSection = subSubSubSectionDescription = null;
    }

    private void resetSubSections() {
        subSectionDescription = subSubSection = subSubSectionDescription
                = subSubSubSection = subSubSubSectionDescription = null;
    }

    private void resetSubSubSections() {
        subSubSectionDescription = subSubSubSection = subSubSubSectionDescription = null;
    }


}

