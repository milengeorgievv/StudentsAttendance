package org.vaadin.example;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.example.dao.PlatformRepository;
import org.vaadin.example.dao.StudentActivityRepository;
import org.vaadin.example.dao.StudentResultRepository;
import org.vaadin.example.entity.StudentActivityEntity;
import org.vaadin.example.entity.StudentResultEntity;

import java.io.*;
import java.util.Iterator;

@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

    public MainView(@Autowired GreetService service, PlatformRepository platformRepository,
                    StudentActivityRepository studentActivityRepository, StudentResultRepository studentResultRepository) {
        Html title = new Html("<span style=\"font-size: 30px; font-family: Cursive;\">Data analysis for e-learning management</span>");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(10);
        upload.setDropLabel(new Label("Upload up to 10 files in .xlsx format"));
        //upload.setAcceptedFileTypes("text/csv");
        Div output = new Div();

        upload.addFileRejectedListener(event -> {
            Paragraph component = new Paragraph();
            //showOutput(event.getErrorMessage(), component, output);
        });
        upload.getElement().addEventListener("file-remove", event -> {
            output.removeAll();
        });
        upload.addFinishedListener(event ->{
            createSpreadsheet(buffer, studentActivityRepository, studentResultRepository);

        });



        Select<String> labelSelect = new Select<>();
        labelSelect.setItems("Frequency distribution", "Measures of the central trend", "Distraction measures", "Correlation analysis");
        labelSelect.setLabel("Select an option: ");

        Button button = new Button("Calculate");
        Button button1 = new Button("Clear all");

        button.addClickListener(buttonClickEvent -> {


        });

        Div line = new Div();
        line.getStyle().set("width", "100%").set("border-top", "1px solid grey");

        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.add(button, button1);

        Label empty = new Label("");
        empty.setHeight("1em");

        add(title);
        add(empty);
        add(upload, output);
        add(labelSelect);
        add(horizontal);
        add(line);
    }

    private void createSpreadsheet(MemoryBuffer buffer, StudentActivityRepository studentActivityRepository,
                                   StudentResultRepository studentResultRepository) {
        InputStream inputStream = buffer.getInputStream();
        try {
            Spreadsheet spreadsheet = new Spreadsheet(inputStream);
            Sheet sheet = spreadsheet.getWorkbook().getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            int rowIndex = 0;
            boolean isStudentsResults = false;
            long ID = 0;
            float result = 0;
            boolean isStudentsActivity = false;
            String time = "";
            String eventContext = "";
            String component = "";
            String eventName = "";
            String description = "";
            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                int columnIndex = 0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if(rowIndex == 0 && columnIndex == 0) {
                        if(cell.getStringCellValue().equals("Time")) {
                            isStudentsActivity = true;
                        } else if(cell.getStringCellValue().equals("ID")) {
                            isStudentsResults = true;
                        } else {
                            //error
                        }
                    }
                    if(isStudentsActivity) {
                        switch (columnIndex) {
                            case 0:
                                time = cell.getStringCellValue();
                                break;
                            case 1:
                                eventContext = cell.getStringCellValue();
                                break;
                            case 2:
                                component = cell.getStringCellValue();
                                break;
                            case 3:
                                eventName = cell.getStringCellValue();
                                break;
                            case 4:
                                description = cell.getStringCellValue();
                                break;
                        }
                    } else if(isStudentsResults) {
                        switch (columnIndex) {
                            case 0:
                                cell.setCellType(CellType.NUMERIC);
                                ID = (int)cell.getNumericCellValue();
                                break;
                            case 1:
                                cell.setCellType(CellType.NUMERIC);
                                result = (float)cell.getNumericCellValue();
                                break;
                        }
                    }
                    if(!cellIterator.hasNext()) {
                        if(isStudentsActivity && rowIndex!=0) {
                            StudentActivityEntity sae = new StudentActivityEntity(time, eventContext, component, eventName, description);
                            studentActivityRepository.save(sae);
                        } else if(isStudentsResults && rowIndex!=0) {
                            StudentResultEntity sre = new StudentResultEntity(ID, result);
                            studentResultRepository.save(sre);
                        }
                    }

                    columnIndex++;
                }
                rowIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
