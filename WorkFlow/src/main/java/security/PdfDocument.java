package security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import model.Donnee;
import model.Utilisateur;

public class PdfDocument {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // Variables globales pour gérer la pagination dynamique
    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private int y;

    // Polices chargées depuis le système d'exploitation (évite le dossier /fonts/)
    private PDType0Font fontRegular;
    private PDType0Font fontBold;
    private PDType0Font fontItalic;

    public byte[] creationPdf(int idWorkflow, List<Donnee> toutesDonnees) throws IOException {
        document = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // CHARGEMENT DE SÉCURITÉ : On pioche directement dans les polices de l'OS pour avoir les accents (é, è, à...)
            String os = System.getProperty("os.name").toLowerCase();
            File fontDir;
            
            if (os.contains("win")) {
                fontDir = new File("C:/Windows/Fonts/");
            } else if (os.contains("mac")) {
                fontDir = new File("/Library/Fonts/");
            } else {
                fontDir = new File("/usr/share/fonts/truetype/dejavu/"); // Linux de secours
            }

            // Liaison dynamique avec les fichiers de l'OS (Zéro fichier à ajouter dans ton projet Java)
            File fReg = new File(fontDir, os.contains("win") ? "arial.ttf" : "Arial.ttf");
            File fBold = new File(fontDir, os.contains("win") ? "arialbd.ttf" : "Arial Bold.ttf");
            File fItalic = new File(fontDir, os.contains("win") ? "ariali.ttf" : "Arial Italic.ttf");

            if (fReg.exists()) {
                fontRegular = PDType0Font.load(document, fReg);
                fontBold = PDType0Font.load(document, fBold);
                fontItalic = PDType0Font.load(document, fItalic);
            } else {
                // Si l'OS est ultra-restreint ou n'a pas Arial, on utilise le fallback standard de PDFBox
                // Attention : Les accents risquent de sauter ou de générer des erreurs sans fichier ttf.
                throw new IOException("Impossible de mapper les polices système pour l'UTF-8.");
            }

            // Initialisation de la première page
            nouvellePage();

            // TITRE DU DOCUMENT
            contentStream.beginText();
            contentStream.setFont(fontBold, 18);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("Récapitulatif Workflow #" + idWorkflow);
            contentStream.endText();

            y -= 40; 
            int derniereEtapeAffichee = -1;

            if (toutesDonnees != null && !toutesDonnees.isEmpty()) {
                for (Donnee d : toutesDonnees) {

                    // TITRE DE L'ÉTAPE
                    int currentNbEtape = d.getEtape().getNbEtape();
                    if (currentNbEtape != derniereEtapeAffichee) {
                        derniereEtapeAffichee = currentNbEtape;
                        String nomRole = Utilisateur.getRole(currentNbEtape);
                        
                        // Sécurité saut de page avant d'insérer un titre de section 
                        verifierEspaceEtSauter(45);
                        
                        y -= 25;
                        contentStream.beginText();
                        contentStream.setFont(fontBold, 12);
                        contentStream.newLineAtOffset(50, y);
                        contentStream.showText("--- " + currentNbEtape + ". " + nomRole + " ---");
                        contentStream.endText();
                        y -= 20;
                    }

                    // --- LIGNE DE DONNÉE 
                    verifierEspaceEtSauter(20);

                    contentStream.beginText();
                    contentStream.setFont(fontRegular, 9);
                    contentStream.newLineAtOffset(50, y);
                    String strDate = (d.getDate() != null) ? dateFormat.format(d.getDate()) : "          "; 
                    contentStream.showText(strDate);

                    contentStream.setFont(fontBold, 10);
                    contentStream.newLineAtOffset(70, 0); 
                    
                    String type = (d.getType() != null) ? d.getType() : "Champ";
                    String valeur = (d.getAttribut() != null) ? d.getAttribut() : "";
                    contentStream.showText("- " + type + " : " + valeur); 
                    
                    contentStream.endText();

                    // --- COMMENTAIRE
                    if (d.getCommentaire() != null && !d.getCommentaire().trim().isEmpty() && !d.getCommentaire().equalsIgnoreCase("null")) {
                        
                        verifierEspaceEtSauter(15);
                        
                        y -= 12;
                        contentStream.beginText();
                        contentStream.setFont(fontItalic, 9);
                        contentStream.newLineAtOffset(135, y); 
                        contentStream.showText(d.getCommentaire());
                        contentStream.endText();
                    }
                    
                    y -= 18; // Espace
                }
            }

            // Fermeture propre
            if (contentStream != null) {
                contentStream.close();
            }

            document.save(out);
        } finally {
            document.close();
        }
        return out.toByteArray();
    }

    private void nouvellePage() throws IOException {
        if (contentStream != null) {
            contentStream.close(); 
        }
        currentPage = new PDPage();
        document.addPage(currentPage);
        contentStream = new PDPageContentStream(document, currentPage);
        y = 750; 
    }

    private void verifierEspaceEtSauter(int espaceNecessaire) throws IOException {
        if ((y - espaceNecessaire) < 60) {
            nouvellePage();
        }
    }
}