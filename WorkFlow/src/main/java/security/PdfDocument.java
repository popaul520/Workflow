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

        PDPageContentStream contentStream = null;

        try {
            PDPage page = new PDPage();
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);

            // --- TITRE ---
            ajouterTitre(contentStream, idWorkflow);

            int y = 710;
            int derniereEtapeAffichee = -1;

            if (toutesDonnees != null && !toutesDonnees.isEmpty()) {

                for (Donnee d : toutesDonnees) {

                    // 🔥 Gestion saut de page
                    if (y < 80) {
                        contentStream.close();

                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);

                        ajouterTitre(contentStream, idWorkflow);

                        y = 710;
                    }

                    // --- TITRE ETAPE ---
                    int currentNbEtape = d.getEtape().getNbEtape();

                    if (currentNbEtape != derniereEtapeAffichee) {
                        derniereEtapeAffichee = currentNbEtape;

                        y -= 25;

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        contentStream.newLineAtOffset(50, y);

                        String nomRole = Utilisateur.getRole(currentNbEtape);
                        contentStream.showText("--- " + currentNbEtape + ". " + nomRole + " ---");

                        contentStream.endText();

                        y -= 20;
                    }

                    // --- LIGNE DONNEE ---
                    contentStream.beginText();

                    // Date
                    contentStream.setFont(PDType1Font.HELVETICA, 9);
                    contentStream.newLineAtOffset(50, y);

                    String strDate = (d.getDate() != null)
                            ? dateFormat.format(d.getDate())
                            : "";

                    contentStream.showText(strDate);

                    // Donnée
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.newLineAtOffset(70, 0);

                    String type = (d.getType() != null) ? d.getType() : "Champ";
                    String valeur = (d.getAttribut() != null) ? d.getAttribut() : "";

                    contentStream.showText("- " + nettoyerTexte(type + " : " + valeur));

                    contentStream.endText();

                    // --- COMMENTAIRE ---
                    if (d.getCommentaire() != null
                            && !d.getCommentaire().trim().isEmpty()
                            && !d.getCommentaire().equalsIgnoreCase("null")) {

                        y -= 12;

                        // 🔥 Vérification espace avant écriture commentaire
                        if (y < 80) {
                            contentStream.close();

                            page = new PDPage();
                            document.addPage(page);
                            contentStream = new PDPageContentStream(document, page);

                            ajouterTitre(contentStream, idWorkflow);

                            y = 710;
                        }

                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                        contentStream.newLineAtOffset(135, y);

                        contentStream.showText(nettoyerTexte(d.getCommentaire()));

                        contentStream.endText();
                    }

                    y -= 18;
                }
            }

            contentStream.close();
            document.save(out);

        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    //  Méthode pour réutiliser le titre
    private void ajouterTitre(PDPageContentStream contentStream, int idWorkflow) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Recapitulatif Workflow #" + idWorkflow);
        contentStream.endText();
    }

    private String nettoyerTexte(String texte) {
        if (texte == null) return "";
        return texte.replaceAll("[^\\x00-\\x7F]", "");
    }
}