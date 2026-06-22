package security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import dao.WorkflowDAO;
import model.Donnee;
import model.Utilisateur;

public class PdfDocument {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    private PDDocument document;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private int y;

    // Utilisation des polices standards
    private final PDFont fontRegular = PDType1Font.HELVETICA;
    private final PDFont fontBold = PDType1Font.HELVETICA_BOLD;
    private final PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;

    public byte[] creationPdf(int idWorkflow, List<Donnee> toutesDonnees) throws IOException {
        document = new PDDocument();
        String titre = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
        	titre = WorkflowDAO.getTitre(idWorkflow);
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
            // 1. Initialisation de la première page
            nouvellePage();
            // ================= TITRE PRINCIPAL =================
            contentStream.beginText();
            contentStream.setFont(fontBold, 18);
            contentStream.newLineAtOffset(50, y);
            contentStream.showText("Rapport de Synthese - "+ titre +" id : #" + idWorkflow);
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
                        int numEtape = (d.getEtape() != null) ? d.getEtape().getNbEtape() : 0;
                        String nomGroupe = Utilisateur.getRole(numEtape);
                        if (nomGroupe == null) nomGroupe = type;

                        String valeurAvis = (d.getAttribut() != null) ? d.getAttribut() : "Non renseigne";
                        String ligneAvis = "• " + nomGroupe + " -> " + valeurAvis;
                        
                        // Découpage strict par nombre de caractères (Marge gauche de 65)
                        ecrireTexteMultiLignes(purgerTexte(ligneAvis), fontBold, 10, 65, 15);
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

                    // Écriture d'une ligne de donnée classique (Date d'abord)
                    verifierEspaceEtSauter(20);

                    contentStream.beginText();
                    contentStream.setFont(fontRegular, 9);
                    contentStream.newLineAtOffset(50, y);
                    String strDate = (d.getDate() != null) ? dateFormat.format(d.getDate()) : "          "; 
                    contentStream.showText(strDate);
                    contentStream.endText();

                    // Affichage décalé du type et de la valeur
                    String type = (d.getType() != null) ? d.getType() : "Champ";
                    String valeur = (d.getAttribut() != null) ? d.getAttribut() : "";
                    String ligneTexte = "- " + type + " : " + valeur;
                    
                    // On écrit le texte principal à partir de X = 120
                    ecrireTexteMultiLignes(purgerTexte(ligneTexte), fontBold, 10, 120, 15);

                    // Traitement et affichage du commentaire si existant
                    if (d.getCommentaire() != null && !d.getCommentaire().trim().isEmpty() && !d.getCommentaire().equalsIgnoreCase("null")) {
                        String commentaire = d.getCommentaire();
                        // Affichage en retrait sous le champ (X = 135) avec interligne de 12
                        ecrireTexteMultiLignes(purgerTexte(commentaire), fontItalic, 9, 135, 12);
                    }
                    
                    y -= 10; // Espacement de sécurité après le bloc complet d'une donnée
                }
            }

            // Fermeture du flux avant écriture du document final
            if (contentStream != null) {
                contentStream.close();
                contentStream = null;
            }

            document.save(out);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de la generation du document PDF", e);
        } finally {
            if (contentStream != null) {
                try {
                    contentStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        y = 750; // Réinitialisation de la hauteur de page
    }

    private void verifierEspaceEtSauter(int espaceNecessaire) throws IOException {
        if ((y - espaceNecessaire) < 60) { 
            nouvellePage();
        }
    }

    /**
     * Reçoit le texte purifié et force le saut de ligne de manière séquentielle 
     * en recalculant continuellement l'ordonnée Y globale.
     */
    private void ecrireTexteMultiLignes(String texte, PDFont font, int fontSize, int margeGauche, int interLigne) throws IOException {
        // Fixe une limite de sécurité stricte : max 65 caractères par sous-ligne
        int limiteCaracteres = 65; 
        
        List<String> lignesDeTexte = diviserParNombreCaracteres(texte, limiteCaracteres);
        
        for (String ligne : lignesDeTexte) {
            verifierEspaceEtSauter(interLigne + 5);
            
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(margeGauche, y);
            contentStream.showText(ligne);
            contentStream.endText();
            
            // On descend l'ordonnée Y globale pour la ligne suivante
            y -= interLigne;
        }
    }

    /**
     * Découpe mathématiquement une chaîne de caractères tous les 'tailleMax' caractères.
     * Évite définitivement l'extension à l'infini à l'écran.
     */
    private List<String> diviserParNombreCaracteres(String texte, int tailleMax) {
        List<String> fragments = new ArrayList<>();
        if (texte == null || texte.isEmpty()) {
            return fragments;
        }

        int longueur = texte.length();
        int index = 0;
        
        while (index < longueur) {
            // Extrait un sous-bloc d'au maximum 'tailleMax' caractères
            int finIndex = Math.min(index + tailleMax, longueur);
            fragments.add(texte.substring(index, finIndex));
            index += tailleMax;
        }
        
        return fragments;
    }

    /**
     * Purge le texte des sauts de lignes internes et remplace les caractères accentués français.
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