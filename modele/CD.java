package modele;

import exception.MediaDejaEmprunteException;

public class CD extends Media implements Empruntable {

    private static final long serialVersionUID = 1L;
    private String artiste;
    private int nombrePistes;

    public CD(String titre, int anneePublication, String artiste, int nombrePistes) {
        super(titre, anneePublication);
        this.artiste = artiste;
        this.nombrePistes = nombrePistes;
    }

    @Override
    public void afficherDetails() {
        System.out.print(
                "[CD] " + titre + " (" + anneePublication + ") - " + artiste + " (" + nombrePistes + " pistes). ");
        if (estDisponible()) {
            System.out.println("-> DISPONIBLE");
        } else {
            System.out.println("-> EMPRUNTÉ par : " + emprunteurActuel);
        }
    }

    public String getArtiste() {
        return artiste;
    }

    @Override
    public void emprunter(String emprunteur) throws MediaDejaEmprunteException {
        if (!estDisponible()) {
            throw new MediaDejaEmprunteException("Le CD '" + titre + "' est déjà chez " + emprunteurActuel + ".");
        }
        this.emprunteurActuel = emprunteur;
        System.out.println("-> CD emprunté avec succès par " + emprunteur + ".");
    }

    @Override
    public void rendre() {
        this.emprunteurActuel = null;
        System.out.println("-> CD rendu : " + titre);
    }
}
