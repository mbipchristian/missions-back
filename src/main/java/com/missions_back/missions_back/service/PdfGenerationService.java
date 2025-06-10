package com.missions_back.missions_back.service;

import com.missions_back.missions_back.dto.MandatResponseDto;
import com.missions_back.missions_back.dto.EtapeResponseDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class PdfGenerationService {

    @Autowired
    private EtapeService etapeService;

    public byte[] generateMandatPdf(MandatResponseDto mandat) throws Exception {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Titre principal
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("MANDAT DE MISSION", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Sous-titre avec référence
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
        Paragraph subtitle = new Paragraph("Référence: " + mandat.reference(), subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(25);
        document.add(subtitle);

        // Fonts
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

        // === INFORMATIONS GÉNÉRALES ===
        addSectionHeader(document, "INFORMATIONS GÉNÉRALES", headerFont);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);
        infoTable.setWidths(new float[]{30f, 70f});

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        addTableRow(infoTable, "Référence:", mandat.reference(), headerFont, normalFont);
        addTableRow(infoTable, "Objectif:", mandat.objectif() != null ? mandat.objectif() : "Non spécifié", headerFont, normalFont);
        addTableRow(infoTable, "Date de début:", dateFormat.format(mandat.dateDebut()), headerFont, normalFont);
        addTableRow(infoTable, "Date de fin:", dateFormat.format(mandat.dateFin()), headerFont, normalFont);
        addTableRow(infoTable, "Durée totale:", mandat.duree() + " jour(s)", headerFont, normalFont);
        addTableRow(infoTable, "Type de mission:", mandat.missionDeControle() ? "Mission de contrôle" : "Mission standard", headerFont, normalFont);

        document.add(infoTable);

        // === ÉTAPES DU MANDAT ===
        try {
            List<EtapeResponseDto> etapes = etapeService.getAllEtapesByMandatId(mandat.id());
            
            if (etapes != null && !etapes.isEmpty()) {
                addSectionHeader(document, "ÉTAPES DU MANDAT (" + etapes.size() + ")", headerFont);

                // Trier les étapes par ordre
                etapes.sort((e1, e2) -> Integer.compare(e1.ordre(), e2.ordre()));

                for (EtapeResponseDto etape : etapes) {
                    addEtapeSection(document, etape, headerFont, normalFont, smallFont, dateFormat);
                }
            } else {
                addSectionHeader(document, "ÉTAPES DU MANDAT", headerFont);
                Paragraph noEtapes = new Paragraph("Aucune étape définie pour ce mandat.", normalFont);
                noEtapes.setSpacingAfter(20);
                document.add(noEtapes);
            }
        } catch (Exception e) {
            // En cas d'erreur lors de la récupération des étapes
            addSectionHeader(document, "ÉTAPES DU MANDAT", headerFont);
            Paragraph errorEtapes = new Paragraph("Erreur lors de la récupération des étapes.", normalFont);
            errorEtapes.setSpacingAfter(20);
            document.add(errorEtapes);
        }

        // === UTILISATEURS ASSIGNÉS ===
        if (mandat.users() != null && !mandat.users().isEmpty()) {
            addSectionHeader(document, "UTILISATEURS ASSIGNÉS (" + mandat.users().size() + ")", headerFont);

            PdfPTable usersTable = new PdfPTable(3);
            usersTable.setWidthPercentage(100);
            usersTable.setSpacingAfter(20);
            usersTable.setWidths(new float[]{40f, 40f, 20f});

            // Headers
            addTableHeader(usersTable, "Nom d'utilisateur", headerFont);
            addTableHeader(usersTable, "Email", headerFont);
            addTableHeader(usersTable, "Matricule", headerFont);

            // Data
            mandat.users().forEach(user -> {
                addTableCell(usersTable, user.username(), normalFont);
                addTableCell(usersTable, user.email(), normalFont);
                addTableCell(usersTable, user.matricule(), normalFont);
            });

            document.add(usersTable);
        }

        // === VILLES DE MISSION ===
        if (mandat.villes() != null && !mandat.villes().isEmpty()) {
            addSectionHeader(document, "VILLES DE MISSION (" + mandat.villes().size() + ")", headerFont);

            PdfPTable villesTable = new PdfPTable(2);
            villesTable.setWidthPercentage(100);
            villesTable.setSpacingAfter(20);
            villesTable.setWidths(new float[]{70f, 30f});

            // Headers
            addTableHeader(villesTable, "Nom de la ville", headerFont);
            addTableHeader(villesTable, "Code", headerFont);

            // Data
            mandat.villes().forEach(ville -> {
                addTableCell(villesTable, ville.name(), normalFont);
                addTableCell(villesTable, ville.code(), normalFont);
            });

            document.add(villesTable);
        }

        // === RESSOURCES ALLOUÉES ===
        if (mandat.ressources() != null && !mandat.ressources().isEmpty()) {
            addSectionHeader(document, "RESSOURCES ALLOUÉES (" + mandat.ressources().size() + ")", headerFont);

            PdfPTable ressourcesTable = new PdfPTable(4);
            ressourcesTable.setWidthPercentage(100);
            ressourcesTable.setSpacingAfter(20);
            ressourcesTable.setWidths(new float[]{40f, 20f, 15f, 25f});

            // Headers
            addTableHeader(ressourcesTable, "Nom", headerFont);
            

            // Data
            mandat.ressources().forEach(ressource -> {
                addTableCell(ressourcesTable, ressource.name(), normalFont);
            });

            document.add(ressourcesTable);
        }

        // === FOOTER ===
        document.add(new Paragraph(" ")); // Espace
        Paragraph footer = new Paragraph("Document généré le " + dateFormat.format(new java.util.Date()), smallFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    private void addSectionHeader(Document document, String title, Font font) throws DocumentException {
        Paragraph header = new Paragraph(title, font);
        header.setSpacingBefore(15);
        header.setSpacingAfter(10);
        header.setIndentationLeft(0);
        document.add(header);
        
        // Ligne de séparation
        Paragraph line = new Paragraph("_".repeat(80), FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY));
        line.setSpacingAfter(10);
        document.add(line);
    }

    private void addEtapeSection(Document document, EtapeResponseDto etape, Font headerFont, Font normalFont, Font smallFont, SimpleDateFormat dateFormat) throws DocumentException {
        // Titre de l'étape
        Font etapeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.DARK_GRAY);
        Paragraph etapeTitle = new Paragraph("Étape " + etape.ordre() + ": " + etape.nom(), etapeFont);
        etapeTitle.setSpacingBefore(10);
        etapeTitle.setSpacingAfter(8);
        etapeTitle.setIndentationLeft(10);
        document.add(etapeTitle);

        // Informations de l'étape
        PdfPTable etapeTable = new PdfPTable(2);
        etapeTable.setWidthPercentage(95);
        etapeTable.setSpacingAfter(10);
        etapeTable.setWidths(new float[]{25f, 75f});

        addTableRow(etapeTable, "Date de début:", dateFormat.format(etape.dateDebut()), smallFont, smallFont);
        addTableRow(etapeTable, "Date de fin:", dateFormat.format(etape.dateFin()), smallFont, smallFont);
        addTableRow(etapeTable, "Durée:", etape.duree() + " jour(s)", smallFont, smallFont);

        // Assignations
        if (etape.users() != null && !etape.users().isEmpty()) {
            String usersStr = etape.users().stream()
                .map(user -> user.username() + " (" + user.matricule() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("Aucun");
            addTableRow(etapeTable, "Utilisateurs:", usersStr, smallFont, smallFont);
        }

        if (etape.villes() != null && !etape.villes().isEmpty()) {
            String villesStr = etape.villes().stream()
                .map(ville -> ville.name() + " (" + ville.code() + ")")
                .reduce((a, b) -> a + ", " + b)
                .orElse("Aucune");
            addTableRow(etapeTable, "Villes:", villesStr, smallFont, smallFont);
        }

        if (etape.ressources() != null && !etape.ressources().isEmpty()) {
            String ressourcesStr = etape.ressources().stream()
                .map(ressource -> ressource.name() )
                .reduce((a, b) -> a + ", " + b)
                .orElse("Aucune");
            addTableRow(etapeTable, "Ressources:", ressourcesStr, smallFont, smallFont);
        }

        document.add(etapeTable);
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(3);
        labelCell.setPaddingTop(3);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(3);
        valueCell.setPaddingTop(3);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", font));
        cell.setPadding(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}
