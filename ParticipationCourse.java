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
            return false;
        }

        distanceParcourue += voiture.getVitesse();
        toursEffectues = numeroTour;

        if (distanceParcourue >= distanceTotale) {
            distanceParcourue = distanceTotale; // Plafonnement exigé par F3
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
