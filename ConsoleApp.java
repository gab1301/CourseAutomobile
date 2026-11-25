import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final GestionnaireCourses gestionnaire;
    private final Scanner scanner;

    public ConsoleApp(GestionnaireCourses gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new ConsoleApp(new GestionnaireCourses()).executer(args);
    }

    public void executer(String[] args) {
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = scanner.nextLine().trim();
            try {
                switch (choix) {
                    case "1" -> gererAjoutVoiture();
                    case "2" -> gererAffichageVoitures();
                    case "3" -> gererRechercheVoiture();
                    case "4" -> gererModificationVoiture();
                    case "5" -> gererSuppressionVoiture();
                    case "6" -> gererCreationCourse();
                    case "7" -> gererDemarrageCourse();
                    case "8" -> gererConsultationResultats();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void afficherMenu() {
        System.out.println("\n=== GESTIONNAIRE DE COURSES ===");
        System.out.println("1. Ajouter une voiture");
        System.out.println("2. Afficher les voitures");
        System.out.println("3. Rechercher une voiture");
        System.out.println("4. Modifier une voiture");
        System.out.println("5. Supprimer une voiture");
        System.out.println("6. Créer une course");
        System.out.println("7. Démarrer / simuler une course");
        System.out.println("8. Consulter les résultats de la dernière course");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private void gererAjoutVoiture() {
        System.out.print("ID : ");
        String id = scanner.nextLine().trim();
        System.out.print("Nom : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Marque : ");
        String marque = scanner.nextLine().trim();
        System.out.print("Vitesse (> 0) : ");
        double vitesse = Double.parseDouble(scanner.nextLine().trim());

        gestionnaire.ajouterVoiture(id, nom, marque, vitesse);
        System.out.println("Voiture ajoutée.");
    }

    private void gererAffichageVoitures() {
        List<Voiture> voitures = gestionnaire.listerVoitures();
        if (voitures.isEmpty()) {
            System.out.println("Aucune voiture dans le garage.");
            return;
        }
        voitures.forEach(System.out::println);
    }

    private void gererRechercheVoiture() {
        System.out.print("ID de la voiture : ");
        String id = scanner.nextLine().trim();
        Voiture v = gestionnaire.rechercherVoiture(id);
        System.out.println(v != null ? v : "Voiture introuvable.");
    }

    private void gererModificationVoiture() {
        System.out.print("ID de la voiture à modifier : ");
        String id = scanner.nextLine().trim();
        System.out.print("Nouveau nom : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Nouvelle marque : ");
        String marque = scanner.nextLine().trim();
        System.out.print("Nouvelle vitesse (> 0) : ");
        double vitesse = Double.parseDouble(scanner.nextLine().trim());

        gestionnaire.modifierVoiture(id, nom, marque, vitesse);
        System.out.println("Voiture modifiée.");
    }

    private void gererSuppressionVoiture() {
        System.out.print("ID de la voiture à supprimer : ");
        String id = scanner.nextLine().trim();
        boolean supprimee = gestionnaire.supprimerVoiture(id);
        System.out.println(supprimee ? "Voiture supprimée." : "Voiture introuvable.");
    }

    private void gererCreationCourse() {
        System.out.print("ID de la course : ");
        String id = scanner.nextLine().trim();
        System.out.print("Nom de la course : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Distance totale (> 0) : ");
        double distance = Double.parseDouble(scanner.nextLine().trim());

        List<Voiture> disponibles = gestionnaire.listerVoitures();
        if (disponibles.size() < 2) {
            System.out.println("Il faut au moins 2 voitures dans le garage.");
            return;
        }

        System.out.println("Voitures disponibles :");
        disponibles.forEach(System.out::println);
        System.out.print("Entrez les IDs séparés par des virgules : ");
        String[] ids = scanner.nextLine().split(",");

        List<Voiture> selection = new ArrayList<>();
        for (String rawId : ids) {
            Voiture v = gestionnaire.rechercherVoiture(rawId.trim());
            if (v != null && !selection.contains(v)) {
                selection.add(v);
            }
        }

        gestionnaire.creerCourse(id, nom, distance, selection);
        System.out.println("Course créée.");
    }

    private void gererDemarrageCourse() {
        System.out.print("Simulation automatique (o/n) ? ");
        String mode = scanner.nextLine().trim().toLowerCase();

        if ("o".equals(mode)) {
            Course terminee = gestionnaire.lancerSimulationAutomatique();
            afficherClassementCourse(terminee);
        } else {
            gestionnaire.demarrerCourse();
            while (gestionnaire.consulterResultatsDerniereCourse() == null
                    || gestionnaire.consulterResultatsDerniereCourse().getEtat() != EtatCourse.TERMINEE) {
                try {
                    gestionnaire.simulerTour();
                } catch (IllegalStateException e) {
                    break;
                }
            }
            afficherClassementCourse(gestionnaire.consulterResultatsDerniereCourse());
        }
    }

    private void gererConsultationResultats() {
        Course derniere = gestionnaire.consulterResultatsDerniereCourse();
        if (derniere == null) {
            System.out.println("Aucune course terminée pour le moment.");
            return;
        }
        afficherClassementCourse(derniere);
    }

    private void afficherClassementCourse(Course course) {
        if (course == null) {
            return;
        }
        System.out.println("\n--- Résultats : " + course.getNom() + " ---");
        List<ParticipationCourse> classement = course.getClassement();
        for (int i = 0; i < classement.size(); i++) {
            ParticipationCourse p = classement.get(i);
            System.out.printf("%d. %s (ID: %s) - Temps théorique: %.2f%n",
                    i + 1, p.getVoiture().getNom(), p.getVoiture().getId(), p.getTempsArriveeTheorique());
        }
        ParticipationCourse gagnant = course.getGagnant();
        if (gagnant != null) {
            System.out.println("Gagnant : " + gagnant.getVoiture().getNom());
        }
    }
}
