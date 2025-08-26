package com.missions_back.missions_back.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.missions_back.missions_back.dto.DecomptesPreCreationRequest;
import com.missions_back.missions_back.dto.DecomptesResponse;
import com.missions_back.missions_back.dto.OrdreMissionResponseDto;
import com.missions_back.missions_back.dto.OrdreMissionUpdateDto;
import com.missions_back.missions_back.dto.PieceJointeResponseDto;
import com.missions_back.missions_back.dto.RangResponseDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.dto.UserResponseDto;
import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.OrdreMission;
import com.missions_back.missions_back.model.OrdreMissionStatut;
import com.missions_back.missions_back.model.PieceJointe;
import com.missions_back.missions_back.model.Ressource;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.model.Ville;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.OrdreMissionRepo;
import com.missions_back.missions_back.repository.UserRepo;
import com.missions_back.missions_back.repository.VilleRepo;

import jakarta.persistence.EntityNotFoundException;

import com.itextpdf.text.BaseColor;
// Imports pour la génération PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.missions_back.missions_back.service.EmailService;

@Service
public class OrdreMissionService {
    private final OrdreMissionRepo ordreMissionRepo;
    private final UserRepo userRepo;
    private final MandatRepo mandatRepo;
    private final EmailService emailService;
    private final VilleRepo villeRepo;

    public OrdreMissionService(OrdreMissionRepo ordreMissionRepo, UserRepo userRepo, MandatRepo mandatRepo, EmailService emailService, VilleRepo villeRepo) {
        this.ordreMissionRepo = ordreMissionRepo;
        this.userRepo = userRepo;
        this.mandatRepo = mandatRepo;
        this.emailService = emailService;
        this.villeRepo = villeRepo;
    }
    /**
     * Récupérer tous les ordres de missions en attente de justificatif
     */
    public List<OrdreMissionResponseDto> getOrdresMissionEnAttenteJustificatif() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatut(OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les ordres de missions en attente d'exécution
     */
    public List<OrdreMissionResponseDto> getOrdresMissionEnAttenteExecution() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatut(OrdreMissionStatut.EN_ATTENTE_EXECUTION);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les ordres de missions en cours
     */
    public List<OrdreMissionResponseDto> getOrdresMissionEnCours() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatut(OrdreMissionStatut.EN_COURS);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les ordres de missions achevés
     */
    public List<OrdreMissionResponseDto> getOrdresMissionAcheves() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatut(OrdreMissionStatut.ACHEVE);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }


