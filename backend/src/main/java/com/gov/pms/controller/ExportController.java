package com.gov.pms.controller;

import com.gov.pms.entity.Objective;
import com.gov.pms.entity.Plan;
import com.gov.pms.service.ExportService;
import com.gov.pms.service.ObjectiveService;
import com.gov.pms.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
@Tag(name = "Export", description = "API xuất dữ liệu ra Excel và PDF")
public class ExportController {
    
    private final ExportService exportService;
    private final PlanService planService;
    private final ObjectiveService objectiveService;
    
    @GetMapping("/plans/excel")
    @Operation(summary = "Xuất danh sách kế hoạch ra Excel", 
               description = "Export tất cả hoặc theo bộ lọc sang file Excel")
    public ResponseEntity<byte[]> exportPlansToExcel(
            @Parameter(description = "Năm") @RequestParam(required = false) Integer year,
            @Parameter(description = "Trạng thái") @RequestParam(required = false) String status) throws IOException {
        
        List<Plan> plans;
        if (year != null) {
            plans = planService.getPlansByYear(year);
        } else {
            // Get all plans (limit to reasonable number for export)
            Page<Plan> planPage = planService.getAllPlans(Pageable.ofSize(1000));
            plans = planPage.getContent();
        }
        
        byte[] excelData = exportService.exportPlansToExcel(plans);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "plans_export.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
    
    @GetMapping("/objectives/excel")
    @Operation(summary = "Xuất danh sách nhiệm vụ ra Excel",
               description = "Export objectives theo plan sang file Excel")
    public ResponseEntity<byte[]> exportObjectivesToExcel(
            @Parameter(description = "ID của Plan") @RequestParam(required = false) UUID planId) throws IOException {
        
        List<Objective> objectives;
        if (planId != null) {
            objectives = objectiveService.getObjectivesByPlanId(planId);
        } else {
            // Get all objectives for breakthrough
            objectives = objectiveService.getAllObjectives();
        }
        
        byte[] excelData = exportService.exportObjectivesToExcel(objectives);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "objectives_export.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
    
    @GetMapping("/plans/{id}/pdf")
    @Operation(summary = "Xuất chi tiết kế hoạch ra PDF",
               description = "Export một kế hoạch cụ thể với objectives sang file PDF")
    public ResponseEntity<byte[]> exportPlanToPDF(
            @Parameter(description = "ID của Plan") @PathVariable UUID id) throws IOException {
        
        byte[] pdfData = exportService.exportPlanToPDF(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "plan_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }
    
    @GetMapping("/objectives/{id}/pdf")
    @Operation(summary = "Xuất chi tiết nhiệm vụ ra PDF",
               description = "Export một objective cụ thể với key results sang file PDF")
    public ResponseEntity<byte[]> exportObjectiveToPDF(
            @Parameter(description = "ID của Objective") @PathVariable UUID id) throws IOException {
        
        byte[] pdfData = exportService.exportObjectiveToPDF(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "objective_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
    }
}
