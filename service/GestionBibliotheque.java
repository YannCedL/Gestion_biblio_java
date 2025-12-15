package service;

import modele.*;
import exception.MediaNonTrouveException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GestionBibliotheque {

    // Changement MAJEUR : Une clé (Titre) pointe vers une LISTE de médias
    private HashMap<String, List<Media>> bibliotheque;
    private final String FICHIER_SAUVEGARDE = "bibliotheque.dat";

    public GestionBibliotheque() {
        bibliotheque = new HashMap<>();
        chargerDonnees();
    }

    public void ajouterMedia(Media media) {
        String titre = media.getTitre();

        // 1. On récupère (ou crée) la liste pour ce titre
        List<Media> liste = bibliotheque.computeIfAbsent(titre, k -> new ArrayList<>());

        // 2. CHECK SUBTIL : On vérifie si un média DE MÊME TYPE existe déjà
        for (Media m : liste) {
            // Si c'est la même classe (ex: Livre et Livre), c'est un doublon interdit
            if (m.getClass().equals(media.getClass())) {
                System.out.println("⚠️ Erreur : Le " + m.getClass().getSimpleName() + " '" + titre + "' existe déjà !");
                return; // On annule l'ajout
            }
        }

        // 3. Si tout est bon, on ajoute
        liste.add(media);
        System.out.println("Succès : '" + titre + "' (" + media.getClass().getSimpleName() + ") ajouté.");
    }

    // Suppression spécifique : on doit savoir LEQUEL supprimer si y'en a plusieurs
    public boolean supprimerMedia(Media mediaASupprimer) {
        String titre = mediaASupprimer.getTitre();
        List<Media> liste = bibliotheque.get(titre);

        if (liste != null) {
            boolean removed = liste.remove(mediaASupprimer);
            // Si la liste devient vide après suppression, on nettoie la Map
            if (liste.isEmpty()) {
                bibliotheque.remove(titre);
            }
            return removed;
        }
        return false;
    }

    // Retourne TOUS les médias qui ont ce titre exact
    public List<Media> rechercherMediasExacts(String titre) throws MediaNonTrouveException {
        List<Media> liste = bibliotheque.get(titre);
        if (liste == null || liste.isEmpty()) {
            throw new MediaNonTrouveException("Aucun média trouvé avec le titre exact : " + titre);
        }
        return liste;
    }

    // Recherche un peu floue (contient le mot)
    public List<Media> rechercherParMotCle(String motCle) {
        List<Media> resultats = new ArrayList<>();
        String motCleMinuscule = motCle.toLowerCase();

        // On parcourt toutes les LISTES de la map
        for (List<Media> liste : bibliotheque.values()) {
            for (Media m : liste) {
                if (m.getTitre().toLowerCase().contains(motCleMinuscule)) {
                    resultats.add(m);
                }
            }
        }
        return resultats;
    }

    public List<Media> rechercherEmpruntsParUtilisateur(String nomUtilisateur) {
        List<Media> emprunts = new ArrayList<>();
        String nomCible = nomUtilisateur.toLowerCase();

        for (List<Media> liste : bibliotheque.values()) {
            for (Media m : liste) {
                String emprunteur = m.getEmprunteurActuel();
                if (emprunteur != null && emprunteur.toLowerCase().equals(nomCible)) {
                    emprunts.add(m);
                }
            }
        }
        return emprunts;
    }

    public void listerMedias() {
        if (bibliotheque.isEmpty()) {
            System.out.println("La bibliothèque est vide.");
        } else {
            System.out.println("\n--- CONTENU DE LA BIBLIOTHÈQUE (Trié A-Z) ---");

            // SUBTILITÉ : On trie les clés (Titres) par ordre alphabétique
            List<String> titresTries = new ArrayList<>(bibliotheque.keySet());
            java.util.Collections.sort(titresTries);

            for (String titre : titresTries) {
                List<Media> liste = bibliotheque.get(titre);
                for (Media m : liste) {
                    m.afficherDetails();
                }
            }
            System.out.println("---------------------------------------------");
        }
    }

    public void afficherStatistiques() {
        int total = 0;
        int empruntes = 0;
        int nbLivres = 0;
        int nbCD = 0;
        int nbMagazines = 0;

        for (List<Media> liste : bibliotheque.values()) {
            for (Media m : liste) {
                total++;
                if (!m.estDisponible()) {
                    empruntes++;
                }
                if (m instanceof Livre)
                    nbLivres++;
                else if (m instanceof CD)
                    nbCD++;
                else if (m instanceof Magazine)
                    nbMagazines++;
            }
        }

        System.out.println("\n--- STATISTIQUES ---");
        System.out.println("Total médias   : " + total);
        System.out.println("  - Livres     : " + nbLivres);
        System.out.println("  - CD         : " + nbCD);
        System.out.println("  - Magazines  : " + nbMagazines);
        System.out.println("Médias sortis  : " + empruntes);
        System.out.println("Disponibles    : " + (total - empruntes));
    }

    public void sauvegarderDonnees() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FICHIER_SAUVEGARDE))) {
            oos.writeObject(bibliotheque);
            System.out.println("Sauvegarde terminée.");
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void chargerDonnees() {
        File fichier = new File(FICHIER_SAUVEGARDE);
        if (!fichier.exists())
            return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            // Attention : maintenant on lit une Map<String, List<Media>>
            bibliotheque = (HashMap<String, List<Media>>) ois.readObject();

            // Calculer la taille totale pour l'affichage
            int count = 0;
            for (List<Media> l : bibliotheque.values())
                count += l.size();

            System.out.println("Données chargées (" + count + " médias).");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur de chargement (fichier corrompu ou version incompatible).");
        }
    }
}
