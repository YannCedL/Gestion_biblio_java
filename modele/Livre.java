package modele;

import exception.MediaDejaEmprunteException;

public class Livre extends Media implements Empruntable {

    private static final long serialVersionUID = 1L;
    private String auteur;
    private int nombrePages;

    public Livre(String titre, int anneePublication, String auteur, int nombrePages) {
        super(titre, anneePublication);
        this.auteur = auteur;
        this.nombrePages = nombrePages;
    }

    @Override
    public void afficherDetails() {
        System.out.print("[LIVRE] " + titre + " (" + anneePublication + ") par " + auteur + ", " + nombrePages + "p. ");
        if (estDisponible()) {
            System.out.println("-> DISPONIBLE");
        } else {
            System.out.println("-> EMPRUNTÉ par : " + emprunteurActuel);
        }
    }

    public String getAuteur() {
        return auteur;
    }

    @Override
    public void emprunter(String emprunteur) throws MediaDejaEmprunteException {
        if (!estDisponible()) {
            throw new MediaDejaEmprunteException("Le livre '" + titre + "' est déjà chez " + emprunteurActuel + ".");
        }
        this.emprunteurActuel = emprunteur;
        System.out.println("-> Livre emprunté avec succès par " + emprunteur + ".");
    }

    @Override
    public void rendre() {
        this.emprunteurActuel = null;
        System.out.println("-> Livre rendu : " + titre);
    }
}
