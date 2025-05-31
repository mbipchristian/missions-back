package com.missions_back.missions_back.service;

import com.missions_back.missions_back.dto.MandatResponseDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

@Service
public class PdfGenerationService {

    public byte[] generateMandatPdf(MandatResponseDto mandat) throws Exception {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("MANDAT DE MISSION", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Informations générales
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

        // Table des informations générales
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(15);

        addTableRow(infoTable, "Référence:", mandat.reference(), headerFont, normalFont);
        addTableRow(infoTable, "Objectif:", mandat.objectif() != null ? mandat.objectif() : "Non spécifié", headerFont, normalFont);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        addTableRow(infoTable, "Date de début:", dateFormat.format(mandat.dateDebut()), headerFont, normalFont);
        addTableRow(infoTable, "Date de fin:", dateFormat.format(mandat.dateFin()), headerFont, normalFont);
        addTableRow(infoTable, "Durée:", mandat.duree() + " jour(s)", headerFont, normalFont);
        addTableRow(infoTable, "Type de mission:", mandat.missionDeControle() ? "Mission de contrôle" : "Mission standard", headerFont, normalFont);

        document.add(infoTable);

        // Utilisateurs assignés
        if (mandat.users() != null && !mandat.users().isEmpty()) {
            Paragraph usersHeader = new Paragraph("UTILISATEURS ASSIGNÉS", headerFont);
            usersHeader.setSpacingBefore(10);
            usersHeader.setSpacingAfter(10);
            document.add(usersHeader);

            PdfPTable usersTable = new PdfPTable(3);
            usersTable.setWidthPercentage(100);
            usersTable.setSpacingAfter(15);

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

        // Villes de mission
        if (mandat.villes() != null && !mandat.villes().isEmpty()) {
            Paragraph villesHeader = new Paragraph("VILLES DE MISSION", headerFont);
            villesHeader.setSpacingBefore(10);
            villesHeader.setSpacingAfter(10);
            document.add(villesHeader);

            PdfPTable villesTable = new PdfPTable(2);
            villesTable.setWidthPercentage(100);
            villesTable.setSpacingAfter(15);

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

        // Ressources allouées
        if (mandat.ressources() != null && !mandat.ressources().isEmpty()) {
            Paragraph ressourcesHeader = new Paragraph("RESSOURCES ALLOUÉES", headerFont);
            ressourcesHeader.setSpacingBefore(10);
            ressourcesHeader.setSpacingAfter(10);
            document.add(ressourcesHeader);

            PdfPTable ressourcesTable = new PdfPTable(3);
            ressourcesTable.setWidthPercentage(100);
            ressourcesTable.setSpacingAfter(15);

            // Headers
            addTableHeader(ressourcesTable, "Nom", headerFont);
            addTableHeader(ressourcesTable, "Code", headerFont);
            addTableHeader(ressourcesTable, "Quantité", headerFont);

            // Data
            mandat.ressources().forEach(ressource -> {
                addTableCell(ressourcesTable, ressource.name(), normalFont);
                
            });

            document.add(ressourcesTable);
        }

        // Footer
        Paragraph footer = new Paragraph("Document généré le " + dateFormat.format(new java.util.Date()), normalFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingBottom(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingBottom(5);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}