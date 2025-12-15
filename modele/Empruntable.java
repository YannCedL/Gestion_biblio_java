package modele;

import exception.MediaDejaEmprunteException;

public interface Empruntable {
    void emprunter(String emprunteur) throws MediaDejaEmprunteException;

    void rendre();
}
