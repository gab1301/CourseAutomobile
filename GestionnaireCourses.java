import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GestionnaireCourses {
    private final Map<String, Voiture> garage;
    private Course courseCourante;
    private Course derniereCourse;

    public GestionnaireCourses() {
        this.garage = new LinkedHashMap<>();
        this.courseCourante = null;
        this.derniereCourse = null;
    }

    public void ajouterVoiture(String id, String nom, String marque, double vitesse) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de la voiture est obligatoire.");
        }
        String idNettoye = id.trim();
        if (garage.containsKey(idNettoye)) {
            throw new IllegalArgumentException("Une voiture avec l'id '" + idNettoye + "' existe déjà.");
        }
        garage.put(idNettoye, new Voiture(idNettoye, nom, marque, vitesse));
    }

    public List<Voiture> listerVoitures() {
        return new ArrayList<>(garage.values());
    }

    public Voiture rechercherVoiture(String id) {
        if (id == null) {
            return null;
        }
        return garage.get(id.trim());
    }

    public void modifierVoiture(String id, String nom, String marque, double vitesse) {
        String idNettoye = id == null ? null : id.trim();
        Voiture voiture = garage.get(idNettoye);
        if (voiture == null) {
            throw new IllegalArgumentException("Voiture introuvable : " + id);
        }
        if (estVoitureEnCourseNonTerminee(idNettoye)) {
            throw new IllegalStateException("Modification interdite : la voiture est inscrite à une course non terminée.");
        }
        voiture.setNom(nom);
        voiture.setMarque(marque);
        voiture.setVitesse(vitesse);
    }

    public boolean supprimerVoiture(String id) {
        String idNettoye = id == null ? null : id.trim();
        if (idNettoye == null || !garage.containsKey(idNettoye)) {
            return false;
        }
        if (estVoitureEnCourseNonTerminee(idNettoye)) {
            throw new IllegalStateException("Suppression interdite : la voiture est inscrite à une course non terminée.");
        }
        return garage.remove(idNettoye) != null;
    }

    public boolean estVoitureEnCourseNonTerminee(String id) {
        if (id == null) {
            return false;
        }
        String idNettoye = id.trim();
        Course[] coursesAVerifier = {courseCourante, derniereCourse};
        for (Course c : coursesAVerifier) {
            if (c != null && c.getEtat() != EtatCourse.TERMINEE) {
                boolean inscrite = c.getParticipations().stream()
                        .anyMatch(p -> p.getVoiture().getId().equalsIgnoreCase(idNettoye));
                if (inscrite) {
                    return true;
                }
            }
        }
        return false;
    }

    public Course creerCourse(String id, String nom, double distanceTotale, List<Voiture> voituresSelectionnees) {
        Objects.requireNonNull(voituresSelectionnees, "La liste des voitures sélectionnées ne peut pas être nulle.");
        if (courseCourante != null && courseCourante.getEtat() == EtatCourse.EN_COURS) {
            throw new IllegalStateException("Une course est déjà en cours.");
        }

        Course nouvelleCourse = new Course(id, nom, distanceTotale);
        for (Voiture v : voituresSelectionnees) {
            nouvelleCourse.ajouterParticipant(v);
        }
        this.courseCourante = nouvelleCourse;
        return nouvelleCourse;
    }

    public Course demarrerCourse() {
        if (courseCourante == null) {
            throw new IllegalStateException("Aucune course créée à démarrer.");
        }
        courseCourante.demarrer();
        return courseCourante;
    }

    public void simulerTour() {
        if (courseCourante == null || courseCourante.getEtat() != EtatCourse.EN_COURS) {
            throw new IllegalStateException("Aucune course en cours pour simuler un tour.");
        }
        boolean terminee = courseCourante.simulerTour();
        if (terminee) {
            derniereCourse = courseCourante;
            courseCourante = null;
        }
    }

    public Course consulterResultatsDerniereCourse() {
        return derniereCourse;
    }

    public Course lancerSimulationAutomatique() {
        if (courseCourante == null) {
            throw new IllegalStateException("Aucune course créée.");
        }
        if (courseCourante.getEtat() == EtatCourse.EN_PREPARATION) {
            demarrerCourse();
        }
        while (courseCourante != null && courseCourante.getEtat() == EtatCourse.EN_COURS) {
            simulerTour();
        }
        return derniereCourse;
    }
}