    public List<OrdreMissionResponseDto> getOrdresMissionParMandat(Long mandatId) {
        // Vérifier que le mandat existe
        if (!mandatRepo.existsById(mandatId)) {
            throw new EntityNotFoundException("Mandat non trouvé avec l'ID: " + mandatId);
        }
        
        // Récupérer les ordres de mission du mandat
        List<OrdreMission> ordres = ordreMissionRepo.findByMandatId(mandatId);
        
        // Convertir en DTO
        return ordres.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // @Transactional
    // public OrdreMissionResponseDto updateOrdreMission(Long ordreMissionId, OrdreMissionUpdateDto updateDto, Authentication authentication) {
    //     // Extraire l'utilisateur depuis le token JWT
    //     String userEmail = authentication.getName();
    //     User user = userRepo.findByEmail(userEmail)
    //         .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        
    //     // Récupérer l'ordre de mission existant
    //     OrdreMission ordreMission = ordreMissionRepo.findById(ordreMissionId)
    //         .orElseThrow(() -> new EntityNotFoundException("Ordre de mission non trouvé avec l'ID: " + ordreMissionId));

    //     // Mise à jour des champs si ils sont fournis dans le DTO
    //     if (updateDto.getObjectif() != null) {
    //         ordreMission.setObjectif(updateDto.getObjectif());
    //     }
        
    //     if (updateDto.getModePaiement() != null) {
    //         ordreMission.setModePaiement(updateDto.getModePaiement());
    //     }
        
    //     if (updateDto.getDevise() != null) {
    //         ordreMission.setDevise(updateDto.getDevise());
    //     }
        
    //     if (updateDto.getDateDebut() != null) {
    //         ordreMission.setDateDebut(updateDto.getDateDebut());
    //     }
        
    //     if (updateDto.getDateFin() != null) {
    //         ordreMission.setDateFin(updateDto.getDateFin());
    //     }
        
    //     if (updateDto.getDuree() != null) {
    //         ordreMission.setDuree(updateDto.getDuree());
    //     }
        
    //     if (updateDto.getTauxAvance() != null) {
    //         ordreMission.setTauxAvance(updateDto.getTauxAvance());
    //     }
        
    //     if (updateDto.getDecompteTotal() != null) {
    //         ordreMission.setDecompteTotal(updateDto.getDecompteTotal());
    //     }
        
    //     if (updateDto.getDecompteAvance() != null) {
    //         ordreMission.setDecompteAvance(updateDto.getDecompteAvance());
    //     }
        
    //     if (updateDto.getDecompteRelicat() != null) {
    //         ordreMission.setDecompteRelicat(updateDto.getDecompteRelicat());
    //     }
        
    //     if (updateDto.getStatut() != null) {
    //         ordreMission.setStatut(updateDto.getStatut());
    //         // Notifier l'utilisateur concerné
    //         if (ordreMission.getUser() != null) {
    //             emailService.sendEmail(
    //                 ordreMission.getUser().getEmail(),
    //                 "Changement de statut de l'ordre de mission",
    //                 "L'ordre de mission " + ordreMission.getReference() + " est maintenant au statut : " + updateDto.getStatut().getLibelle()
    //             );
    //         }
    //         // Notifier le DRH si le statut est EN_ATTENTE_CONFIRMATION
    //         if (updateDto.getStatut() == com.missions_back.missions_back.model.OrdreMissionStatut.EN_ATTENTE_CONFIRMATION) {
    //             var drhList = userRepo.findByRole_NameAndActifTrue(com.missions_back.missions_back.model.RoleEnum.DIRECTEUR_RESSOURCES_HUMAINES);
    //             if (!drhList.isEmpty()) {
    //                 emailService.sendEmail(
    //                     drhList.stream().map(User::getEmail).toList(),
    //                     "Ordre de mission à confirmer",
    //                     "Un ordre de mission (" + ordreMission.getReference() + ") attend votre confirmation."
    //                 );
    //             }
    //         }
    //     }

    //     // Mettre à jour la date de modification
    //     ordreMission.setUpdated_at(LocalDateTime.now());
        
    //     // Sauvegarder les modifications
    //     OrdreMission updatedOrdreMission = ordreMissionRepo.save(ordreMission);
        
    //     return mapToResponseDto(updatedOrdreMission);
    // }

    /**
     * Créer un nouvel ordre de mission pour un utilisateur et un mandat donnés
     */
    @Transactional
    public OrdreMissionResponseDto creerOrdreMission(Mandat mandat, User missionUser, String reference, String objectif, String modePaiement, String devise, Long tauxAvance, Date dateDebut, Date dateFin, Long duree, Long decompteTotal, Long decompteAvance, Long decompteRelicat, Authentication authentication) {
    // 2. Valider l'utilisateur
    String validationError = validerUtilisateur(missionUser, mandat, dateDebut);
    if (validationError != null) {
        throw new IllegalArgumentException("Impossible de créer l'ordre de mission : " + validationError);
    }
    
    OrdreMission ordreMission = new OrdreMission();
    ordreMission.setReference(reference);
    ordreMission.setObjectif(objectif);
    ordreMission.setModePaiement(modePaiement);
    ordreMission.setDevise(devise);
    ordreMission.setTauxAvance(tauxAvance);
    ordreMission.setDateDebut(dateDebut);
    ordreMission.setDateFin(dateFin);
    ordreMission.setDuree(duree);

    // Calculs financiers par défaut - CORRECTION: utiliser les villes de l'ordre de mission
    decompteTotal = calculateDecompteTotal(missionUser, ordreMission);
    decompteAvance = decompteTotal * ordreMission.getTauxAvance() / 100;
    decompteRelicat = decompteTotal - decompteAvance;

    ordreMission.setDecompteTotal(decompteTotal);
    ordreMission.setDecompteAvance(decompteAvance);
    ordreMission.setDecompteRelicat(decompteRelicat);
    ordreMission.setStatut(OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF);
    ordreMission.setUser(missionUser);
    ordreMission.setMandat(mandat);
    ordreMission.setActif(true);
    
    OrdreMission saved = ordreMissionRepo.save(ordreMission);
    
    // CORRECTION: Utiliser la durée de l'ordre de mission, pas du mandat
    missionUser.setQuotaAnnuel(missionUser.getQuotaAnnuel() + duree);
    userRepo.save(missionUser);

    // Notifier l'utilisateur de la création de l'ordre de mission
    emailService.sendEmail(
        missionUser.getEmail(),
        "Ordre de mission créé",
        "Votre ordre de mission " + ordreMission.getReference() + " a été créé avec succès."
    );
    
    return mapToResponseDto(saved);
}

private Long calculateDecompteTotal(User user, OrdreMission ordreMission) {
    // Récupérer les villes spécifiquement sélectionnées pour cet ordre de mission
    List<Ville> villesSelectionnees = ordreMission.getVilles(); // Utiliser les villes de l'ordre de mission
    
    if (villesSelectionnees == null || villesSelectionnees.isEmpty()) {
        // Fallback vers les villes du mandat si aucune ville spécifique n'est définie
        villesSelectionnees = ordreMission.getMandat() != null ? 
                             ordreMission.getMandat().getVilles() : new ArrayList<>();
    }
    
    // Vérifier s'il y a au moins une ville extérieure parmi les villes sélectionnées
    boolean hasVilleExterieure = villesSelectionnees.stream()
            .anyMatch(ville -> !ville.isInterieur());
    
    if (hasVilleExterieure) {
        return calculateFraisExterne(ordreMission, user);
    } else {
        return calculateFraisInterne(ordreMission, user);
    }
}
public DecomptesResponse calculerDecomptesPreCreation(DecomptesPreCreationRequest request) {
    // Récupérer l'utilisateur et le mandat
    User user = userRepo.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    
    // Récupérer les villes sélectionnées par leurs IDs
    List<Ville> villesSelectionnees = villeRepo.findAllById(request.getVilleIds());
    
    // Vérifier s'il y a au moins une ville extérieure parmi les villes sélectionnées
    boolean hasVilleExterieure = villesSelectionnees.stream()
            .anyMatch(ville -> !ville.isInterieur());
    
    // Calculer selon le type de ville
    Long tauxJournalier;
    if (hasVilleExterieure) {
        tauxJournalier = user.getRang().getFraisExterne().longValue();
    } else {
        tauxJournalier = user.getRang().getFraisInterne().longValue();
    }
    
    Long decompteTotal = tauxJournalier * request.getDuree();
    Long decompteAvance = Math.round((decompteTotal * request.getTauxAvance()) / 100.0);
    Long decompteRelicat = decompteTotal - decompteAvance;
    
    return new DecomptesResponse(decompteTotal, decompteAvance, decompteRelicat);
}

public DecomptesResponse getOrdreMissionDecomptes(Long ordreMissionId, Long userId, Double tauxAvance) {
        // Implémentation de l'ancienne méthode pour compatibilité
        OrdreMission ordreMission = ordreMissionRepo.findById(ordreMissionId)
                .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé"));
        
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Long decompteTotal = calculateDecompteTotal(user, ordreMission);
        Long decompteAvance = Math.round((decompteTotal * tauxAvance) / 100.0);
        Long decompteRelicat = decompteTotal - decompteAvance;
        
        return new DecomptesResponse(decompteTotal, decompteAvance, decompteRelicat);
    }

private Long calculateFraisExterne(OrdreMission ordreMission, User user) {
    // Exemple de calcul pour les frais externes
    Long fraisExterne = user.getRang().getFraisExterne().longValue();
    
    // Utiliser la durée de l'ordre de mission
    return fraisExterne * ordreMission.getDuree();
}

private Long calculateFraisInterne(OrdreMission ordreMission, User user) {
    // Exemple de calcul pour les frais internes
    Long fraisInterne = user.getRang().getFraisInterne().longValue();
    
    // Utiliser la durée de l'ordre de mission
    return fraisInterne * ordreMission.getDuree();
}

    private String validerUtilisateur(User user, Mandat mandat, Date dateDebutOrdreMission) {
        // Vérification 1 : Ordre de mission en cours
        Optional<OrdreMission> dernierOrdreMission = ordreMissionRepo
            .findTopByUserAndActifTrueOrderByDateFinDesc(user);
        
        if (dernierOrdreMission.isPresent()) {
            Date dateFinDernierOrdre = dernierOrdreMission.get().getDateFin();
            // Date dateDebutMandat = mandat.getDateDebut();
            
            // Convert to LocalDate for comparison
            LocalDate finDernierOrdre = dateFinDernierOrdre.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            LocalDate debutNouvelOrdre = dateDebutOrdreMission.toInstant()  // ✅ Correction
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
            
            if (!debutNouvelOrdre.isAfter(finDernierOrdre)) {
                return "Ordre de mission en cours jusqu'au " + 
                       finDernierOrdre.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            
        }
        
        // Vérification 2 : Quota annuel
        Long quotaApresMandat = user.getQuotaAnnuel() + mandat.getDuree();
        if (quotaApresMandat > 100) {
            return "Dépassement du quota annuel (actuel: " + user.getQuotaAnnuel() + 
                   " jours, après mandat: " + quotaApresMandat + " jours)";
        }
        
        return null; // Utilisateur conforme
    }

    /**
 * Calcule les décomptes pour un utilisateur et un ordre de mission (sans sauvegarder l'ordre de mission)
 */
public Map<String, Long> calculerDecomptes(User user, OrdreMission ordreMission, Long tauxAvance) {
    Long decompteTotal = calculateDecompteTotal(user, ordreMission);
    Long decompteAvance = decompteTotal * tauxAvance / 100;
    Long decompteRelicat = decompteTotal - decompteAvance;
    
    return Map.of(
        "decompteTotal", decompteTotal,
        "decompteAvance", decompteAvance,
        "decompteRelicat", decompteRelicat
    );
}

    /**
     * Générer et télécharger un PDF de l'ordre de mission
     */
   public ResponseEntity<ByteArrayResource> telechargerPdf(Long ordreMissionId) {
    try {
        // Récupérer l'ordre de mission
        OrdreMission ordreMission = ordreMissionRepo.findById(ordreMissionId)
            .orElseThrow(() -> new EntityNotFoundException("Ordre de mission non trouvé avec l'ID: " + ordreMissionId));

        // Récupérer l'utilisateur associé à l'ordre de mission
        User utilisateur = ordreMission.getUser();
        if (utilisateur == null) {
            throw new EntityNotFoundException("Utilisateur non trouvé pour cet ordre de mission");
        }

        // Générer le PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 25, 25);
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Styles de police - Réduites pour optimiser l'espace
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font mediumFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        
        // === EN-TÊTE OFFICIEL ===
        createOfficialHeader(document, titleFont, headerFont, normalFont, smallFont, mediumFont);
        
        // === NUMÉRO DE RÉFÉRENCE ET DATE ===
        PdfPTable refTable = new PdfPTable(2);
        refTable.setWidthPercentage(100);
        refTable.setSpacingBefore(8);
        refTable.setSpacingAfter(10);

        //Mandat mandat = ordreMission.getMandat();
        
        PdfPCell refCell = new PdfPCell(new Phrase("N°" + ""));
        refCell.setBorder(Rectangle.NO_BORDER);
        refCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());
        PdfPCell dateCell = new PdfPCell(new Phrase("Yaoundé, le " + currentDate, normalFont));
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        refTable.addCell(refCell);
        refTable.addCell(dateCell);
        document.add(refTable);
        
        // === TITRE ENCADRÉ "ORDRE DE MISSION" ===
        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(50);
        titleTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleTable.setSpacingBefore(10);
        titleTable.setSpacingAfter(15);
        
        PdfPCell titleCell = new PdfPCell(new Phrase("ORDRE DE MISSION", titleFont));
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.setPadding(6);
        titleCell.setBorderWidth(2);
        titleCell.setBorderColor(BaseColor.BLACK);
        titleCell.setMinimumHeight(30);
        
        titleTable.addCell(titleCell);
        document.add(titleTable);
        
        // === SECTION PRINCIPALE - INFORMATIONS AGENT ===
        createMainInfoSection(document, ordreMission, utilisateur, normalFont, smallFont);
        
        // === SECTION SIGNATURE ===
        createSignatureSection(document, normalFont);
        
        // === SECTION PAIEMENT ===
        createPaymentSection(document, ordreMission, normalFont, headerFont);

        // === NOUVELLE PAGE POUR LE VERSO ===
        document.newPage();
        
        // === VERSO - OBSERVATIONS ET DÉCOMPTES ===
        createVersoPage(document, ordreMission, normalFont, smallFont, headerFont);
        
        
        document.close();
        
        // Préparer la réponse
        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ordre_mission_" + ordreMission.getReference() + "_" + utilisateur.getMatricule() + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(resource.contentLength())
            .body(resource);
            
    } catch (DocumentException e) {
        throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
    }
}

/**
 * Méthode pour créer l'en-tête officiel ART
 */
private void createOfficialHeader(Document document, Font titleFont, Font headerFont, Font normalFont, Font smallFont, Font mediumFont) throws DocumentException {
    // Tableau principal pour l'en-tête (3 colonnes)
    PdfPTable headerTable = new PdfPTable(3);
    headerTable.setWidthPercentage(100);
    headerTable.setWidths(new float[]{30, 40, 30});
    headerTable.setSpacingAfter(8);
    
    // === COLONNE GAUCHE (Français) ===
    PdfPCell leftCell = new PdfPCell();
    leftCell.setBorder(Rectangle.NO_BORDER);
    leftCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    
    Paragraph repFr = new Paragraph("RÉPUBLIQUE DU CAMEROUN", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD));
    repFr.setAlignment(Element.ALIGN_CENTER);
    leftCell.addElement(repFr);
    
    Paragraph deviseFr = new Paragraph("Paix - Travail - Patrie", new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL));
    deviseFr.setAlignment(Element.ALIGN_CENTER);
    leftCell.addElement(deviseFr);
    
