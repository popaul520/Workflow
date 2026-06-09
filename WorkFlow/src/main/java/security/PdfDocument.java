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

    // Utilisation des polices standards (Zéro fichier ou dépendance externe requis)
    private final PDFont fontRegular = PDType1Font.HELVETICA;
    private final PDFont fontBold = PDType1Font.HELVETICA_BOLD;
    private final PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;

    public byte[] creationPdf(int idWorkflow, List<Donnee> toutesDonnees) throws IOException {
        document = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // 1. Initialisation de la première page
            nouvellePage();

            // ================= TITRE PRINCIPAL =================
            contentStream.beginText();
            contentStream.setFont(fontBold, 18);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("Rapport de Synthese - Workflow #" + idWorkflow);
            contentStream.endText();
            y -= 35;

            // ================= BLOC 1 : SYNTHÈSE DES AVIS (EN PREMIER) =================
            contentStream.beginText();
            contentStream.setFont(fontBold, 12);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("SYNTHESE DES AVIS DECISIONNELS :");
            contentStream.endText();
            y -= 20;

            if (toutesDonnees != null && !toutesDonnees.isEmpty()) {
                // Première passe : Extraction exclusive des données de type "Avis"
                for (Donnee d : toutesDonnees) {
                    String type = (d.getType() != null) ? d.getType() : "";
                    
                    if (type.toLowerCase().startsWith("avis")) {
                        verifierEspaceEtSauter(20);
                        
                        contentStream.beginText();
                        contentStream.setFont(fontBold, 10);
                        contentStream.newLineAtOffset(65, y); // Léger retrait vers la droite
                        
                        int numEtape = (d.getEtape() != null) ? d.getEtape().getNbEtape() : 0;
                        String nomGroupe = Utilisateur.getRole(numEtape);
                        if (nomGroupe == null) nomGroupe = type;

                        String valeurAvis = (d.getAttribut() != null) ? d.getAttribut() : "Non renseigne";
                        String ligneAvis = "• " + nomGroupe + " -> " + valeurAvis;
                        
                        contentStream.showText(purgerTexte(ligneAvis));
                        contentStream.endText();
                        y -= 15;
                    }
                }
            }
            
            // Espace de transition avant la chronologie détaillée
            y -= 20; 
            verifierEspaceEtSauter(40);

            // ================= BLOC 2 : CHRONOLOGIE ET DÉTAILS COMPLET =================
            contentStream.beginText();
            contentStream.setFont(fontBold, 12);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("CHRONOLOGIE ET DETAILS DU WORKFLOW :");
            contentStream.endText();
            y -= 25;

            int derniereEtapeAffichee = -1;

            if (toutesDonnees != null && !toutesDonnees.isEmpty()) {
                // Deuxième passe : Parcours complet pour l'affichage chronologique standard
                for (Donnee d : toutesDonnees) {

                    int currentNbEtape = (d.getEtape() != null) ? d.getEtape().getNbEtape() : 0;
                    
                    // Si on change d'étape, on écrit une ligne de rupture / sous-titre d'étape
                    if (currentNbEtape != derniereEtapeAffichee) {
                        derniereEtapeAffichee = currentNbEtape;
                        String nomRole = Utilisateur.getRole(currentNbEtape);
                        if (nomRole == null) nomRole = "Utilisateur";
                        
                        verifierEspaceEtSauter(45);
                        y -= 15;
                        
                        contentStream.beginText();
                        contentStream.setFont(fontBold, 11);
                        contentStream.newLineAtOffset(50, y);
                        contentStream.showText("--- Etape " + currentNbEtape + " : " + purgerTexte(nomRole) + " ---");
                        contentStream.endText();
                        y -= 20;
                    }

                    // Écriture d'une ligne de donnée classique
                    verifierEspaceEtSauter(20);

                    contentStream.beginText();
                    contentStream.setFont(fontRegular, 9);
                    contentStream.newLineAtOffset(50, y);
                    String strDate = (d.getDate() != null) ? dateFormat.format(d.getDate()) : "          "; 
                    contentStream.showText(strDate);

                    contentStream.setFont(fontBold, 10);
                    contentStream.newLineAtOffset(70, 0); // Décale à droite après la date
                    
                    String type = (d.getType() != null) ? d.getType() : "Champ";
                    String valeur = (d.getAttribut() != null) ? d.getAttribut() : "";
                    String ligneTexte = "- " + type + " : " + valeur;
                    
                    contentStream.showText(purgerTexte(ligneTexte)); 
                    contentStream.endText();

                    // Traitement et affichage du commentaire si existant
                    if (d.getCommentaire() != null && !d.getCommentaire().trim().isEmpty() && !d.getCommentaire().equalsIgnoreCase("null")) {
                        verifierEspaceEtSauter(15);
                        y -= 12;
                        
                        contentStream.beginText();
                        contentStream.setFont(fontItalic, 9);
                        contentStream.newLineAtOffset(135, y); // Aligné sous la valeur du champ
                        contentStream.showText(purgerTexte(d.getCommentaire()));
                        contentStream.endText();
                    }
                    
                    y -= 15; // Marge entre les lignes de données
                }
            }

            // Fermeture du flux avant écriture du document final
            if (contentStream != null) {
                contentStream.close();
                contentStream = null;
            }

            document.save(out);
            
        } finally {
            // Le bloc finally s'exécute TOUJOURS et sécurise les fermetures
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
        y = 750; // Positionnement en haut de la nouvelle feuille
    }

    private void verifierEspaceEtSauter(int espaceNecessaire) throws IOException {
        if ((y - espaceNecessaire) < 60) { // Zone de sécurité pour éviter d'écrire dans la marge basse
            nouvellePage();
        }
    }

    /**
     * Purge le texte des sauts de lignes et remplace les caractères accentués français
     * pour empêcher la bibliothèque Apache PDFBox de lever une exception "unmappable character".
     */
    private String purgerTexte(String texte) {
        if (texte == null) return "";
        String resultat = texte.replace("\n", " ").replace("\r", "").trim();
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