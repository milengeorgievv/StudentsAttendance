package org.vaadin.example;

import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
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
import org.vaadin.example.entity.PlatformEntity;
import org.vaadin.example.entity.StudentActivityEntity;
import org.vaadin.example.entity.StudentResultEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        Div output = new Div();

        upload.getElement().addEventListener("file-remove", event -> {
            output.removeAll();
        });
        upload.addFinishedListener(event ->{
            createSpreadsheet(buffer, studentActivityRepository, studentResultRepository);

        });



        Select<String> labelSelect = new Select<>();
        labelSelect.setItems("Frequency distribution", "Measures of the central trend", "Distraction measures", "Correlation analysis","Summarizing information");
        labelSelect.setLabel("Select an option: ");

        Button button = new Button("Calculate");
        Button button1 = new Button("Clear all");

        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.add(button, button1);

        TextField field = new TextField();
        field.setLabel("Subject name");
        TextField field1 = new TextField();
        field1.setLabel("Platform name");
        TextField field2 = new TextField();
        field2.setLabel("URL address");
        Button buttonSubmit = new Button("Submit");

        buttonSubmit.addClickListener(buttonClickEvent -> {
            PlatformEntity pl = new PlatformEntity(field.getValue(), field1.getValue(), field2.getValue());
            platformRepository.save(pl);
        });

        HorizontalLayout horizontal1 = new HorizontalLayout();
        horizontal1.add(field,field1,field2);
        List<Integer> frequency = new ArrayList<>();
        Grid<Frequency> grid = new Grid<>();
        grid.setSizeFull();
        grid.addColumn(Frequency::getExercises).setHeader("Number of Exercises");
        grid.addColumn(Frequency::getAbsoluteFrequency).setHeader("Absolute frequency");
        grid.addColumn(Frequency::getRelativeFrequency).setHeader("Relative frequency");
        grid.setPageSize(20);

        button.addClickListener(buttonClickEvent -> {
            switch(labelSelect.getValue()) {
                case "Frequency distribution":
                    //1
                    getFrequencyDistributionValues(studentActivityRepository); //2
                    //3
                    //setColumns  1 - 2 - 3

                    grid.setItems();
                    break;
                case "Measures of the central trend":
                    break;
                case "Distraction measures":
                    break;
                case "Correlation analysis":
                    break;
                case "Summarizing information":
                    break;
            }
        });



        Label empty = new Label("");
        empty.setHeight("1em");
        Label empty1 = new Label("");
        empty.setHeight("1em");

        add(title);
        add(empty);
        add(upload, output);
        add(horizontal1);
        add(buttonSubmit);
        add(empty1);
        add(labelSelect);
        add(horizontal);
        add(grid);
    }

    private int[] getFrequencyDistributionValues(StudentActivityRepository studentActivityRepository) {
        int[] frequency = {0,0,0,0,0};
        List<Integer> values = studentActivityRepository.findFrequencyDistributionValues();
        for(int i = 0; i< values.size();i++) {
            switch (values.get(i)) {
                case 1:
                    frequency[0]+=1;
                    break;
                case 2:
                    frequency[1]+=1;
                    break;
                case 3:
                    frequency[2]+=1;
                    break;
                case 4:
                    frequency[3]+=1;
                    break;
                case 5:
                    frequency[4]+=1;
                    break;
            }
        }
        return frequency;

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
