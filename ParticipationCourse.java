

public class ParticipationCourse {
    private Voiture voiture;
    private double distanceParcourue;
    private int toursEffectues;
    private boolean terminee;
    private double tempsArriveeTheorique;

    public ParticipationCourse(Voiture voiture) {
        if (voiture == null) {
            throw new IllegalArgumentException("La voiture associée à la participation ne peut pas être nulle.");
        }
        this.voiture = voiture;
        reinitialiser();
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public double getDistanceParcourue() {
        return distanceParcourue;
    }

    public int getToursEffectues() {
        return toursEffectues;
    }

    public boolean isTerminee() {
        return terminee;
    }

    public double getTempsArriveeTheorique() {
        return tempsArriveeTheorique;
    }

    public boolean avancer(double distanceTotale, int numeroTour) {
        if (distanceTotale <= 0) {
            throw new IllegalArgumentException("La distance totale doit être strictement supérieure à 0.");
        }
        if (terminee) {
            return true;
        }
        double vitesse = voiture.getVitesse();
        if (vitesse <= 0) {
            throw new IllegalStateException("La vitesse de la voiture doit être strictement supérieure à 0.");
        }
        distanceParcourue += vitesse;
        toursEffectues = numeroTour;

        if (distanceParcourue >= distanceTotale) {
            distanceParcourue = distanceTotale; // Plafonnement exigé 
            terminee = true;
            tempsArriveeTheorique = distanceTotale / voiture.getVitesse();
            return true;
        }
        return false;
    }

    public void reinitialiser() {
        this.distanceParcourue = 0.0;
        this.toursEffectues = 0;
        this.terminee = false;
        this.tempsArriveeTheorique = 0.0;
    }

    @Override
    public String toString() {
        return voiture.getNom() + " (" + voiture.getId() + ") : "
                + distanceParcourue + " km (" + toursEffectues + " tours)";
    }
}
