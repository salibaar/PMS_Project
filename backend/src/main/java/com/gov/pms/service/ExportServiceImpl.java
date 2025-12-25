package com.gov.pms.service;

import com.gov.pms.entity.KeyResult;
import com.gov.pms.entity.Objective;
import com.gov.pms.entity.Plan;
import com.gov.pms.exception.ResourceNotFoundException;
import com.gov.pms.repository.ObjectiveRepository;
import com.gov.pms.repository.PlanRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportServiceImpl implements ExportService {
    
    private final PlanRepository planRepository;
    private final ObjectiveRepository objectiveRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public byte[] exportPlansToExcel(List<Plan> plans) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Plans");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            String[] headers = {"Year", "Title", "Description", "Status", "Created At", "Updated At"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (Plan plan : plans) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(plan.getYear());
                row.createCell(1).setCellValue(plan.getTitle());
                row.createCell(2).setCellValue(plan.getDescription() != null ? plan.getDescription() : "");
                row.createCell(3).setCellValue(plan.getStatus().toString());
                row.createCell(4).setCellValue(plan.getCreatedAt() != null ? plan.getCreatedAt().format(DATE_FORMATTER) : "");
                row.createCell(5).setCellValue(plan.getUpdatedAt() != null ? plan.getUpdatedAt().format(DATE_FORMATTER) : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    @Override
    public byte[] exportObjectivesToExcel(List<Objective> objectives) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Objectives");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            String[] headers = {"Content", "Status", "Type", "Progress", "Order", "Created At"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (Objective objective : objectives) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(objective.getContent());
                row.createCell(1).setCellValue(objective.getStatus().toString());
                row.createCell(2).setCellValue(objective.getIsBreakthrough() ? "Breakthrough" : "Regular");
                row.createCell(3).setCellValue(objective.getProgress() + "%");
                row.createCell(4).setCellValue(objective.getOrderIndex());
                row.createCell(5).setCellValue(objective.getCreatedAt() != null ? objective.getCreatedAt().format(DATE_FORMATTER) : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    @Override
    public byte[] exportPlanToPDF(UUID planId) throws IOException {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Title
            Paragraph title = new Paragraph("PLAN REPORT: " + plan.getTitle())
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            
            // Plan details table
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
            table.setWidth(UnitValue.createPercentValue(100));
            
            addTableRow(table, "Year", String.valueOf(plan.getYear()));
            addTableRow(table, "Status", plan.getStatus().toString());
            addTableRow(table, "Description", plan.getDescription() != null ? plan.getDescription() : "N/A");
            addTableRow(table, "Created At", plan.getCreatedAt() != null ? plan.getCreatedAt().format(DATE_FORMATTER) : "N/A");
            addTableRow(table, "Updated At", plan.getUpdatedAt() != null ? plan.getUpdatedAt().format(DATE_FORMATTER) : "N/A");
            
            document.add(table);
            document.add(new Paragraph("\n"));
            
            // Objectives section
            if (plan.getObjectives() != null && !plan.getObjectives().isEmpty()) {
                document.add(new Paragraph("OBJECTIVES")
                        .setFontSize(14)
                        .setBold());
                
                Table objTable = new Table(UnitValue.createPercentArray(new float[]{50, 20, 15, 15}));
                objTable.setWidth(UnitValue.createPercentValue(100));
                
                // Header
                addHeaderCell(objTable, "Content");
                addHeaderCell(objTable, "Status");
                addHeaderCell(objTable, "Type");
                addHeaderCell(objTable, "Progress");
                
                // Data
                for (Objective objective : plan.getObjectives()) {
                    if (objective.getParent() == null) { // Only root objectives
                        objTable.addCell(new Cell().add(new Paragraph(objective.getContent())));
                        objTable.addCell(new Cell().add(new Paragraph(objective.getStatus().toString())));
                        objTable.addCell(new Cell().add(new Paragraph(objective.getIsBreakthrough() ? "Breakthrough" : "Regular")));
                        objTable.addCell(new Cell().add(new Paragraph(objective.getProgress() + "%")));
                    }
                }
                
                document.add(objTable);
            }
            
            document.close();
            return out.toByteArray();
        }
    }
    
    @Override
    public byte[] exportObjectiveToPDF(UUID objectiveId) throws IOException {
        Objective objective = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Objective not found with id: " + objectiveId));
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Title
            Paragraph title = new Paragraph("OBJECTIVE REPORT")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            
            // Objective details table
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
            table.setWidth(UnitValue.createPercentValue(100));
            
            addTableRow(table, "Content", objective.getContent());
            addTableRow(table, "Status", objective.getStatus().toString());
            addTableRow(table, "Type", objective.getIsBreakthrough() ? "Breakthrough Objective" : "Regular Objective");
            addTableRow(table, "Progress", objective.getProgress() + "%");
            addTableRow(table, "Order Index", String.valueOf(objective.getOrderIndex()));
            addTableRow(table, "Created At", objective.getCreatedAt() != null ? objective.getCreatedAt().format(DATE_FORMATTER) : "N/A");
            
            document.add(table);
            document.add(new Paragraph("\n"));
            
            // Key Results section
            if (objective.getKeyResults() != null && !objective.getKeyResults().isEmpty()) {
                document.add(new Paragraph("KEY RESULTS")
                        .setFontSize(14)
                        .setBold());
                
                Table krTable = new Table(UnitValue.createPercentArray(new float[]{50, 25, 25}));
                krTable.setWidth(UnitValue.createPercentValue(100));
                
                // Header
                addHeaderCell(krTable, "Description");
                addHeaderCell(krTable, "Target Value");
                addHeaderCell(krTable, "Current Value");
                
                // Data
                for (KeyResult kr : objective.getKeyResults()) {
                    krTable.addCell(new Cell().add(new Paragraph(kr.getDescription())));
                    krTable.addCell(new Cell().add(new Paragraph(String.valueOf(kr.getTargetValue()))));
                    krTable.addCell(new Cell().add(new Paragraph(String.valueOf(kr.getCurrentValue()))));
                }
                
                document.add(krTable);
            }
            
            // Child Objectives section
            if (objective.getChildren() != null && !objective.getChildren().isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("SUB-OBJECTIVES")
                        .setFontSize(14)
                        .setBold());
                
                Table childTable = new Table(UnitValue.createPercentArray(new float[]{60, 20, 20}));
                childTable.setWidth(UnitValue.createPercentValue(100));
                
                // Header
                addHeaderCell(childTable, "Content");
                addHeaderCell(childTable, "Status");
                addHeaderCell(childTable, "Progress");
                
                // Data
                for (Objective child : objective.getChildren()) {
                    childTable.addCell(new Cell().add(new Paragraph(child.getContent())));
                    childTable.addCell(new Cell().add(new Paragraph(child.getStatus().toString())));
                    childTable.addCell(new Cell().add(new Paragraph(child.getProgress() + "%")));
                }
                
                document.add(childTable);
            }
            
            document.close();
            return out.toByteArray();
        }
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private void addTableRow(Table table, String label, String value) {
        Cell labelCell = new Cell().add(new Paragraph(label).setBold());
        labelCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        table.addCell(labelCell);
        table.addCell(new Cell().add(new Paragraph(value)));
    }
    
    private void addHeaderCell(Table table, String text) {
        Cell cell = new Cell().add(new Paragraph(text).setBold());
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);
    }
}
