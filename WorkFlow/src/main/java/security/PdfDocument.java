package security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import model.Donnee;
import model.Utilisateur;

public class PdfDocument {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private int y;

    // Utilisation des polices standards intégrées à PDFBox (Zéro fichier externe requis)
    private final PDFont fontRegular = PDType1Font.HELVETICA;
    private final PDFont fontBold = PDType1Font.HELVETICA_BOLD;
    private final PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;

    public byte[] creationPdf(int idWorkflow, List<Donnee> toutesDonnees) throws IOException {
        document = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Initialisation de la première page
            nouvellePage();

            // TITRE DU DOCUMENT
            contentStream.beginText();
            contentStream.setFont(fontBold, 18);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("Recapitulatif Workflow #" + idWorkflow);
            contentStream.endText();

            y -= 40; 
            int derniereEtapeAffichee = -1;

            if (toutesDonnees != null && !toutesDonnees.isEmpty()) {
                for (Donnee d : toutesDonnees) {

                    // TITRE DE L'ÉTAPE
                    // Sécurité si d.getEtape() est nul
                    int currentNbEtape = (d.getEtape() != null) ? d.getEtape().getNbEtape() : 0;
                    if (currentNbEtape != derniereEtapeAffichee) {
                        derniereEtapeAffichee = currentNbEtape;
                        String nomRole = Utilisateur.getRole(currentNbEtape);
                        if (nomRole == null) nomRole = "Utilisateur";
                        
                        verifierEspaceEtSauter(45);
                        
                        y -= 25;
                        contentStream.beginText();
                        contentStream.setFont(fontBold, 12);
                        contentStream.newLineAtOffset(50, y);
                        contentStream.showText("--- " + currentNbEtape + ". " + purgerTexte(nomRole) + " ---");
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
                    
                    String ligneTexte = "- " + type + " : " + valeur;
                    contentStream.showText(purgerTexte(ligneTexte)); 
                    contentStream.endText();

                    // --- COMMENTAIRE
                    if (d.getCommentaire() != null && !d.getCommentaire().trim().isEmpty() && !d.getCommentaire().equalsIgnoreCase("null")) {
                        
                        verifierEspaceEtSauter(15);
                        
                        y -= 12;
                        contentStream.beginText();
                        contentStream.setFont(fontItalic, 9);
                        contentStream.newLineAtOffset(135, y); 
                        contentStream.showText(purgerTexte(d.getCommentaire()));
                        contentStream.endText();
                    }
                    
                    y -= 18; // Espace
                }
            }

            // Fermeture propre du flux de contenu avant sauvegarde
            if (contentStream != null) {
                contentStream.close();
                contentStream = null;
            }

            document.save(out);
        } finally {
            if (contentStream != null) {
                contentStream.close();
            }
            if (document != null) {
                document.close();
            }
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

    /**
     * Méthode essentielle de nettoyage pour éviter le crash de PDFBox (Helvetica Standard).
     * Supprime les retours à la ligne et remplace les caractères accentués français 
     * pour éviter les "unmappable character" sur les serveurs de production Linux.
     */
    private String purgerTexte(String texte) {
        if (texte == null) return "";
        
        // Nettoyage des sauts de ligne incompatibles avec showText
        String resultat = texte.replace("\n", " ").replace("\r", "").trim();
        
        // Remplacement des caractères accentués pour Helvetica Standard
        resultat = resultat.replaceAll("[éèêë]", "e")
                           .replaceAll("[àâä]", "a")
                           .replaceAll("[ùûü]", "u")
                           .replaceAll("[îï]", "i")
                           .replaceAll("[ôö]", "o")
                           .replaceAll("[ç]", "c")
                           .replaceAll("[ÉÈÊË]", "E")
                           .replaceAll("[ÀÂÄ]", "A")
                           .replaceAll("[Ç]", "C");
                           
        return resultat;
    }
}