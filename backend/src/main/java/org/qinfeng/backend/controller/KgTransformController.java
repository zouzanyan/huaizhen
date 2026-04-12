package org.qinfeng.backend.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.qinfeng.backend.common.Result;
import org.qinfeng.backend.entity.KgEntityType;
import org.qinfeng.backend.service.IKgEntityTypeService;
import org.qinfeng.backend.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/api/kg/transform")
public class KgTransformController {

    @Autowired
    private Neo4jService neo4jService;

    @Autowired
    private IKgEntityTypeService kgEntityTypeService;

    @PostMapping("/excel")
    public Result<Map<String, Object>> transformExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") Long projectId,
            @RequestParam("entityType") String entityType,
            @RequestParam("nameColumn") String nameColumn,
            @RequestParam(value = "propertyColumns", required = false) String propertyColumns) {
        
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                columnIndex.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }
            
            int nameCol = columnIndex.getOrDefault(nameColumn, -1);
            if (nameCol == -1) {
                return Result.error("找不到名称列: " + nameColumn);
            }
            
            Set<String> propertyCols = new HashSet<>();
            if (propertyColumns != null && !propertyColumns.isEmpty()) {
                propertyCols.addAll(Arrays.asList(propertyColumns.split(",")));
            }
            
            KgEntityType et = kgEntityTypeService.listByProjectId(projectId).stream()
                    .filter(e -> e.getName().equals(entityType))
                    .findFirst()
                    .orElse(null);
            String color = et != null ? et.getColor() : "#409EFF";
            
            long nodeId = System.currentTimeMillis();
            int count = 0;
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Cell nameCell = row.getCell(nameCol);
                if (nameCell == null) continue;
                
                String name = getCellValueAsString(nameCell);
                if (name == null || name.isEmpty()) continue;
                
                Map<String, Object> properties = new HashMap<>();
                properties.put("id", nodeId);
                properties.put("projectId", projectId);
                properties.put("name", name);
                properties.put("entityTypeName", entityType);
                properties.put("color", color);
                
                for (String propCol : propertyCols) {
                    Integer colIdx = columnIndex.get(propCol.trim());
                    if (colIdx != null) {
                        Cell propCell = row.getCell(colIdx);
                        if (propCell != null) {
                            properties.put(propCol.trim(), getCellValueAsString(propCell));
                        }
                    }
                }
                
                neo4jService.createNodeWithId(entityType.toUpperCase(), nodeId, projectId, name, entityType, color);
                nodeId++;
                count++;
            }
            
            workbook.close();
            
            return Result.success(Map.of(
                "success", true,
                "count", count,
                "message", "成功导入 " + count + " 条数据"
            ));
            
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @PostMapping("/csv")
    public Result<Map<String, Object>> transformCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") Long projectId,
            @RequestParam("entityType") String entityType,
            @RequestParam("nameColumn") String nameColumn,
            @RequestParam(value = "propertyColumns", required = false) String propertyColumns) {
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return Result.error("文件为空");
            }
            
            String[] headers = headerLine.split(",");
            Map<String, Integer> columnIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                columnIndex.put(headers[i].trim(), i);
            }
            
            int nameCol = columnIndex.getOrDefault(nameColumn, -1);
            if (nameCol == -1) {
                return Result.error("找不到名称列: " + nameColumn);
            }
            
            Set<String> propertyCols = new HashSet<>();
            if (propertyColumns != null && !propertyColumns.isEmpty()) {
                propertyCols.addAll(Arrays.asList(propertyColumns.split(",")));
            }
            
            KgEntityType et = kgEntityTypeService.listByProjectId(projectId).stream()
                    .filter(e -> e.getName().equals(entityType))
                    .findFirst()
                    .orElse(null);
            String color = et != null ? et.getColor() : "#409EFF";
            
            long nodeId = System.currentTimeMillis();
            int count = 0;
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length <= nameCol) continue;
                
                String name = values[nameCol].trim();
                if (name.isEmpty()) continue;
                
                neo4jService.createNodeWithId(entityType.toUpperCase(), nodeId, projectId, name, entityType, color);
                nodeId++;
                count++;
            }
            
            reader.close();
            
            return Result.success(Map.of(
                "success", true,
                "count", count,
                "message", "成功导入 " + count + " 条数据"
            ));
            
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }

    @PostMapping("/excel/preview")
    public Result<Map<String, Object>> previewExcel(@RequestParam("file") MultipartFile file) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }
            
            List<Map<String, String>> previewData = new ArrayList<>();
            int previewRows = Math.min(5, sheet.getLastRowNum());
            for (int i = 1; i <= previewRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    rowData.put(headers.get(j), cell != null ? getCellValueAsString(cell) : "");
                }
                previewData.add(rowData);
            }
            
            workbook.close();
            
            return Result.success(Map.of(
                "headers", headers,
                "previewData", previewData,
                "totalRows", sheet.getLastRowNum()
            ));
            
        } catch (Exception e) {
            return Result.error("预览失败: " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
