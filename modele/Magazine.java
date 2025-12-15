package modele;

import exception.MediaDejaEmprunteException;

public class Magazine extends Media implements Empruntable {

    private static final long serialVersionUID = 1L;
    private int numeroEdition;

    public Magazine(String titre, int anneePublication, int numeroEdition) {
        super(titre, anneePublication);
        this.numeroEdition = numeroEdition;
    }

    @Override
    public void afficherDetails() {
        System.out.print("[MAGAZINE] " + titre + " N°" + numeroEdition + " (" + anneePublication + "). ");
        if (estDisponible()) {
            System.out.println("-> DISPONIBLE");
        } else {
            System.out.println("-> EMPRUNTÉ par : " + emprunteurActuel);
        }
    }

    @Override
    public void emprunter(String emprunteur) throws MediaDejaEmprunteException {
        if (!estDisponible()) {
            throw new MediaDejaEmprunteException("Le magazine '" + titre + "' est déjà chez " + emprunteurActuel + ".");
        }
        this.emprunteurActuel = emprunteur;
        System.out.println("-> Magazine emprunté avec succès par " + emprunteur + ".");
    }

    @Override
    public void rendre() {
        this.emprunteurActuel = null;
        System.out.println("-> Magazine rendu : " + titre);
    }
}