    // Ligne de séparation
    Paragraph separator1 = new Paragraph("___________________", smallFont);
    separator1.setAlignment(Element.ALIGN_CENTER);
    leftCell.addElement(separator1);
    
    Paragraph artFr = new Paragraph("AGENCE DE RÉGULATION", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD));
    artFr.setAlignment(Element.ALIGN_CENTER);
    leftCell.addElement(artFr);
    
    Paragraph artFr2 = new Paragraph("DES TÉLÉCOMMUNICATIONS", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD));
    artFr2.setAlignment(Element.ALIGN_CENTER);
    leftCell.addElement(artFr2);
    
    Paragraph separator2 = new Paragraph("___________________", smallFont);
    separator2.setAlignment(Element.ALIGN_CENTER);
    leftCell.addElement(separator2);
    
    headerTable.addCell(leftCell);
    
    // === COLONNE CENTRE (Logo) ===
    PdfPCell centerCell = new PdfPCell();
    centerCell.setBorder(Rectangle.NO_BORDER);
    centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    centerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    
    // Ajout du logo ART
    try {
        ClassPathResource logoResource = new ClassPathResource("art_logo.png");
        if (logoResource.exists()) {
            Image logo = Image.getInstance(logoResource.getURL());
            logo.scaleToFit(120, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            centerCell.addElement(logo);
        } else {
            // Fallback - texte "ART" stylisé
            Paragraph logoText = new Paragraph("ART", new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLUE));
            logoText.setAlignment(Element.ALIGN_CENTER);
            centerCell.addElement(logoText);
        }
    } catch (Exception e) {
        // Si erreur lors du chargement du logo
        Paragraph logoText = new Paragraph("ART", new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLUE));
        logoText.setAlignment(Element.ALIGN_CENTER);
        centerCell.addElement(logoText);
    }
    
    headerTable.addCell(centerCell);
    
    // === COLONNE DROITE (Anglais) ===
    PdfPCell rightCell = new PdfPCell();
    rightCell.setBorder(Rectangle.NO_BORDER);
    rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    
    Paragraph repEn = new Paragraph("REPUBLIC OF CAMEROON", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD));
    repEn.setAlignment(Element.ALIGN_CENTER);
    rightCell.addElement(repEn);
    
    Paragraph deviseEn = new Paragraph("Peace - Work - Fatherland", new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL));
    deviseEn.setAlignment(Element.ALIGN_CENTER);
    rightCell.addElement(deviseEn);
    
    // Ligne de séparation
    Paragraph separator3 = new Paragraph("___________________", smallFont);
    separator3.setAlignment(Element.ALIGN_CENTER);
    rightCell.addElement(separator3);
    
    Paragraph artEn = new Paragraph("TELECOMMUNICATIONS", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD));
    artEn.setAlignment(Element.ALIGN_CENTER);
    rightCell.addElement(artEn);
    
    Paragraph artEn2 = new Paragraph("REGULATORY BOARD", new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD));
    artEn2.setAlignment(Element.ALIGN_CENTER);
    rightCell.addElement(artEn2);
    
    Paragraph separator4 = new Paragraph("___________________", smallFont);
    separator4.setAlignment(Element.ALIGN_CENTER);
    rightCell.addElement(separator4);
    
    headerTable.addCell(rightCell);
    
    document.add(headerTable);
}

