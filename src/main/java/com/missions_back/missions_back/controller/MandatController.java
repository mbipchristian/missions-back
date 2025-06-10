package com.missions_back.missions_back.controller;

import com.missions_back.missions_back.dto.MandatDto;
import com.missions_back.missions_back.dto.MandatResponseDto;
import com.missions_back.missions_back.service.MandatService;
import com.missions_back.missions_back.service.PdfGenerationService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/mandats")
@CrossOrigin(origins = "*")
public class MandatController {

    @Autowired
    private MandatService mandatService;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @GetMapping("/all")
    public ResponseEntity<List<MandatResponseDto>> getAllMandats() {
        try {
            List<MandatResponseDto> mandats = mandatService.getAllMandats();
            return ResponseEntity.ok(mandats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MandatResponseDto> getMandatById(@PathVariable Long id) {
        try {
            MandatResponseDto mandat = mandatService.getMandatById(id);
            if (mandat != null) {
                return ResponseEntity.ok(mandat);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<MandatResponseDto> createMandat(@RequestBody MandatDto mandatDto) {
        try {
            MandatResponseDto createdMandat = mandatService.createMandat(mandatDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMandat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<ByteArrayResource> downloadMandatPdf(@PathVariable Long id) {
        try {
            // Récupérer les détails du mandat
            MandatResponseDto mandat = mandatService.getMandatById(id);
            if (mandat == null) {
                return ResponseEntity.notFound().build();
            }

            // Générer le PDF avec toutes les étapes incluses
            byte[] pdfBytes = pdfGenerationService.generateMandatPdf(mandat);
            
            // Créer la ressource ByteArray
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            // Définir les headers pour le téléchargement
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=mandat-" + mandat.reference() + "-complet.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfBytes.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MandatResponseDto> updateMandat(
            @PathVariable Long id, 
            @RequestBody MandatDto mandatDto) {
        try {
            MandatResponseDto updatedMandat = mandatService.updateMandat(id, mandatDto);
            if (updatedMandat != null) {
                return ResponseEntity.ok(updatedMandat);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMandat(@PathVariable Long id) {
        try {
            boolean deleted = mandatService.softDeleteMandat(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
