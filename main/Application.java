package main;

import modele.*;
import service.GestionBibliotheque;
import exception.*;

import java.util.List;
import java.util.Scanner;

public class Application {

    private static Scanner scanner = new Scanner(System.in);
    private static GestionBibliotheque gestionnaire = new GestionBibliotheque();

    public static void main(String[] args) {
        boolean continuer = true;

        System.out.println("=== Gestion de Bibliothèque (v2.1) ===");

        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine();

            try {
                switch (choix) {
                    case "1":
                        ajouterLivre();
                        break;
                    case "2":
                        ajouterCD();
                        break;
                    case "3":
                        ajouterMagazine();
                        break;
                    case "4":
                        gestionnaire.listerMedias();
                        break;
                    case "5":
                        rechercherEtEmprunter();
                        break;
                    case "6":
                        rendreMediaParUtilisateur();
                        break;
                    case "7":
                        gestionnaire.afficherStatistiques();
                        break;
                    case "8":
                        supprimerMedia();
                        break;
                    case "9":
                        gestionnaire.sauvegarderDonnees();
                        continuer = false;
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.err.println("Erreur inattendue : " + e.getMessage());
            }
        }
    }

    private static void afficherMenu() {
        System.out.println("\n---------------- MENU ----------------");
        System.out.println("1. Ajouter Livre");
        System.out.println("2. Ajouter CD");
        System.out.println("3. Ajouter Magazine");
        System.out.println("4. Lister tout");
        System.out.println("5. Rechercher & Emprunter");
        System.out.println("6. Rendre un média (Par Nom)");
        System.out.println("7. Afficher Statistiques");
        System.out.println("8. Supprimer un média");
        System.out.println("9. Quitter & Sauvegarder");
        System.out.print("Votre choix > ");
    }

    private static void ajouterLivre() {
        System.out.println("--- Nouveau Livre ---");
        String titre = lireChaine("Titre : ");
        String auteur = lireChaine("Auteur : ");
        int annee = lireEntier("Année : ");
        int pages = lireEntier("Nombre de pages : ");

        gestionnaire.ajouterMedia(new Livre(titre, annee, auteur, pages));
    }

    private static void ajouterCD() {
        System.out.println("--- Nouveau CD ---");
        String titre = lireChaine("Titre : ");
        String artiste = lireChaine("Artiste : ");
        int annee = lireEntier("Année : ");
        int pistes = lireEntier("Nombre de pistes : ");

        gestionnaire.ajouterMedia(new CD(titre, annee, artiste, pistes));
    }

    private static void ajouterMagazine() {
        System.out.println("--- Nouveau Magazine ---");
        String titre = lireChaine("Titre : ");
        int numero = lireEntier("Numéro d'édition : ");
        int annee = lireEntier("Année : ");

        gestionnaire.ajouterMedia(new Magazine(titre, annee, numero));
    }

    private static void rechercherEtEmprunter() {
        System.out.println("--- Emprunter un média ---");
        System.out.println("Recherche (partie du titre) :");
        String motCle = lireChaine("Recherche : ");

        List<Media> resultats = gestionnaire.rechercherParMotCle(motCle);

        if (resultats.isEmpty()) {
            System.out.println("Aucun média trouvé.");
            return;
        }

        // On affiche les résultats avec un index pour faciliter le choix
        System.out.println("\nRésultats :");
        for (int i = 0; i < resultats.size(); i++) {
            System.out.print((i + 1) + ". ");
            resultats.get(i).afficherDetails();
        }

        System.out.println("\nEntrez le NUMÉRO du média à emprunter (ou 0 pour annuler) :");
        int choix = lireEntier("> ");

        if (choix < 1 || choix > resultats.size()) {
            System.out.println("Annulé.");
            return;
        }

        Media m = resultats.get(choix - 1);

        try {
            if (m instanceof Empruntable) {
                if (!m.estDisponible()) {
                    System.out.println("❌ Déjà emprunté par " + m.getEmprunteurActuel());
                    return;
                }
                String nom = lireChaine("Votre nom : ");
                ((Empruntable) m).emprunter(nom);
            } else {
                System.out.println("Ce média ne s'emprunte pas.");
            }
        } catch (MediaDejaEmprunteException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void rendreMediaParUtilisateur() {
        System.out.println("--- Retour Média ---");
        String nomUtilisateur = lireChaine("Quel est votre nom ? ");

        List<Media> emprunts = gestionnaire.rechercherEmpruntsParUtilisateur(nomUtilisateur);

        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt trouvé pour : " + nomUtilisateur);
            return;
        }

        System.out.println("\n--- Emprunts de " + nomUtilisateur + " ---");
        for (int i = 0; i < emprunts.size(); i++) {
            System.out.print((i + 1) + ". ");
            emprunts.get(i).afficherDetails();
        }

        System.out.println("\nEntrez le NUMÉRO du média à rendre (ou 0 pour tout rendre) :");
        int choix = lireEntier("> ");

        if (choix == 0) {
            for (Media m : emprunts) {
                if (m instanceof Empruntable)
                    ((Empruntable) m).rendre();
            }
            System.out.println("✅ Tout a été rendu !");
        } else if (choix > 0 && choix <= emprunts.size()) {
            Media m = emprunts.get(choix - 1);
            if (m instanceof Empruntable)
                ((Empruntable) m).rendre();
        } else {
            System.out.println("Choix invalide.");
        }
    }

    private static void supprimerMedia() {
        System.out.println("--- Suppression ---");
        String motCle = lireChaine("Titre à supprimer (recherche) : ");

        // On réutilise la recherche pour être sûr de supprimer le bon
        List<Media> resultats = gestionnaire.rechercherParMotCle(motCle);

        if (resultats.isEmpty()) {
            System.out.println("Rien trouvé.");
            return;
        }

        System.out.println("\nLequel supprimer ?");
        for (int i = 0; i < resultats.size(); i++) {
            System.out.print((i + 1) + ". ");
            resultats.get(i).afficherDetails();
        }

        System.out.println("\nNuméro (0 pour annuler) :");
        int choix = lireEntier("> ");

        if (choix > 0 && choix <= resultats.size()) {
            Media aSupprimer = resultats.get(choix - 1);
            if (gestionnaire.supprimerMedia(aSupprimer)) {
                System.out.println("✅ Supprimé.");
            } else {
                System.out.println("Erreur.");
            }
        }
    }

    // Méthode utilitaire blindée (Trim + Capitalisation automatique)
    private static String lireChaine(String invite) {
        System.out.print(invite);
        String s = scanner.nextLine();
        // On boucle tant que c'est vide
        while (s.trim().isEmpty()) {
            System.out.print("Entrée vide interdite. " + invite);
            s = scanner.nextLine();
        }
        s = s.trim();
        // Subtilité : On met la première lettre en majuscule, le reste tel quel
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static int lireEntier(String invite) {
        System.out.print(invite);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Veuillez entrer un nombre valide : ");
            }
        }
    }
}