/**
 * Créer la section principale avec les informations de l'agent
 */
private void createMainInfoSection(Document document, OrdreMission ordreMission, User utilisateur, Font normalFont, Font smallFont) throws DocumentException {
    // Cadre principal pour toutes les informations
    PdfPTable mainTable = new PdfPTable(1);
    mainTable.setWidthPercentage(100);
    mainTable.setSpacingBefore(10);
    mainTable.setSpacingAfter(12);
    
    PdfPCell mainCell = new PdfPCell();
    mainCell.setBorderWidth(2);
    mainCell.setBorderColor(BaseColor.BLACK);
    mainCell.setPadding(10);
    
    // Contenu du cadre
    StringBuilder content = new StringBuilder();
    
    // Informations de l'agent

// Bloc 1: Noms et Prénoms
content.append("Noms et Prénoms................: ").append(utilisateur.getName() != null ? utilisateur.getName() : "").append("\n");
content.append("(Name, First name)\n\n");

// Bloc 2: Grade
content.append("Grade..............................: ").append(utilisateur.getFonction() != null ? utilisateur.getFonction() : "").append("\n");
content.append("(Rank)\n\n");

// Bloc 3: Fonction / Service
content.append("Fonction / Service.........: ").append(utilisateur.getFonction() != null ? utilisateur.getFonction() : "").append("\n");
content.append("(Function / Office)\n\n");

// Bloc 4: Destination
String destinationString = "";
if (ordreMission.getMandat() != null && ordreMission.getMandat().getVilles() != null) {
    destinationString = ordreMission.getMandat().getVilles()
        .stream()
        .map(Ville::getName)
        .collect(Collectors.joining(" - "));
}
content.append("Destination...................: ").append(destinationString).append("\n");
content.append("(Destination)\n\n");

// Bloc 5: Motif/Objectif
content.append("Motif..............................: ").append(ordreMission.getObjectif() != null ? ordreMission.getObjectif() : "").append("\n");
content.append("(Purpose of journey)\n\n");

// Bloc 6: Moyen de transport
String ressourcesString = "";
if (ordreMission.getMandat() != null && ordreMission.getMandat().getRessources() != null) {
    ressourcesString = ordreMission.getMandat().getRessources()
        .stream()
        .map(Ressource::getName)
        .collect(Collectors.joining(", "));
}
content.append("Moyen de transport........: ").append(ressourcesString).append("\n");
content.append("(Means of transport)\n\n");

// Bloc 7: Durée
content.append("Durée............................: ");
if (ordreMission.getDuree() != null) {
    content.append(ordreMission.getDuree().toString()).append(" jours");
}
content.append("\n");
content.append("(Duration)\n\n");

Paragraph contentParagraph = new Paragraph(content.toString(), normalFont);
contentParagraph.setLeading(14f); // Augmenté pour plus d'espace entre les lignes
mainCell.addElement(contentParagraph);

mainTable.addCell(mainCell);
document.add(mainTable);
}

/**
 * Créer la section signature
 */
private void createSignatureSection(Document document, Font normalFont) throws DocumentException {
    // Signature du directeur général
    Paragraph signature = new Paragraph("SIGNATURE DU DIRECTEUR GÉNÉRAL", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD));
    signature.setAlignment(Element.ALIGN_RIGHT);
    signature.setSpacingBefore(10);
    signature.setSpacingAfter(15);
    document.add(signature);
}

/**
 * Créer la section paiement
 */
