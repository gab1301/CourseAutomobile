public class Voiture {
    private String id;
    private String nom;
    private String marque;
    private double vitesse;

    public Voiture(String id, String nom, String marque, double vitesse) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de la voiture ne peut pas être vide.");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la voiture ne peut pas être vide.");
        }
        if (marque == null || marque.trim().isEmpty()) {
            throw new IllegalArgumentException("La marque de la voiture ne peut pas être vide.");
        }
        if (vitesse <= 0) {
            throw new IllegalArgumentException("La vitesse doit être strictement supérieure à 0.");
        }
        this.id = id.trim();
        this.nom = nom.trim();
        this.marque = marque.trim();
        this.vitesse = vitesse;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getMarque() {
        return marque;
    }

    public double getVitesse() {
        return vitesse;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la voiture ne peut pas être vide.");
        }
        this.nom = nom.trim();
    }

    public void setMarque(String marque) {
        if (marque == null || marque.trim().isEmpty()) {
            throw new IllegalArgumentException("La marque de la voiture ne peut pas être vide.");
        }
        this.marque = marque.trim();
    }

    public void setVitesse(double vitesse) {
        if (vitesse <= 0) {
            throw new IllegalArgumentException("La vitesse doit être strictement supérieure à 0.");
        }
        this.vitesse = vitesse;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nom + " (" + marque + ") - " + vitesse + " km/tour";
    }
}
