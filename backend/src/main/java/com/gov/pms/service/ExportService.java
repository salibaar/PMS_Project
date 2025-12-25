package com.gov.pms.service;

import com.gov.pms.entity.Objective;
import com.gov.pms.entity.Plan;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface ExportService {
    
    /**
     * Export plans to Excel format
     */
    byte[] exportPlansToExcel(List<Plan> plans) throws IOException;
    
    /**
     * Export objectives to Excel format
     */
    byte[] exportObjectivesToExcel(List<Objective> objectives) throws IOException;
    
    /**
     * Export plan details to PDF format
     */
    byte[] exportPlanToPDF(UUID planId) throws IOException;
    
    /**
     * Export objective details to PDF format
     */
    byte[] exportObjectiveToPDF(UUID objectiveId) throws IOException;
}