private void createPaymentSection(Document document, OrdreMission ordreMission, Font normalFont, Font headerFont) throws DocumentException {
    // Mode de paiement
    Paragraph modePaiement = new Paragraph("Mode de paiement ............: " + (ordreMission.getModePaiement() != null ? ordreMission.getModePaiement() : ""), normalFont);
    modePaiement.setSpacingAfter(8);
    document.add(modePaiement);
    
    // Devise
    Paragraph devise = new Paragraph("Devise ..................: " + (ordreMission.getDevise() != null ? ordreMission.getDevise() : ""), normalFont);
    devise.setSpacingAfter(8);
    document.add(devise);
    
    // Tableau des décomptes
    PdfPTable paymentTable = new PdfPTable(3);
    paymentTable.setWidthPercentage(100);
    paymentTable.setSpacingBefore(5);
    paymentTable.setSpacingAfter(10);
    
    // En-têtes
    PdfPCell headerCell1 = new PdfPCell(new Phrase("Nombre de jours", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
    headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
    headerCell1.setPadding(4);
    headerCell1.setBorderWidth(1);
    
    PdfPCell headerCell2 = new PdfPCell(new Phrase("Taux", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
    headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
    headerCell2.setPadding(4);
    headerCell2.setBorderWidth(1);
    
    PdfPCell headerCell3 = new PdfPCell(new Phrase("Décompte", new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD)));
    headerCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
    headerCell3.setPadding(4);
    headerCell3.setBorderWidth(1);
    
    paymentTable.addCell(headerCell1);
    paymentTable.addCell(headerCell2);
    paymentTable.addCell(headerCell3);
    
    // Ligne de données (vide pour remplissage manuel)
    PdfPCell dataCell1 = new PdfPCell(new Phrase(ordreMission.getDuree() != null ? ordreMission.getDuree().toString() : "", normalFont));
    dataCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
    dataCell1.setPadding(8);
    dataCell1.setBorderWidth(1);
    
    PdfPCell dataCell2 = new PdfPCell(new Phrase(ordreMission.getTauxAvance() != null ? ordreMission.getTauxAvance().toString() : "", normalFont));
    dataCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
    dataCell2.setPadding(8);
    dataCell2.setBorderWidth(1);
    
    PdfPCell dataCell3 = new PdfPCell(new Phrase(ordreMission.getDecompteTotal() != null ? ordreMission.getDecompteTotal().toString() : "", normalFont));
    dataCell3.setHorizontalAlignment(Element.ALIGN_CENTER);
    dataCell3.setPadding(8);
    dataCell3.setBorderWidth(1);
    
    paymentTable.addCell(dataCell1);
    paymentTable.addCell(dataCell2);
    paymentTable.addCell(dataCell3);
    
    document.add(paymentTable);
    
    // Phrase finale
    Paragraph finalPhrase = new Paragraph("Arrêté le présent décompte à la somme de ................................................................", normalFont);
    finalPhrase.setSpacingBefore(8);
    finalPhrase.setSpacingAfter(12);
    document.add(finalPhrase);
    
    // Date et lieu de signature
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String currentDate = dateFormat.format(new Date());
    Paragraph dateSignature = new Paragraph("A ................................, le " + currentDate , normalFont);
    dateSignature.setAlignment(Element.ALIGN_RIGHT);
    document.add(dateSignature);
}

/**
 * Créer la page verso avec observations et décomptes
 */
private void createVersoPage(Document document, OrdreMission ordreMission, Font normalFont, Font smallFont, Font headerFont) throws DocumentException {
    // === SECTION OBSERVATIONS ===
    createObservationsSection(document, normalFont, smallFont, headerFont);
    
    // === SECTION DÉCOMPTES DES AVANCES ===
    createDecomptesAvancesSection(document, ordreMission, normalFont, smallFont, headerFont);
    
    // // === SECTION NOTE DE FRAIS ===
    createNoteFraisSection(document, normalFont, smallFont, headerFont);
    
    // // === SECTION DÉCOMPTES FINAUX ===
    createDecomptesFinauxSection(document, normalFont, smallFont, headerFont);
}

/**
 * Créer la section observations
 */
private void createObservationsSection(Document document, Font normalFont, Font smallFont, Font headerFont) throws DocumentException {
    // Titre OBSERVATIONS
    Paragraph obsTitle = new Paragraph("OBSERVATIONS", headerFont);
    obsTitle.setAlignment(Element.ALIGN_CENTER);
    obsTitle.setSpacingBefore(10);
    obsTitle.setSpacingAfter(8);
    document.add(obsTitle);
    
    // Tableau des observations
    PdfPTable obsTable = new PdfPTable(4);
    obsTable.setWidthPercentage(100);
    obsTable.setWidths(new float[]{25, 25, 25, 25});
    obsTable.setSpacingAfter(10);
    
    // En-têtes
    PdfPCell header1 = new PdfPCell(new Phrase("Visa au départ", smallFont));
    header1.setHorizontalAlignment(Element.ALIGN_CENTER);
    header1.setPadding(4);
    header1.setBorderWidth(1);
    header1.setMinimumHeight(20);
    
    PdfPCell header2 = new PdfPCell(new Phrase("Visa à l'arrivée", smallFont));
    header2.setHorizontalAlignment(Element.ALIGN_CENTER);
    header2.setPadding(4);
    header2.setBorderWidth(1);
    header2.setMinimumHeight(20);
    
    PdfPCell header3 = new PdfPCell(new Phrase("Visa au départ", smallFont));
    header3.setHorizontalAlignment(Element.ALIGN_CENTER);
    header3.setPadding(4);
    header3.setBorderWidth(1);
    header3.setMinimumHeight(20);
    
    PdfPCell header4 = new PdfPCell(new Phrase("Visa à l'arrivée", smallFont));
    header4.setHorizontalAlignment(Element.ALIGN_CENTER);
    header4.setPadding(4);
    header4.setBorderWidth(1);
    header4.setMinimumHeight(20);
    
    obsTable.addCell(header1);
    obsTable.addCell(header2);
    obsTable.addCell(header3);
    obsTable.addCell(header4);
    
    // Cellules vides pour les visas
    for (int i = 0; i < 4; i++) {
        PdfPCell emptyCell = new PdfPCell(new Phrase("", normalFont));
        emptyCell.setBorderWidth(1);
        emptyCell.setMinimumHeight(60);
        obsTable.addCell(emptyCell);
    }
    
    document.add(obsTable);
    
    // Section durée et raison
    PdfPTable reasonTable = new PdfPTable(2);
    reasonTable.setWidthPercentage(100);
    reasonTable.setSpacingAfter(15);
    
    // Durée et raison du prolongement
    PdfPCell reasonCell1 = new PdfPCell(new Phrase("Durée et raison du prolongement..........................", smallFont));
    reasonCell1.setBorder(Rectangle.NO_BORDER);
    reasonCell1.setColspan(1);
    reasonCell1.setPadding(4);
    
    PdfPCell reasonCell2 = new PdfPCell(new Phrase("Visa du Directeur Général................................", smallFont));
    reasonCell2.setBorder(Rectangle.NO_BORDER);
    reasonCell2.setColspan(1);
    reasonCell2.setPadding(4);
    
    reasonTable.addCell(reasonCell1);
    reasonTable.addCell(reasonCell2);
    
    // Ligne de points
    PdfPCell dots1 = new PdfPCell(new Phrase(".............................................................................", smallFont));
    dots1.setBorder(Rectangle.NO_BORDER);
    dots1.setPadding(4);
    
    PdfPCell dots2 = new PdfPCell(new Phrase(".............................................................................", smallFont));
    dots2.setBorder(Rectangle.NO_BORDER);
    dots2.setPadding(4);
    
    reasonTable.addCell(dots1);
    reasonTable.addCell(dots2);
    
    // Durée et raison de fin prématurée
    PdfPCell prematureCell1 = new PdfPCell(new Phrase("Durée et raison de fin prématurée de", smallFont));
    prematureCell1.setBorder(Rectangle.NO_BORDER);
    prematureCell1.setPadding(4);
    
    PdfPCell prematureCell2 = new PdfPCell(new Phrase("", smallFont));
    prematureCell2.setBorder(Rectangle.NO_BORDER);
    prematureCell2.setPadding(4);
    
    reasonTable.addCell(prematureCell1);
    reasonTable.addCell(prematureCell2);
    
    PdfPCell missionCell = new PdfPCell(new Phrase("la mission................................", smallFont));
    missionCell.setBorder(Rectangle.NO_BORDER);
    missionCell.setPadding(4);
    
    PdfPCell emptyCell = new PdfPCell(new Phrase("", smallFont));
    emptyCell.setBorder(Rectangle.NO_BORDER);
    emptyCell.setPadding(4);
    
    reasonTable.addCell(missionCell);
    reasonTable.addCell(emptyCell);
    
    // Ligne de points finale
    PdfPCell finalDots1 = new PdfPCell(new Phrase(".............................................................................", smallFont));
    finalDots1.setBorder(Rectangle.NO_BORDER);
    finalDots1.setPadding(4);
    
    PdfPCell finalDots2 = new PdfPCell(new Phrase(".............................................................................", smallFont));
    finalDots2.setBorder(Rectangle.NO_BORDER);
    finalDots2.setPadding(4);
    
    reasonTable.addCell(finalDots1);
    reasonTable.addCell(finalDots2);
    
    document.add(reasonTable);
}

/**
 * Créer la section décomptes des avances
 */
private void createDecomptesAvancesSection(Document document, OrdreMission ordreMission, Font normalFont, Font smallFont, Font headerFont) throws DocumentException {
    // Titre
    Paragraph title = new Paragraph("DÉCOMPTES DES AVANCES - DETAILS OF ADVANCES", headerFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingBefore(5);
    title.setSpacingAfter(8);
    document.add(title);
    
    // Tableau principal
    PdfPTable mainTable = new PdfPTable(7);
    mainTable.setWidthPercentage(100);
    mainTable.setWidths(new float[]{20, 10, 10, 10, 10, 10, 30});
    mainTable.setSpacingAfter(10);
    
    // En-têtes
    String[] headers = {
        "AU DÉPART - AT DEPARTURE",
        "NOMBRE Number",
        "TAUX Rate",
        "DÉCOMPTE Sub-total",
        "NOMBRE Number",
        "TAUX Rate",
        "Indication des réquisitions"
    };
    
    for (String header : headers) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setPadding(3);
        headerCell.setBorderWidth(1);
        headerCell.setMinimumHeight(25);
        mainTable.addCell(headerCell);
    }
    
    // Lignes de données
    String[] rowLabels = {
        "INDEMNITÉ JOURNALIÈRE Daily allowance",
        "Normale - Normal",
        "Réduite - Reduced",
        "Partielle - Partial"
    };
    
    for (String label : rowLabels) {
        // Première colonne - label
        PdfPCell labelCell = new PdfPCell(new Phrase(label, new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
        labelCell.setPadding(3);
        labelCell.setBorderWidth(1);
        labelCell.setMinimumHeight(20);
        mainTable.addCell(labelCell);
        
        // Colonnes de données (vides pour remplissage)
        for (int i = 0; i < 6; i++) {
            PdfPCell dataCell = new PdfPCell(new Phrase("", normalFont));
            dataCell.setBorderWidth(1);
            dataCell.setMinimumHeight(20);
            mainTable.addCell(dataCell);
        }
    }
    
    // Ligne TOTAL
    PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL", headerFont));
    totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    totalCell.setPadding(3);
    totalCell.setBorderWidth(1);
    totalCell.setMinimumHeight(25);
    mainTable.addCell(totalCell);
    
    for (int i = 0; i < 4; i++) {
        PdfPCell emptyCell = new PdfPCell(new Phrase("", normalFont));
        emptyCell.setBorderWidth(1);
        emptyCell.setMinimumHeight(25);
        mainTable.addCell(emptyCell);
    }
    
    PdfPCell totalRightCell = new PdfPCell(new Phrase("TOTAL", headerFont));
    totalRightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    totalRightCell.setPadding(3);
    totalRightCell.setBorderWidth(1);
    totalRightCell.setMinimumHeight(25);
    mainTable.addCell(totalRightCell);
    
    PdfPCell finalEmptyCell = new PdfPCell(new Phrase("", normalFont));
    finalEmptyCell.setBorderWidth(1);
    finalEmptyCell.setMinimumHeight(25);
    mainTable.addCell(finalEmptyCell);
    
    document.add(mainTable);
    
    // Section des montants
    PdfPTable amountTable = new PdfPTable(2);
    amountTable.setWidthPercentage(100);
    amountTable.setSpacingAfter(10);
    
    // Colonne gauche
    PdfPCell leftAmount = new PdfPCell();
    leftAmount.setBorder(Rectangle.NO_BORDER);
    leftAmount.addElement(new Paragraph("ARRÊTÉ À LA SOMME DE", smallFont));
    leftAmount.addElement(new Paragraph("CLOSE AT THE SUM OF", smallFont));
    leftAmount.addElement(new Paragraph("", normalFont));
    leftAmount.addElement(new Paragraph("Payé en advance", smallFont));
    leftAmount.addElement(new Paragraph("Paid as advance", smallFont));
    leftAmount.addElement(new Paragraph("", normalFont));
    leftAmount.addElement(new Paragraph("A (AL) ________________, le (on the) ________________", smallFont));
    leftAmount.addElement(new Paragraph("", normalFont));
    leftAmount.addElement(new Paragraph("La (The) ________________", smallFont));
    leftAmount.addElement(new Paragraph("                    (Signature)", smallFont));
    
    // Colonne droite
    PdfPCell rightAmount = new PdfPCell();
    rightAmount.setBorder(Rectangle.NO_BORDER);
    rightAmount.addElement(new Paragraph("PAYÉ LA SOMME DE", smallFont));
    rightAmount.addElement(new Paragraph("PAID THE SUM OF", smallFont));
    rightAmount.addElement(new Paragraph("", normalFont));
    rightAmount.addElement(new Paragraph("A titre de", smallFont));
    rightAmount.addElement(new Paragraph("As title of", smallFont));
    rightAmount.addElement(new Paragraph("", normalFont));
    rightAmount.addElement(new Paragraph("A (AL) ________________, le (on the) ________________", smallFont));
    rightAmount.addElement(new Paragraph("", normalFont));
    rightAmount.addElement(new Paragraph("La (The) ________________", smallFont));
    rightAmount.addElement(new Paragraph("                    (Signature)", smallFont));
    
    amountTable.addCell(leftAmount);
    amountTable.addCell(rightAmount);
    
    document.add(amountTable);
}

/**
 * Créer la section note de frais
 */
private void createNoteFraisSection(Document document, Font normalFont, Font smallFont, Font headerFont) throws DocumentException {
    // Titre
    Paragraph title = new Paragraph("NOTE DE FRAIS", headerFont);
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingBefore(15);
    title.setSpacingAfter(8);
    document.add(title);
    
    // Ligne d'imputation
    Paragraph imputation = new Paragraph("Imputation................................................................................................", smallFont);
    imputation.setSpacingAfter(5);
    document.add(imputation);
    
    Paragraph montant = new Paragraph("Montant total des frais................................................................................................", smallFont);
    montant.setSpacingAfter(10);
    document.add(montant);
}

/**
 * Créer la section décomptes finaux
 */
private void createDecomptesFinauxSection(Document document, Font normalFont, Font smallFont, Font headerFont) throws DocumentException {
    // Tableau des décomptes finaux
    PdfPTable finalTable = new PdfPTable(2);
    finalTable.setWidthPercentage(100);
    finalTable.setSpacingAfter(15);
    
    // Colonne gauche - DÉCOMPTES DES AVANCES
    PdfPCell leftFinalCell = new PdfPCell();
    leftFinalCell.setBorderWidth(1);
    leftFinalCell.setPadding(3);
    
    leftFinalCell.addElement(new Paragraph("DÉCOMPTES DES AVANCES", new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
    leftFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("Montant en chiffres", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("En lettres................................................................................................", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("................................................................................................", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("Acquit du bénéficiaire", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("Reçu", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("CNI N°................................................................................................", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("Délivrée le........................A................................", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    leftFinalCell.addElement(new Paragraph("SIGNATURE", new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
    
    // Colonne droite - DÉCOMPTE DU RESTE
    PdfPCell rightFinalCell = new PdfPCell();
    rightFinalCell.setBorderWidth(1);
    rightFinalCell.setPadding(3);
    
    rightFinalCell.addElement(new Paragraph("DÉCOMPTE DU RESTE", new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
    rightFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("Montant en chiffres", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("En lettres................................................................................................", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("................................................................................................", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("Acquit du bénéficiaire", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("Reçu", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("CNI N°................................................................................................", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("Délivrée le........................A................................", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("", new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL)));
    rightFinalCell.addElement(new Paragraph("SIGNATURE", new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD)));
    
    finalTable.addCell(leftFinalCell);
    finalTable.addCell(rightFinalCell);
    
    document.add(finalTable);
}
    @Transactional
    public OrdreMissionResponseDto confirmerOrdreMission(Long ordreMissionId, Authentication authentication) {
        // Extraire l'utilisateur depuis le token JWT
        String userEmail = authentication.getName();
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        OrdreMission ordreMission = ordreMissionRepo.findById(ordreMissionId)
            .orElseThrow(() -> new EntityNotFoundException("Ordre de mission non trouvé"));

        if (ordreMission.getStatut() != OrdreMissionStatut.EN_ATTENTE_CONFIRMATION) {
            throw new IllegalArgumentException("Cet ordre de mission ne peut pas être confirmé dans son état actuel");
        }

        // Confirmer l'ordre de mission
        ordreMission.setStatut(OrdreMissionStatut.EN_ATTENTE_EXECUTION);
        ordreMission.setConfirmeParUserId(user.getId()); // Utiliser l'ID de l'utilisateur authentifié
        ordreMission.setDateConfirmation(LocalDateTime.now());
        
        OrdreMission confirmedOrdreMission = ordreMissionRepo.save(ordreMission);

        // Notifier l'utilisateur concerné
        emailService.sendEmail(
            ordreMission.getUser().getEmail(),
            "Ordre de mission confirmé",
            "Votre ordre de mission " + ordreMission.getReference() + " a été confirmé avec succès, par " + user.getName() + "."
        );

        return mapToResponseDto(confirmedOrdreMission);
    }

    @Transactional
    public OrdreMissionResponseDto rejetterOrdreMission(Long ordreMissionId, Authentication authentication) {
        // Extraire l'utilisateur depuis le token JWT
        String userEmail = authentication.getName();
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        OrdreMission ordreMission = ordreMissionRepo.findById(ordreMissionId)
            .orElseThrow(() -> new EntityNotFoundException("Ordre de mission non trouvé"));

        if (ordreMission.getStatut() != OrdreMissionStatut.EN_ATTENTE_CONFIRMATION) {
            throw new IllegalArgumentException("Cet ordre de mission ne peut pas être confirmé dans son état actuel");
        }

        // Rejeter l'ordre de mission
        ordreMission.setStatut(OrdreMissionStatut.REJETE);
        ordreMission.setRejeteParUserId(user.getId()); // Utiliser l'ID de l'utilisateur authentifié
        ordreMission.setDateRejet(LocalDateTime.now());
        
        OrdreMission rejectedOrdreMission = ordreMissionRepo.save(ordreMission);

        // Notifier l'utilisateur concerné
        emailService.sendEmail(
            ordreMission.getUser().getEmail(),
            "Ordre de mission rejeté",
            "Votre ordre de mission " + ordreMission.getReference() + " a été rejeté" + " par " + user.getName() + "."
        );
        return mapToResponseDto(rejectedOrdreMission);
    }

    public List<OrdreMissionResponseDto> getOrdresMissionEnAttenteConfirmation() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatut(OrdreMissionStatut.EN_ATTENTE_CONFIRMATION);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<OrdreMissionResponseDto> getOrdresMissionRejete() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatut(OrdreMissionStatut.REJETE);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<OrdreMissionResponseDto> getOrdresMissionVisiblesPourAgent() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatutIn(
            List.of(OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF, OrdreMissionStatut.EN_ATTENTE_CONFIRMATION)
        );
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<OrdreMissionResponseDto> getOrdresMissionPourUtilisateur(Long userId) {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByUserIdAndStatutNot(
            userId, OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF);
        return ordresMission.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void mettreAJourStatutsAutomatiquement() {
        Date maintenant = new Date();
        
        // Mettre à jour les ordres EN_ATTENTE_EXECUTION vers EN_COURS
        List<OrdreMission> ordresADemarrer = ordreMissionRepo.findByStatutAndDateDebutLessThanEqual(
            OrdreMissionStatut.EN_ATTENTE_EXECUTION, maintenant);
        
        for (OrdreMission ordre : ordresADemarrer) {
            ordre.setStatut(OrdreMissionStatut.EN_COURS);
            ordreMissionRepo.save(ordre);

            // Notifier l'utilisateur concerné
            emailService.sendEmail(
                ordre.getUser().getEmail(),
                "Ordre de mission en cours",
                "Votre ordre de mission " + ordre.getReference() + " a été mis en cours d'exécution."
            );
        }
        
        // Mettre à jour les ordres EN_COURS vers ACHEVE
        List<OrdreMission> ordresATerminer = ordreMissionRepo.findByStatutAndDateFinLessThanEqual(
            OrdreMissionStatut.EN_COURS, maintenant);
        
        for (OrdreMission ordre : ordresATerminer) {
            ordre.setStatut(OrdreMissionStatut.ACHEVE);
            ordreMissionRepo.save(ordre);

            // Notifier l'utilisateur concerné
            emailService.sendEmail(
                ordre.getUser().getEmail(),
                "Ordre de mission achevé",
                "Votre ordre de mission " + ordre.getReference() + " a été marqué comme achevé."
            );
        }
    }

    // Autres méthodes existantes...
    public List<OrdreMissionResponseDto> getAllOrdresMission() {
        return ordreMissionRepo.findAllActive()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public OrdreMissionResponseDto getOrdreMissionById(Long id) {
        OrdreMission ordreMission = ordreMissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordre de mission non trouvé avec l'ID: " + id));
        return mapToResponseDto(ordreMission);
    }

    private OrdreMissionResponseDto mapToResponseDto(OrdreMission ordreMission) {
        // Convertir les pièces jointes
        List<PieceJointeResponseDto> pieceJointeDtos = ordreMission.getPiecesJointes() != null ? 
            ordreMission.getPiecesJointes().stream()
                .map(this::convertirEnResponseDto)
                .collect(Collectors.toList()) : 
            List.of();
        // Convertir le user
        UserResponseDto userDto = ordreMission.getUser() != null ? 
        new UserResponseDto(
            ordreMission.getUser().getId(), 
            ordreMission.getUser().getName(), 
            ordreMission.getUser().getEmail(),
            ordreMission.getUser().getMatricule(),
            ordreMission.getUser().getQuotaAnnuel(),
            new RoleResponseDto(
                ordreMission.getUser().getRole().getId(),
                ordreMission.getUser().getRole().getName(),
                ordreMission.getUser().getRole().getDescription(),
                ordreMission.getUser().getRole().getCreated_at(),
                ordreMission.getUser().getRole().getUpdated_at()
            ),
            new RangResponseDto(
                ordreMission.getUser().getRang().getId(),
                ordreMission.getUser().getRang().getNom(),
                ordreMission.getUser().getRang().getCode(),
                ordreMission.getUser().getRang().getFraisExterne(),
                ordreMission.getUser().getRang().getFraisInterne(),
                ordreMission.getUser().getRang().getCreated_at(),
                ordreMission.getUser().getRang().getUpdated_at()
            ),
            ordreMission.getUser().getFonction(),
            ordreMission.getUser().getCreated_at(),
            ordreMission.getUser().getUpdated_at()
        ) : 
        null;

        return new OrdreMissionResponseDto(
                ordreMission.getId(),
                ordreMission.getReference(),
                ordreMission.getObjectif(),
                ordreMission.getModePaiement(),
                ordreMission.getDevise(),
                ordreMission.getDateDebut(),
                ordreMission.getDateFin(),
                ordreMission.getDuree(),
                ordreMission.getTauxAvance(),
                ordreMission.getDecompteTotal(),
                ordreMission.getDecompteAvance(),
                ordreMission.getDecompteRelicat(),
                ordreMission.getStatut(),
                ordreMission.getCreated_at(),
                ordreMission.getUpdated_at(),
                ordreMission.getDateConfirmation(),
                ordreMission.getDateRejet(),
                pieceJointeDtos,
                userDto

        );
    }

    // Méthode utilitaire pour convertir en DTO de réponse
    private PieceJointeResponseDto convertirEnResponseDto(PieceJointe pieceJointe) {
        PieceJointeResponseDto dto = new PieceJointeResponseDto();
        dto.setId(pieceJointe.getId());
        dto.setNom(pieceJointe.getNom());
        dto.setNomOriginal(pieceJointe.getNomOriginal());
        dto.setCheminFichier(pieceJointe.getCheminFichier());
        dto.setTypeMime(pieceJointe.getTypeMime());
        dto.setTaille(pieceJointe.getTaille());
        dto.setDescription(pieceJointe.getDescription());
        dto.setCreated_at(pieceJointe.getCreated_at());
        dto.setUpdated_at(pieceJointe.getUpdated_at());
        dto.setActif(pieceJointe.isActif());

        // Relations
        if (pieceJointe.getUser() != null) {
            dto.setUserId(pieceJointe.getUser().getId());
            // Se rassurer que la classe User a une méthode getName()
            dto.setUserName(pieceJointe.getUser().getName());
        }

        if (pieceJointe.getMandat() != null) {
            dto.setMandatId(pieceJointe.getMandat().getId());
            dto.setMandatReference(pieceJointe.getMandat().getReference());
        }

        if (pieceJointe.getOrdreMission() != null) {
            dto.setOrdreMissionId(pieceJointe.getOrdreMission().getId());
            dto.setOrdreMissionReference(pieceJointe.getOrdreMission().getReference());
        }

        if (pieceJointe.getRapport() != null) {
            dto.setRapportId(pieceJointe.getRapport().getId());
            dto.setRapportReference(pieceJointe.getRapport().getReference());
        }

        return dto;
    }
}