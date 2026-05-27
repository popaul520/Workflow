package security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    // Polices TrueType chargées dynamiquement pour le support UTF-8 intégral
    private PDType0Font fontRegular;
    private PDType0Font fontBold;
    private PDType0Font fontItalic;

    public byte[] creationPdf(int idWorkflow, List<Donnee> toutesDonnees) throws IOException {
        document = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // 1. CHARGEMENT DES POLICES TRUE TYPE (Nécessaire pour les accents français)
            // On charge les fichiers de police depuis le classpath
            try (InputStream inReg = getClass().getResourceAsStream("/fonts/Arial.ttf");
                 InputStream inBold = getClass().getResourceAsStream("/fonts/Arial-Bold.ttf");
                 InputStream inItalic = getClass().getResourceAsStream("/fonts/Arial-Italic.ttf")) {
                
                if (inReg == null) {
                    // Repli de secours si tes fichiers de ressources ne sont pas encore prêts
                    throw new IOException("Fichiers de polices .ttf introuvables dans les ressources (/fonts/)");
                }
                
                fontRegular = PDType0Font.load(document, inReg);
                fontBold = PDType0Font.load(document, inBold);
                fontItalic = PDType0Font.load(document, inItalic);
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
                    // Sécurité saut de page avant d'insérer la ligne 
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

    /**
     *  Génère une nouvelle page blanche et ferme le flux de la précédente si elle existe.
     */
    private void nouvellePage() throws IOException {
        if (contentStream != null) {
            contentStream.close(); // Ferme la page
        }
        currentPage = new PDPage();
        document.addPage(currentPage);
        contentStream = new PDPageContentStream(document, currentPage);
        y = 750; 
    }

    /**
     *  Détecte si le texte va dépasser la marge basse et force le passage à la page suivante
     */
    private void verifierEspaceEtSauter(int espaceNecessaire) throws IOException {
        // Si le curseur actuel moins l'espace requis descend sous la marge de sécurité (60)
        if ((y - espaceNecessaire) < 60) {
            nouvellePage();
        }
    }
}