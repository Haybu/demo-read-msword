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
 * Haytham Mohamed
 * 4/15/2024
 *
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
        final String INPUT_FILE_PATH  = "/Users/hmohamed/Downloads/Credence sectioned NationalMedicalRFI2023_LargeMarket_Extract.docx";
        final String OUTPUT_FILE_PATH = "/Users/hmohamed/Downloads/Credence NationalMedicalRFI2023_LargeMarket_Extract.json";
        try {
            XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(INPUT_FILE_PATH)));
            List<IBodyElement> bodyElements = doc.getBodyElements();
            this.readBodyElements(bodyElements);
            //rfpDocument.prettyPrint();
            //rfpDocument.saveToFile(OUTPUT_FILE_PATH);
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
                System.out.println("Question: " + txt.trim());
                rfpDocument.addNewQuestion(txt.trim(), section, subSection, subSectionDescription
                        , subSubSection, subSubSectionDescription
                        , subSubSubSection, subSubSubSectionDescription);
            } else if (label == PartLabels.QUESTION_CONT) {
                rfpDocument.appendToQuestion(txt.trim());
            }
            //System.out.println(paragraph.getText());
        }
    }

    // section, subSection, subSectionDescription, subSubSection, SubSubSubSection, answer
    private void readTable(XWPFTable table) {
        //System.out.println("=== Table Content ===");
        String txt = this.getTableContent(table).toString();
        //if (!nestedTables.isEmpty()) System.out.println("there are tables inside main tables");//////////
        //txt = this.trimLastCharacter(txt, CELL_SEPARATOR);
        int totalNumberOfTables = this.getNumberOfNestedTables(table);
        System.out.println("Number of tables: " + totalNumberOfTables);
        if (totalNumberOfTables == 1 && (table.getNumberOfRows() == 1 || !isAlignedHeadersToRows(txt))) {
            txt = this.clearTags(txt).trim();
        }
        System.out.println(txt);
        this.setLabelForRow(txt);
        if (label == PartLabels.SECTION) {
            section = txt.trim();
            this.resetSections();
        } else if (label == PartLabels.SUB_SECTION) {
            subSection = txt.trim();
            this.resetSubSections();
        } else if (label == PartLabels.SUB_SUB_SECTION) {
            subSubSection = txt.trim();
            this.resetSubSubSections();
        } else if (label == PartLabels.SUB_SUB_SUB_SECTION) {
            subSubSubSection = txt.trim();
        } else if (label == PartLabels.SUB_SECTION_DESCRIPTION) {
            subSectionDescription = txt.trim();
        } else if (label == PartLabels.SUB_SUB_SECTION_DESCRIPTION) {
            subSubSectionDescription = txt.trim();
        } else if (label == PartLabels.SUB_SUB_SUB_SECTION_DESCRIPTION) {
            subSubSubSectionDescription = txt.trim();
        } else if (label == PartLabels.ANSWER || label == PartLabels.ANSWER_CONT) {
            rfpDocument.appendToAnswer(txt.trim() + "\n");
        }
    }

    private StringBuffer getTableContent(XWPFTable table) {
        //System.out.println("=== Table ===");
        List<XWPFTableRow> rows = table.getRows();
        StringBuffer rowBuffer;
        StringBuffer tableBuffer = new StringBuffer();
        for (XWPFTableRow row : rows) {
            rowBuffer = this.getRowContent(row);
            tableBuffer.append(rowBuffer);
            //System.out.println(rowBuffer.toString());/////
        }
        String txt = tableBuffer.toString().trim();
        if (!StringUtils.isEmpty(txt)) {
            tableBuffer.delete(0, tableBuffer.length());
            tableBuffer.append("<table>" + txt.trim() + "</table>");
        }
        /**
        if (!StringUtils.isEmpty(txt) && (table.getNumberOfRows() == 1 || !isAlignedHeadersToRows(txt))) {
            txt = this.clearTags(tableBuffer.toString()).trim();
            tableBuffer.delete(0, tableBuffer.length());
            tableBuffer.append(txt);
        }
        /**
        if (table.getNumberOfRows() >= 1) {
            String temp = tableBuffer.toString();
            temp = temp.replaceAll("<td>", "<th>")
                    .replaceAll("</td>", "</th>").trim();
            tableBuffer.delete(0, tableBuffer.length());
            tableBuffer.append(temp);
        }
         */

        //System.out.println(tableBuffer);//////////
        return tableBuffer;
    }

    private StringBuffer getRowContent(XWPFTableRow row) {
        //System.out.println("=== Table Row ===");
        StringBuffer rowTextBuffer = new StringBuffer();
        List<XWPFTableCell> cells = row.getTableCells();
        int cellCount = 0;
        String temp;
        for (XWPFTableCell cell : cells) {
            rowTextBuffer.append(this.getCellContent(cell));
            //System.out.println("=== Cell ===");
            //System.out.println(rowTextBuffer);
        }
        if (!rowTextBuffer.isEmpty()) {   // && rowTextBuffer.toString().contains("<td>")
            temp = rowTextBuffer.toString();
            rowTextBuffer.delete(0, rowTextBuffer.length());
            rowTextBuffer.append("<tr>" + temp.trim() + "</tr>");
        }
        //System.out.println(rowTextBuffer);
        return rowTextBuffer;
    }

    private StringBuffer getCellContent(XWPFTableCell cell) {
        //System.out.println("Reading Table Cell");
        //System.out.println("Cell type: " + cell.getBodyElements().get(0).getElementType().name());/////
        StringBuffer cellContent = new StringBuffer();

        if (cell.getBodyElements().get(0).getElementType() == BodyElementType.PARAGRAPH) {
            String txt = cell.getTextRecursively().trim();
            if (!StringUtils.isEmpty(txt))
                cellContent.append("<td>" + txt + "</td>");
            else
                cellContent.append("<td></td>");
        } else if (cell.getBodyElements().get(0).getElementType() == BodyElementType.TABLE) { // nested table in a cell
            //System.out.println("nested table inside a cell is found");
            List<XWPFTable> nestedTables = cell.getTables();
            StringBuffer nestedBuffer;
            for (XWPFTable nestedTable : nestedTables) {
                nestedBuffer = this.getTableContent(nestedTable);
                if (!StringUtils.isEmpty(nestedBuffer.toString()))
                    cellContent.append("<td>" + nestedBuffer + "</td>");
                else
                    cellContent.append("<td></td>");
            }
        }
        return cellContent;
    }

    private boolean tableStringWithMoreRows(String txt) {
        return !StringUtils.isEmpty(txt) && (txt.split("</tr>").length > 1);
    }

    private boolean isAlignedHeadersToRows(String txt) {
        String[] rows = txt.split("</tr>");
        if (rows.length > 1) {
            String[] headers = rows[0].split("</td>");
            String[] rowCells = rows[1].split("</td>");
            System.out.println("there are " + headers.length + " headers and " + rowCells.length + " data cells");
            return headers.length == rowCells.length;
        } else
            return false;
    }

    private XWPFTable getNestedTable(XWPFTable table) {
        if(table.getNumberOfRows() == 1) {
            List<XWPFTable> nestedTables = table.getRow(0).getCell(0).getTables();
            if (!nestedTables.isEmpty()) {
                //System.out.println("Nested table found");
                return nestedTables.get(0);
            }
            else {
                //System.out.println("Nested table is NOT found");
                return table;
            }
        }
        else {
            //System.out.println("table has more than 1 row, no Nested table to find");
            return table;
        }
    }

    private String clearTags(String txt) {
        System.out.println("Clearing tags");
        return txt.replaceAll("<tr>", "")
                .replaceAll("</tr>", "")
                .replaceAll("<td>", "")
                .replaceAll("</td>", "")
                .replaceAll("<th>", "")
                .replaceAll("</th>", "")
                .replaceAll("<table>","")
                .replaceAll("</table>","")
                .trim();
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

    private int getNumberOfNestedTables(XWPFTable table) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                List<XWPFTable> list = cell.getTables();
                if (list.isEmpty()) {
                    return 1;
                } else {
                    for (XWPFTable t : list) {
                        return 1 + getNumberOfNestedTables(t);
                    }
                }
            }
        }
        return 1;
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

