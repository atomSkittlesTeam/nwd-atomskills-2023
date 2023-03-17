package com.example.atom.services;

import com.example.atom.dto.DemoDto;
import com.example.atom.entities.Demo;
import com.example.atom.entities.DemoRef;
import com.example.atom.repositories.DemoRefRepository;
import com.example.atom.repositories.DemoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.Instant;
import java.util.*;

@Service
@Log4j2
public class ParsingService {

    @Autowired
    DemoRepository demoRepository;

    @Autowired
    DemoRefRepository demoRefRepository;

    @Autowired
    ObjectMapper objectMapper;

    //относительные пути файлов, кладем их в resources
    private static final String PATH_EXCEL_WITH_ID = "excel/WithId.xlsx";
    private static final String PATH_EXCEL_WITH_ID_2_SHEETS = "excel/WithId2Sheets.xlsx";
    private static final String PATH_EXCEL_WITHOUT_ID = "excel/WithoutId.xlsx";

    private static final String PATH_JSON_WITH_ID = "json/WithId.json";
    private static final String PATH_JSON_WITHOUT_ID = "json/WithoutId.json";
    private static final String PATH_JSON_WITHOUT_ID_WITH_REF = "json/WithoutIdWithRef.json";

    //кол-во пропущенных строк
    private static int excelOffset = 1;

    //можем юзать вот такой шаблон для объекта
    private static Map<Integer, String> demoTemplateWithId = Map.of(
            0, "id",
            1, "stringProperty",
            2, "integerProperty",
            3, "floatProperty",
            4, "booleanProperty",
            5, "instantProperty",
            6, "dateProperty"
    );

    private static Map<Integer, String> demoTemplateWithoutId = Map.of(
            0, "stringProperty",
            1, "integerProperty",
            2, "floatProperty",
            3, "booleanProperty",
            4, "instantProperty",
            5, "dateProperty"
    );

    public void parseJson() {
        String absolutePath = getAbsoluteFileLocationByRelativePath(PATH_JSON_WITHOUT_ID_WITH_REF);
        parseJson(absolutePath);
    }

    public void parseJson(String absolutePath) {
        File file = new File(absolutePath);
        parseJsonFromFileOrString(file);
    }

