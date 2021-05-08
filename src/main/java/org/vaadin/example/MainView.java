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
import org.apache.commons.math3.util.Precision;
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
import java.util.Arrays;
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

    public MainView(PlatformRepository platformRepository,
                    StudentActivityRepository studentActivityRepository, StudentResultRepository studentResultRepository) {
        Html title = new Html("<span style=\"font-size: 30px; font-family: Cursive;\">Data analysis for e-learning management</span>");
        platformRepository.deleteAll();
        studentActivityRepository.deleteAll();
        studentResultRepository.deleteAll();
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(10);
        upload.setDropLabel(new Label("Upload up to 10 files in .xlsx format"));
        Div output = new Div();

        upload.getElement().addEventListener("file-remove", event -> {
            output.removeAll();
        });
        upload.addFinishedListener(event -> {
            createSpreadsheet(buffer, studentActivityRepository, studentResultRepository);

        });


        Select<String> labelSelect = new Select<>();
        labelSelect.setItems("Frequency distribution", "Measures of the central trend", "Distraction measures", "Correlation analysis", "Summarizing information");
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
            field.setValue("");
            field1.setValue("");
            field2.setValue("");
        });

        HorizontalLayout horizontal1 = new HorizontalLayout();
        horizontal1.add(field, field1, field2);

        List<Integer> frequency = new ArrayList<>();
        Grid<Frequency> grid = new Grid<>();
        //grid.setSizeFull();
        grid.addColumn(Frequency::getExercises).setHeader("Number of Exercises");
        grid.addColumn(Frequency::getAbsoluteFrequency).setHeader("Absolute frequency f");
        grid.addColumn(Frequency::getRelativeFrequency).setHeader("Relative frequency p (In %)");
        grid.setPageSize(20);
        grid.setHeightByRows(true);
        grid.setWidth("700px");

        List<Integer> centralTrend = new ArrayList<>();
        Grid<CentralTrend> gridCentralTrend = new Grid<>();
        gridCentralTrend.addColumn(CentralTrend::getMode).setHeader("Mode");
        gridCentralTrend.addColumn(CentralTrend::getMedian).setHeader("Median");
        gridCentralTrend.addColumn(CentralTrend::getAverage).setHeader("Average");
        gridCentralTrend.setHeightByRows(true);
        gridCentralTrend.setWidth("700px");


        button.addClickListener(buttonClickEvent -> {
            switch (labelSelect.getValue()) {
                case "Frequency distribution":
                    List<Frequency> frequencies = new ArrayList<>();
                    int[] absoluteFrequencies = getAnalysisValues(studentActivityRepository); //2
                    int sumOfFrequencies = 0;
                    for (int absFreq : absoluteFrequencies) {
                        sumOfFrequencies += absFreq;
                    }
                    for (int i = 0; i < 5; i++) {
                        Frequency currFrequency = new Frequency(
                                i + 1,
                                absoluteFrequencies[i],
                                Precision.round((((double) absoluteFrequencies[i] / sumOfFrequencies) * 100),2));
                        frequencies.add(currFrequency);
                    }

                    grid.setItems(frequencies);
                    break;
                case "Measures of the central trend":
                    List<CentralTrend> listCentral = new ArrayList<>();
                    int[] array = getAnalysisValues(studentActivityRepository);
                    int mode = 0;
                    int sum = 0;

                    int totalCount = 0;
                    int currCount = 0;
                    Arrays.sort(array);


                    if (array[0] < 2 && array[1] < 2 && array[2] < 2 && array[3] < 2 && array[4] < 2) {
                        mode = 0;
                    } else {
                        for (int i = 0; i < 4; i++) {
                            if (array[i] == array[i + 1]) {
                                currCount++;
                            } else {
                                if (totalCount < currCount) {
                                    totalCount = currCount;
                                    mode = array[i];
                                }
                                currCount = 0;
                            }
                        }
                        if (totalCount < currCount) {
                            mode = array[4];
                        }
                    }

                    Arrays.sort(array);

                    for (int i = 0; i < 5; i++) {
                        sum += array[i];
                    }

                    CentralTrend trend = new CentralTrend(mode, array[2], sum / 5.0);
                    gridCentralTrend.setItems(trend);
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
        add(gridCentralTrend);
    }


    private int[] getAnalysisValues(StudentActivityRepository studentActivityRepository) {
        int[] frequency = {0, 0, 0, 0, 0};
        List<Integer> values = studentActivityRepository.findFrequencyDistributionValues();
        for (int i = 0; i < values.size(); i++) {
            switch (values.get(i)) {
                case 1:
                    frequency[0] += 1;
                    break;
                case 2:
                    frequency[1] += 1;
                    break;
                case 3:
                    frequency[2] += 1;
                    break;
                case 4:
                    frequency[3] += 1;
                    break;
                case 5:
                    frequency[4] += 1;
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
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                int columnIndex = 0;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (rowIndex == 0 && columnIndex == 0) {
                        if (cell.getStringCellValue().equals("Time")) {
                            isStudentsActivity = true;
                        } else if (cell.getStringCellValue().equals("ID")) {
                            isStudentsResults = true;
                        } else {
                            //error
                        }
                    }
                    if (isStudentsActivity) {
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
                    } else if (isStudentsResults) {
                        switch (columnIndex) {
                            case 0:
                                cell.setCellType(CellType.NUMERIC);
                                ID = (int) cell.getNumericCellValue();
                                break;
                            case 1:
                                cell.setCellType(CellType.NUMERIC);
                                result = (float) cell.getNumericCellValue();
                                break;
                        }
                    }
                    if (!cellIterator.hasNext()) {
                        if (isStudentsActivity && rowIndex != 0) {
                            StudentActivityEntity sae = new StudentActivityEntity(time, eventContext, component, eventName, description);
                            studentActivityRepository.save(sae);
                        } else if (isStudentsResults && rowIndex != 0) {
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
