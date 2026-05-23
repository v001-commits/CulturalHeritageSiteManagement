package cn.my.project.utils;

import cn.my.project.entity.SensorData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 列名到字段的映射
    private static final Map<String, String> COLUMN_MAPPING = new HashMap<>();
    static {
        COLUMN_MAPPING.put("区域编码", "areaCode");
        COLUMN_MAPPING.put("温度", "temperature");
        COLUMN_MAPPING.put("湿度", "humidity");
        COLUMN_MAPPING.put("降水量", "precipitation");
        COLUMN_MAPPING.put("风速", "windSpeed");
        COLUMN_MAPPING.put("风向", "windDirection");
        COLUMN_MAPPING.put("光照强度", "lightIntensity");
        COLUMN_MAPPING.put("PM2.5", "pm25");
        COLUMN_MAPPING.put("SO₂", "so2");
        COLUMN_MAPPING.put("NO₂", "no2");
        COLUMN_MAPPING.put("土壤墒情", "soilMoisture");
        COLUMN_MAPPING.put("土壤pH", "soilPh");
        COLUMN_MAPPING.put("水质等级", "waterQuality");
        COLUMN_MAPPING.put("裂缝宽度", "crackWidth");
        COLUMN_MAPPING.put("风化程度", "weatheringDegree");
        COLUMN_MAPPING.put("记录时间", "recordTime");
    }

    /**
     * 解析Excel文件（支持xlsx和xls）
     */
    public static List<SensorData> parseExcel(InputStream inputStream, String fileName, Map<String, Long> areaCodeToId) throws Exception {
        Workbook workbook;
        String lowerFileName = fileName.toLowerCase();

        if (lowerFileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (lowerFileName.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("不支持的文件格式，请使用.xlsx或.xls格式");
        }

        try {
            Sheet sheet = workbook.getSheetAt(0);
            return parseSheet(sheet, areaCodeToId);
        } finally {
            workbook.close();
        }
    }

    /**
     * 解析CSV文件
     */
    public static List<SensorData> parseCSV(InputStream inputStream, Map<String, Long> areaCodeToId) throws Exception {
        List<SensorData> dataList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        String headerLine = reader.readLine();
        if (headerLine == null) {
            throw new IllegalArgumentException("文件为空");
        }

        // 解析表头
        String[] headers = headerLine.split(",");
        Map<Integer, String> columnIndexToField = new HashMap<>();

        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            String field = COLUMN_MAPPING.get(header);
            if (field != null) {
                columnIndexToField.put(i, field);
            }
        }

        // 解析数据行
        String line;
        int rowNum = 1;
        while ((line = reader.readLine()) != null) {
            rowNum++;
            if (line.trim().isEmpty()) continue;

            String[] values = line.split(",");
            SensorData data = parseRow(values, columnIndexToField, areaCodeToId, rowNum);
            if (data != null) {
                dataList.add(data);
            }
        }

        return dataList;
    }

    private static List<SensorData> parseSheet(Sheet sheet, Map<String, Long> areaCodeToId) {
        List<SensorData> dataList = new ArrayList<>();

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("文件缺少表头行");
        }

        // 解析表头，建立列索引到字段的映射
        Map<Integer, String> columnIndexToField = new HashMap<>();
        for (Cell cell : headerRow) {
            String header = getCellValueAsString(cell).trim();
            String field = COLUMN_MAPPING.get(header);
            if (field != null) {
                columnIndexToField.put(cell.getColumnIndex(), field);
            }
        }

        // 解析数据行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            SensorData data = parseExcelRow(row, columnIndexToField, areaCodeToId, i + 1);
            if (data != null) {
                dataList.add(data);
            }
        }

        return dataList;
    }

    private static SensorData parseExcelRow(Row row, Map<Integer, String> columnIndexToField,
                                            Map<String, Long> areaCodeToId, int rowNum) {
        SensorData data = new SensorData();
        data.setDataSource("import");
        data.setCreateTime(LocalDateTime.now());
        boolean hasData = false;

        for (Map.Entry<Integer, String> entry : columnIndexToField.entrySet()) {
            int colIndex = entry.getKey();
            String field = entry.getValue();
            Cell cell = row.getCell(colIndex);
            String value = cell == null ? "" : getCellValueAsString(cell).trim();

            if (value.isEmpty()) continue;

            try {
                switch (field) {
                    case "areaCode":
                        Long areaId = areaCodeToId.get(value);
                        if (areaId == null) {
                            throw new IllegalArgumentException("第" + rowNum + "行：区域编码 '" + value + "' 不存在");
                        }
                        data.setAreaId(areaId);
                        hasData = true;
                        break;
                    case "temperature":
                        data.setTemperature(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "humidity":
                        data.setHumidity(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "precipitation":
                        data.setPrecipitation(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "windSpeed":
                        data.setWindSpeed(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "windDirection":
                        data.setWindDirection(value);
                        hasData = true;
                        break;
                    case "lightIntensity":
                        data.setLightIntensity(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "pm25":
                        data.setPm25(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "so2":
                        data.setSo2(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "no2":
                        data.setNo2(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "soilMoisture":
                        data.setSoilMoisture(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "soilPh":
                        data.setSoilPh(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "waterQuality":
                        data.setWaterQuality(value);
                        hasData = true;
                        break;
                    case "crackWidth":
                        data.setCrackWidth(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "weatheringDegree":
                        data.setWeatheringDegree(value);
                        hasData = true;
                        break;
                    case "recordTime":
                        data.setRecordTime(LocalDateTime.parse(value, DATE_TIME_FORMATTER));
                        hasData = true;
                        break;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("第" + rowNum + "行：" + field + " 值格式错误: " + value);
            } catch (Exception e) {
                throw new IllegalArgumentException("第" + rowNum + "行：" + e.getMessage());
            }
        }

        if (!hasData) {
            return null;
        }

        if (data.getAreaId() == null) {
            throw new IllegalArgumentException("第" + rowNum + "行：缺少必填字段 '区域编码'");
        }

        if (data.getRecordTime() == null) {
            data.setRecordTime(LocalDateTime.now());
        }

        return data;
    }

    private static SensorData parseRow(String[] values, Map<Integer, String> columnIndexToField,
                                       Map<String, Long> areaCodeToId, int rowNum) {
        SensorData data = new SensorData();
        data.setDataSource("import");
        data.setCreateTime(LocalDateTime.now());
        boolean hasData = false;

        for (Map.Entry<Integer, String> entry : columnIndexToField.entrySet()) {
            int colIndex = entry.getKey();
            String field = entry.getValue();
            String value = colIndex < values.length ? values[colIndex].trim() : "";

            if (value.isEmpty()) continue;

            try {
                switch (field) {
                    case "areaCode":
                        Long areaId = areaCodeToId.get(value);
                        if (areaId == null) {
                            throw new IllegalArgumentException("第" + rowNum + "行：区域编码 '" + value + "' 不存在");
                        }
                        data.setAreaId(areaId);
                        hasData = true;
                        break;
                    case "temperature":
                        data.setTemperature(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "humidity":
                        data.setHumidity(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "precipitation":
                        data.setPrecipitation(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "windSpeed":
                        data.setWindSpeed(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "windDirection":
                        data.setWindDirection(value);
                        hasData = true;
                        break;
                    case "lightIntensity":
                        data.setLightIntensity(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "pm25":
                        data.setPm25(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "so2":
                        data.setSo2(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "no2":
                        data.setNo2(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "soilMoisture":
                        data.setSoilMoisture(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "soilPh":
                        data.setSoilPh(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "waterQuality":
                        data.setWaterQuality(value);
                        hasData = true;
                        break;
                    case "crackWidth":
                        data.setCrackWidth(new BigDecimal(value));
                        hasData = true;
                        break;
                    case "weatheringDegree":
                        data.setWeatheringDegree(value);
                        hasData = true;
                        break;
                    case "recordTime":
                        data.setRecordTime(LocalDateTime.parse(value, DATE_TIME_FORMATTER));
                        hasData = true;
                        break;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("第" + rowNum + "行：" + field + " 值格式错误: " + value);
            } catch (Exception e) {
                throw new IllegalArgumentException("第" + rowNum + "行：" + e.getMessage());
            }
        }

        if (!hasData) {
            return null;
        }

        if (data.getAreaId() == null) {
            throw new IllegalArgumentException("第" + rowNum + "行：缺少必填字段 '区域编码'");
        }

        if (data.getRecordTime() == null) {
            data.setRecordTime(LocalDateTime.now());
        }

        return data;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().format(DATE_TIME_FORMATTER);
                }
                double num = cell.getNumericCellValue();
                if (num == (long) num) {
                    return String.valueOf((long) num);
                }
                return String.valueOf(num);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
