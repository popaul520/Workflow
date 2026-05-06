package security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import model.Donnee;
import model.Utilisateur;

public class PdfDocument {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public byte[] creationPdf(int idWorkflow, List<Donnee> toutesDonnees) throws IOException {
        PDDocument document = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                
                // --- TITRE DU DOCUMENT ---
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Recapitulatif Workflow #" + idWorkflow);
                contentStream.endText();

                int y = 710; 
                int derniereEtapeAffichee = -1;

                if (toutesDonnees != null && !toutesDonnees.isEmpty()) {
                    for (Donnee d : toutesDonnees) {
                        
                        // Sécurité bas de page
                        if (y < 80) break; 

                        // --- TITRE DE L'ÉTAPE ---
                        int currentNbEtape = d.getEtape().getNbEtape();
                        if (currentNbEtape != derniereEtapeAffichee) {
                            derniereEtapeAffichee = currentNbEtape;
                            String nomRole = Utilisateur.getRole(currentNbEtape);
                            
                            y -= 25;
                            contentStream.beginText();
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                            contentStream.newLineAtOffset(50, y);
                            contentStream.showText("--- " + currentNbEtape + ". " + nomRole + " ---");
                            contentStream.endText();
                            y -= 20;
                        }

                        // --- LIGNE DE DONNÉE (DATE + DONNÉE) ---
                        contentStream.beginText();
                        
                        // 1. La date tout à gauche (X = 50)
                        contentStream.setFont(PDType1Font.HELVETICA, 9);
                        contentStream.newLineAtOffset(50, y);
                        String strDate = (d.getDate() != null) ? dateFormat.format(d.getDate()) : "          "; 
                        contentStream.showText(strDate);

                        // 2. La donnée décalée (X = 120, donc un décalage de 70 par rapport à la date)
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                        contentStream.newLineAtOffset(70, 0); // Décalage horizontal relatif
                        
                        String type = (d.getType() != null) ? d.getType() : "Champ";
                        String valeur = (d.getAttribut() != null) ? d.getAttribut() : "";
                        contentStream.showText("- " + nettoyerTexte(type + " : " + valeur));
                        
                        contentStream.endText();

                        // --- COMMENTAIRE (Ligne suivante avec retrait "Tab") ---
                        if (d.getCommentaire() != null && !d.getCommentaire().trim().isEmpty() && !d.getCommentaire().equalsIgnoreCase("null")) {
                            y -= 12;
                            contentStream.beginText();
                            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                            
                            // On aligne le commentaire sous la donnée (X = 135 pour l'effet Tab)
                            contentStream.newLineAtOffset(135, y); 
                            contentStream.showText(nettoyerTexte(d.getCommentaire()));
                            
                            contentStream.endText();
                        }
                        
                        y -= 18; // Espace avant le bloc suivant
                    }
                }
            } 

            document.save(out);
        } finally {
            document.close();
        }
        return out.toByteArray();
    }

    private String nettoyerTexte(String texte) {
        if (texte == null) return "";
        return texte.replaceAll("[^\\x00-\\x7F]", ""); 
    }
}