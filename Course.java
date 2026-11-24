import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Course {
    private String id;
    private String nom;
    private double distanceTotale;
    private EtatCourse etat;
    private int nombreToursTotal;
    private List<ParticipationCourse> participations;
    private List<ParticipationCourse> classement;

    public Course(String id, String nom, double distanceTotale) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de la course ne peut pas être vide.");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la course ne peut pas être vide.");
        }
        if (distanceTotale <= 0) {
            throw new IllegalArgumentException("La distance totale de la course doit être strictement supérieure à 0.");
        }
        this.id = id.trim();
        this.nom = nom.trim();
        this.distanceTotale = distanceTotale;
        this.etat = EtatCourse.EN_PREPARATION;
        this.nombreToursTotal = 0;
        this.participations = new ArrayList<>();
        this.classement = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public double getDistanceTotale() {
        return distanceTotale;
    }

    public EtatCourse getEtat() {
        return etat;
    }

    public void setEtat(EtatCourse etat) {
        if (etat == null) {
            throw new IllegalArgumentException("L'état de la course ne peut pas être nul.");
        }
        this.etat = etat;
    }

    public int getNombreToursTotal() {
        return nombreToursTotal;
    }

    public void setNombreToursTotal(int nombreToursTotal) {
        this.nombreToursTotal = nombreToursTotal;
    }

    public List<ParticipationCourse> getParticipations() {
        return Collections.unmodifiableList(participations);
    }

    public List<ParticipationCourse> getClassement() {
        return Collections.unmodifiableList(classement);
    }

    public void ajouterParticipant(Voiture voiture) {
        if (voiture == null) {
            throw new IllegalArgumentException("La voiture à inscrire ne peut pas être nulle.");
        }
        if (etat != EtatCourse.EN_PREPARATION) {
            throw new IllegalStateException("Impossible d'ajouter une voiture : la course n'est plus en préparation.");
        }
        for (ParticipationCourse p : participations) {
            if (p.getVoiture().getId().equalsIgnoreCase(voiture.getId())) {
                throw new IllegalArgumentException("La voiture " + voiture.getId() + " est déjà inscrite à cette course.");
            }
        }
        participations.add(new ParticipationCourse(voiture));
    }

    public boolean estValidePourDemarrage() {
        return etat == EtatCourse.EN_PREPARATION && participations.size() >= 2;
    }

    public void enregistrerArrivee(ParticipationCourse participation, int tourArrivee, double distanceTotale) {
        if (participation != null && !classement.contains(participation)) {
            classement.add(participation);
        }
    }

    public List<ParticipationCourse> genererClassement() {
        List<ParticipationCourse> resultat = new ArrayList<>(participations);
        resultat.sort(Comparator
                .comparingInt(ParticipationCourse::getToursEffectues)
                .thenComparingDouble(ParticipationCourse::getTempsArriveeTheorique)
                .thenComparing(p -> p.getVoiture().getId()));
        this.classement = resultat;
        return Collections.unmodifiableList(this.classement);
    }

    public ParticipationCourse getGagnant() {
        if (classement.isEmpty()) {
            genererClassement();
        }
        return classement.isEmpty() ? null : classement.get(0);
    }
}