    public void parseJsonFromFileOrString(Object json) {
        Long start = System.currentTimeMillis();
        try {
            //тут руками определяю родительские id, так как мы их до сохранения не знаем
            List<DemoDto> demoListDto = new ArrayList<>();
            if (json.getClass().equals(java.io.File.class)) {
                demoListDto = objectMapper.readValue((File) json, new TypeReference<>(){});
            } else {
                demoListDto = objectMapper.readValue((String) json, new TypeReference<>(){});
            }

            Map<Demo, DemoDto> demoMapToSave = new HashMap<>();
            demoListDto.forEach(dto -> {
                Demo demo = new Demo();
                demoMapToSave.put(demo.getDemoFromDto(dto), dto);
            });

            //сохраняю демо, потом беру их id
            demoRepository.saveAll(demoMapToSave.keySet());
            demoRepository.flush();
            //теперь знаю id и проставлю всем вложенным объектам
            List<DemoRef> demoRefList = new ArrayList<>();
            demoMapToSave.forEach((entity, dto) -> {
                dto.setId(entity.getId());
                if (dto.getDemoRefDtoList() != null && !dto.getDemoRefDtoList().isEmpty()) {
                    dto.getDemoRefDtoList().forEach(ref -> {
                        ref.setDemoId(dto.getId());
                        DemoRef demoRef = new DemoRef();
                        demoRefList.add(demoRef.getDemoRefFromDto(ref));
                    });
                }
            });
            demoRefRepository.saveAll(demoRefList);
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
        Long end = System.currentTimeMillis();
        log.info("Запись json заняла: " + (end - start) + " ms");
    }

    public void help(String file) {
        try {
            List<Demo> demoList = objectMapper.readValue(file, new TypeReference<>(){});
            demoRepository.saveAll(demoList);
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    public void parseExcel() {
        String absolutePath = getAbsoluteFileLocationByRelativePath(PATH_EXCEL_WITH_ID_2_SHEETS);
        parseExcel(absolutePath);
    }

    public void parseExcel(String absolutePath) {
        Long start = System.currentTimeMillis();
        try {
            List<Demo> resultList = new ArrayList<>();
            FileInputStream file = new FileInputStream(absolutePath);
            Workbook workbook = new XSSFWorkbook(file);
            int sheetCount = workbook.getNumberOfSheets();
            for(int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                int rowIndex = 0;
                Map<Integer, String> fieldByColumnNumberMap = demoTemplateWithId;
                for (Row row : sheet) {
                    sheet.getLastRowNum();
                    if (rowIndex >= excelOffset) { //пропускаем строчки
                        int cellIndex = 0;
                        Demo targetObject = new Demo();
                        Field[] objectFields = targetObject.getClass().getFields();
                        for (Cell cell : row) {
                            Object cellValue = getValueFromCell(cell);
                            //получаем поле из мапы, такое же поле должно быть в объекте
                            String fieldName = fieldByColumnNumberMap.get(cellIndex);

                            //если null, то такого филда нет в мапе. Но не буду обрывать цикл, чтобы иметь возможность
                            //вставить, например, 0,1,3,6,7 поля
                            if (cellValue != null && fieldName != null) {
                                //ищем такое же поле в объекте
                                List<Field> fieldsWithSameName = Arrays.stream(objectFields)
                                        .filter(e -> e.getName().equalsIgnoreCase(fieldName)).toList();
                                //если нашли однозначное соответствие
                                if (fieldsWithSameName.size() == 1) {
                                    setCellValueToFieldInObject(fieldsWithSameName.get(0), cellValue, targetObject);
                                }
                            }

                            cellIndex++;
                        }
                        if (!Objects.equals(targetObject, new Demo())) {
                            resultList.add(targetObject);
                        }
                    }
                    rowIndex++;
                }
            }
            demoRepository.saveAll(resultList);
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
        Long end = System.currentTimeMillis();
        log.info("Запись excel заняла: " + (end - start) + " ms");
    }

    private void setCellValueToFieldInObject(Field targetField, Object cellValue, Object targetObject) {
        targetField.setAccessible(true);
        Type type = targetField.getType();
        Object editedCellValue = cellValue;
        if (type.equals(Long.class) || type.equals(long.class)) {
            editedCellValue = ((Double) cellValue).longValue();
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            editedCellValue = ((Double) cellValue).intValue();
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            editedCellValue = ((Double) cellValue).floatValue();
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            editedCellValue = cellValue;
        } else if (type.equals(Instant.class)) {
            editedCellValue = DateUtil.getJavaDate((Double) cellValue).toInstant();
        } else if (type.equals(Date.class)) {
            editedCellValue = DateUtil.getJavaDate((Double) cellValue);
        }
        try {
            targetField.set(targetObject, editedCellValue);
        } catch (IllegalAccessException exception) {
            log.error(exception.getMessage());
        }
    }

    private Object getValueFromCell(Cell cell) {
        Object result;
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING -> {
                result = cell.getStringCellValue();
                int i = 0;
            }
            case NUMERIC -> {
                result = cell.getNumericCellValue();
                int i = 0;
            }
            case BOOLEAN -> { //на практике не встретилось (выяснить, почему)
                result = cell.getBooleanCellValue();
                int i = 0;
            }
            case FORMULA -> {
                result = cell.getBooleanCellValue();
                int i = 0;
            }
            default -> {
                result = null;
                int i = 0;
            }
        }
        return result;
    }

    private String getAbsoluteFileLocationByRelativePath(String relativePath) {
        URL resource = getClass().getClassLoader().getResource(relativePath);
        if (resource != null) {
            return resource.getPath();
        } else {
            return null;
        }
    }


    //создаем отношение (номер колонки - название поля в объекте),
    // работает только если названия в объекте и заголовки столбцом равны
    //в процессе...
    private Map<Integer, String> getRelativeDtoFieldByExcelColumnPosition(Row row) {
        Map<Integer, String> resultMap = new HashMap<>();
        int index = 0;
        for (Cell cell : row) {
            resultMap.put(index, cell.getStringCellValue());
            index++;
        }
        return resultMap;
    }
}
