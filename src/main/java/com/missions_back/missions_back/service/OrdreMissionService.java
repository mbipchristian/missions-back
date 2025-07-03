package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.springframework.core.io.ClassPathResource;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.missions_back.missions_back.dto.OrdreMissionResponseDto;
import com.missions_back.missions_back.dto.OrdreMissionUpdateDto;
import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.OrdreMission;
import com.missions_back.missions_back.model.OrdreMissionStatut;
import com.missions_back.missions_back.model.Ressource;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.model.Ville;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.OrdreMissionRepo;
import com.missions_back.missions_back.repository.UserRepo;

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

@Service
public class OrdreMissionService {
    private final OrdreMissionRepo ordreMissionRepo;
    private final UserRepo userRepo;
    private final MandatRepo mandatRepo;

    public OrdreMissionService(OrdreMissionRepo ordreMissionRepo, UserRepo userRepo, MandatRepo mandatRepo) {
        this.ordreMissionRepo = ordreMissionRepo;
        this.userRepo = userRepo;
        this.mandatRepo = mandatRepo;
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

    @Transactional
    public OrdreMissionResponseDto updateOrdreMission(Long ordreMissionId, OrdreMissionUpdateDto updateDto, Authentication authentication) {
        // Extraire l'utilisateur depuis le token JWT
        String userEmail = authentication.getName();
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        
        // Récupérer l'ordre de mission existant
        OrdreMission ordreMission = ordreMissionRepo.findById(ordreMissionId)
            .orElseThrow(() -> new EntityNotFoundException("Ordre de mission non trouvé avec l'ID: " + ordreMissionId));

        // Mise à jour des champs si ils sont fournis dans le DTO
        if (updateDto.getObjectif() != null) {
            ordreMission.setObjectif(updateDto.getObjectif());
        }
        
        if (updateDto.getModePaiement() != null) {
            ordreMission.setModePaiement(updateDto.getModePaiement());
        }
        
        if (updateDto.getDevise() != null) {
            ordreMission.setDevise(updateDto.getDevise());
        }
        
        if (updateDto.getDateDebut() != null) {
            ordreMission.setDateDebut(updateDto.getDateDebut());
        }
        
        if (updateDto.getDateFin() != null) {
            ordreMission.setDateFin(updateDto.getDateFin());
        }
        
        if (updateDto.getDuree() != null) {
            ordreMission.setDuree(updateDto.getDuree());
        }
        
        if (updateDto.getTauxAvance() != null) {
            ordreMission.setTauxAvance(updateDto.getTauxAvance());
        }
        
        if (updateDto.getDecompteTotal() != null) {
            ordreMission.setDecompteTotal(updateDto.getDecompteTotal());
        }
        
        if (updateDto.getDecompteAvance() != null) {
            ordreMission.setDecompteAvance(updateDto.getDecompteAvance());
        }
        
        if (updateDto.getDecompteRelicat() != null) {
            ordreMission.setDecompteRelicat(updateDto.getDecompteRelicat());
        }
        
        if (updateDto.getStatut() != null) {
            ordreMission.setStatut(updateDto.getStatut());
        }

        // Mettre à jour la date de modification
        ordreMission.setUpdated_at(LocalDateTime.now());
        
        // Sauvegarder les modifications
        OrdreMission updatedOrdreMission = ordreMissionRepo.save(ordreMission);
        
        return mapToResponseDto(updatedOrdreMission);
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

        Mandat mandat = ordreMission.getMandat();
        
        PdfPCell refCell = new PdfPCell(new Phrase("N°" + mandat.getReference(), normalFont));
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
    @Transactional
    public OrdreMissionResponseDto confirmerOrdreMission(Long ordreMissionId, Authentication authentication) {
        // Extraire l'utilisateur depuis le token JWT
        String userEmail = authentication.getName();
        User user = userRepo.findByEmail(userEmail)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        
        // if (!user.getRole().equals(RoleEnum.DIRECTEUR_RESSOURCES_HUMAINES) && 
        //     !user.getRole().equals(RoleEnum.ADMIN)) {
        //     throw new IllegalArgumentException("Seul le directeur des ressources humaines peut confirmer un ordre de mission");
        // }

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
        return mapToResponseDto(confirmedOrdreMission);
    }

    public List<OrdreMissionResponseDto> getOrdresMissionEnAttenteConfirmation() {
        List<OrdreMission> ordresMission = ordreMissionRepo.findByStatut(OrdreMissionStatut.EN_ATTENTE_CONFIRMATION);
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
        }
        
        // Mettre à jour les ordres EN_COURS vers ACHEVE
        List<OrdreMission> ordresATerminer = ordreMissionRepo.findByStatutAndDateFinLessThanEqual(
            OrdreMissionStatut.EN_COURS, maintenant);
        
        for (OrdreMission ordre : ordresATerminer) {
            ordre.setStatut(OrdreMissionStatut.ACHEVE);
            ordreMissionRepo.save(ordre);
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
                ordreMission.getDateConfirmation()

        );
    }
}
