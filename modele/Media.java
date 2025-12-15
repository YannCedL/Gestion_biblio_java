package modele;

import java.io.Serializable;

public abstract class Media implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String titre;
    protected int anneePublication;

    // Si null, alors c'est disponible. Sinon, cela contient le nom de l'emprunteur.
    protected String emprunteurActuel;

    public Media(String titre, int anneePublication) {
        this.titre = titre;
        this.anneePublication = anneePublication;
        this.emprunteurActuel = null;
    }

    public abstract void afficherDetails();

    public String getTitre() {
        return titre;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public String getEmprunteurActuel() {
        return emprunteurActuel;
    }

    public boolean estDisponible() {
        return emprunteurActuel == null;
    }
}
