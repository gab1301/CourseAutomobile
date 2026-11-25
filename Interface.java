import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Interface extends JFrame {
    private final GestionnaireCourses gestionnaire;

    private static final String PAGE_AJOUTER = "1. Ajouter une voiture";
    private static final String PAGE_LISTER = "2. Afficher / Rechercher les voitures";
    private static final String PAGE_MODIFIER = "3. Modifier une voiture";
    private static final String PAGE_SUPPRIMER = "4. Supprimer une voiture";
    private static final String PAGE_CREER_COURSE = "5. Créer une course";
    private static final String PAGE_SIMULER = "6. Démarrer / Simuler une course";
    private static final String PAGE_RESULTATS = "7. Résultats de la dernière course";

    private final JComboBox<String> menuDeroulant;
    private final CardLayout cardLayout;
    private final JPanel contenuPanel;

    private DefaultTableModel listeVoituresModel;
    private DefaultTableModel selectionCourseModel;
    private JTable selectionCourseTable;
    private JTextArea simulationArea;
    private JTextArea resultatsArea;

    public Interface(GestionnaireCourses gestionnaire) {
        super("Gestionnaire de Courses");
        this.gestionnaire = gestionnaire;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Barre supérieure avec menu déroulant ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        topBar.add(new JLabel("Action :"));

        menuDeroulant = new JComboBox<>(new String[]{
                PAGE_AJOUTER,
                PAGE_LISTER,
                PAGE_MODIFIER,
                PAGE_SUPPRIMER,
                PAGE_CREER_COURSE,
                PAGE_SIMULER,
                PAGE_RESULTATS
        });
        menuDeroulant.setPreferredSize(new Dimension(280, 30));
        topBar.add(menuDeroulant);

        add(topBar, BorderLayout.NORTH);

        // --- Zone centrale dynamique (CardLayout) ---
        cardLayout = new CardLayout();
        contenuPanel = new JPanel(cardLayout);
        contenuPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        contenuPanel.add(creerPageAjouterVoiture(), PAGE_AJOUTER);
        contenuPanel.add(creerPageListerVoitures(), PAGE_LISTER);
        contenuPanel.add(creerPageModifierVoiture(), PAGE_MODIFIER);
        contenuPanel.add(creerPageSupprimerVoiture(), PAGE_SUPPRIMER);
        contenuPanel.add(creerPageCreerCourse(), PAGE_CREER_COURSE);
        contenuPanel.add(creerPageSimulerCourse(), PAGE_SIMULER);
        contenuPanel.add(creerPageResultats(), PAGE_RESULTATS);

        add(contenuPanel, BorderLayout.CENTER);

        menuDeroulant.addActionListener(e -> {
            String selection = (String) menuDeroulant.getSelectedItem();
            if (selection != null) {
                rafraichirDonnees();
                cardLayout.show(contenuPanel, selection);
            }
        });

        cardLayout.show(contenuPanel, PAGE_AJOUTER);
    }

    // 1. Page Ajouter une voiture
    private JPanel creerPageAjouterVoiture() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = creerGbc();

        JTextField idField = new JTextField(18);
        JTextField nomField = new JTextField(18);
        JTextField marqueField = new JTextField(18);
        JTextField vitesseField = new JTextField(18);
        JButton btnAjouter = new JButton("Ajouter au garage");

        ajouterLigneFormulaire(panel, gbc, 0, "ID :", idField);
        ajouterLigneFormulaire(panel, gbc, 1, "Nom :", nomField);
        ajouterLigneFormulaire(panel, gbc, 2, "Marque :", marqueField);
        ajouterLigneFormulaire(panel, gbc, 3, "Vitesse (> 0) :", vitesseField);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(btnAjouter, gbc);

        btnAjouter.addActionListener(e -> {
            try {
                double vitesse = Double.parseDouble(vitesseField.getText().trim());
                gestionnaire.ajouterVoiture(idField.getText(), nomField.getText(), marqueField.getText(), vitesse);
                JOptionPane.showMessageDialog(this, "Voiture ajoutée avec succès !");
                idField.setText("");
                nomField.setText("");
                marqueField.setText("");
                vitesseField.setText("");
                rafraichirDonnees();
            } catch (Exception ex) {
                afficherErreur(ex.getMessage());
            }
        });

        return panel;
    }

    // 2. Page Afficher / Rechercher les voitures
    private JPanel creerPageListerVoitures() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel recherchePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField rechercheIdField = new JTextField(15);
        JButton btnRechercher = new JButton("Rechercher par ID");
        JButton btnToutAfficher = new JButton("Tout afficher");

        recherchePanel.add(new JLabel("ID :"));
        recherchePanel.add(rechercheIdField);
        recherchePanel.add(btnRechercher);
        recherchePanel.add(btnToutAfficher);

        listeVoituresModel = new DefaultTableModel(new String[]{"ID", "Nom", "Marque", "Vitesse"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(listeVoituresModel);

        btnRechercher.addActionListener(e -> {
            Voiture v = gestionnaire.rechercherVoiture(rechercheIdField.getText());
            listeVoituresModel.setRowCount(0);
            if (v != null) {
                listeVoituresModel.addRow(new Object[]{v.getId(), v.getNom(), v.getMarque(), v.getVitesse()});
            } else {
                afficherErreur("Aucune voiture trouvée avec cet ID.");
            }
        });

        btnToutAfficher.addActionListener(e -> {
            rechercheIdField.setText("");
            rafraichirDonnees();
        });

        panel.add(recherchePanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // 3. Page Modifier une voiture
    private JPanel creerPageModifierVoiture() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = creerGbc();

        JTextField idField = new JTextField(18);
        JTextField nomField = new JTextField(18);
        JTextField marqueField = new JTextField(18);
        JTextField vitesseField = new JTextField(18);
        JButton btnCharger = new JButton("Charger");
        JButton btnModifier = new JButton("Enregistrer les modifications");

        JPanel idLigne = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        idLigne.add(idField);
        idLigne.add(Box.createHorizontalStrut(6));
        idLigne.add(btnCharger);

        ajouterLigneFormulaire(panel, gbc, 0, "ID de la voiture :", idLigne);
        ajouterLigneFormulaire(panel, gbc, 1, "Nouveau nom :", nomField);
        ajouterLigneFormulaire(panel, gbc, 2, "Nouvelle marque :", marqueField);
        ajouterLigneFormulaire(panel, gbc, 3, "Nouvelle vitesse (> 0) :", vitesseField);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(btnModifier, gbc);

        btnCharger.addActionListener(e -> {
            Voiture v = gestionnaire.rechercherVoiture(idField.getText());
            if (v == null) {
                afficherErreur("Voiture introuvable.");
                return;
            }
            nomField.setText(v.getNom());
            marqueField.setText(v.getMarque());
            vitesseField.setText(String.valueOf(v.getVitesse()));
        });

        btnModifier.addActionListener(e -> {
            try {
                double vitesse = Double.parseDouble(vitesseField.getText().trim());
                gestionnaire.modifierVoiture(idField.getText(), nomField.getText(), marqueField.getText(), vitesse);
                JOptionPane.showMessageDialog(this, "Voiture modifiée avec succès !");
                rafraichirDonnees();
            } catch (Exception ex) {
                afficherErreur(ex.getMessage());
            }
        });

        return panel;
    }

    // 4. Page Supprimer une voiture
    private JPanel creerPageSupprimerVoiture() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = creerGbc();

        JTextField idField = new JTextField(18);
        JButton btnSupprimer = new JButton("Supprimer du garage");

        ajouterLigneFormulaire(panel, gbc, 0, "ID de la voiture à supprimer :", idField);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(btnSupprimer, gbc);

        btnSupprimer.addActionListener(e -> {
            try {
                boolean ok = gestionnaire.supprimerVoiture(idField.getText());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Voiture supprimée.");
                    idField.setText("");
                    rafraichirDonnees();
                } else {
                    afficherErreur("Voiture introuvable.");
                }
            } catch (Exception ex) {
                afficherErreur(ex.getMessage());
            }
        });

        return panel;
    }

    // 5. Page Créer une course
    private JPanel creerPageCreerCourse() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JTextField idCourseField = new JTextField(8);
        JTextField nomCourseField = new JTextField(12);
        JTextField distanceField = new JTextField(8);
        JButton btnCreer = new JButton("Créer la course");

        form.add(new JLabel("ID :"));
        form.add(idCourseField);
        form.add(new JLabel("Nom :"));
        form.add(nomCourseField);
        form.add(new JLabel("Distance (> 0) :"));
        form.add(distanceField);
        form.add(btnCreer);

        selectionCourseModel = new DefaultTableModel(new String[]{"ID", "Nom", "Marque", "Vitesse"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        selectionCourseTable = new JTable(selectionCourseModel);
        selectionCourseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        btnCreer.addActionListener(e -> {
            try {
                int[] rows = selectionCourseTable.getSelectedRows();
                if (rows.length < 2) {
                    afficherErreur("Sélectionnez au moins 2 voitures dans la liste (Ctrl + clic).");
                    return;
                }
                List<Voiture> selection = new ArrayList<>();
                for (int r : rows) {
                    String id = selectionCourseModel.getValueAt(r, 0).toString();
                    Voiture v = gestionnaire.rechercherVoiture(id);
                    if (v != null) {
                        selection.add(v);
                    }
                }
                double distance = Double.parseDouble(distanceField.getText().trim());
                Course c = gestionnaire.creerCourse(idCourseField.getText(), nomCourseField.getText(), distance, selection);
                JOptionPane.showMessageDialog(this, "Course '" + c.getNom() + "' créée ! Passez à l'étape simulation.");
            } catch (Exception ex) {
                afficherErreur(ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(selectionCourseTable), BorderLayout.CENTER);
        panel.add(new JLabel("Maintenez Ctrl (ou Cmd) pour sélectionner plusieurs voitures."), BorderLayout.SOUTH);
        return panel;
    }

    // 6. Page Démarrer / Simuler une course
    private JPanel creerPageSimulerCourse() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        JButton btnDemarrer = new JButton("1. Démarrer la course");
        JButton btnTour = new JButton("2. Simuler 1 tour");
        JButton btnAuto = new JButton("Simulation automatique complète");

        boutons.add(btnDemarrer);
        boutons.add(btnTour);
        boutons.add(btnAuto);

        simulationArea = new JTextArea();
        simulationArea.setEditable(false);
        simulationArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        btnDemarrer.addActionListener(e -> {
            try {
                Course c = gestionnaire.demarrerCourse();
                simulationArea.setText("Course démarrée : " + c.getNom() + "\n");
            } catch (Exception ex) {
                afficherErreur(ex.getMessage());
            }
        });

        btnTour.addActionListener(e -> {
            try {
                gestionnaire.simulerTour();
                Course derniere = gestionnaire.consulterResultatsDerniereCourse();
                if (derniere != null && derniere.getEtat() == EtatCourse.TERMINEE) {
                    simulationArea.append("\nCourse terminée !\n" + formaterClassement(derniere));
                } else {
                    simulationArea.append("Tour simulé.\n");
                }
            } catch (Exception ex) {
                afficherErreur(ex.getMessage());
            }
        });

        btnAuto.addActionListener(e -> {
            try {
                Course terminee = gestionnaire.lancerSimulationAutomatique();
                simulationArea.setText(formaterClassement(terminee));
            } catch (Exception ex) {
                afficherErreur(ex.getMessage());
            }
        });

        panel.add(boutons, BorderLayout.NORTH);
        panel.add(new JScrollPane(simulationArea), BorderLayout.CENTER);
        return panel;
    }

    // 7. Page Résultats
    private JPanel creerPageResultats() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        resultatsArea = new JTextArea();
        resultatsArea.setEditable(false);
        resultatsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        panel.add(new JScrollPane(resultatsArea), BorderLayout.CENTER);
        return panel;
    }

    private void rafraichirDonnees() {
        List<Voiture> voitures = gestionnaire.listerVoitures();
        if (listeVoituresModel != null) {
            listeVoituresModel.setRowCount(0);
            for (Voiture v : voitures) {
                listeVoituresModel.addRow(new Object[]{v.getId(), v.getNom(), v.getMarque(), v.getVitesse()});
            }
        }
        if (selectionCourseModel != null) {
            selectionCourseModel.setRowCount(0);
            for (Voiture v : voitures) {
                selectionCourseModel.addRow(new Object[]{v.getId(), v.getNom(), v.getMarque(), v.getVitesse()});
            }
        }
        if (resultatsArea != null) {
            Course derniere = gestionnaire.consulterResultatsDerniereCourse();
            resultatsArea.setText(derniere == null
                    ? "Aucune course terminée pour le moment."
                    : formaterClassement(derniere));
        }
    }

    private String formaterClassement(Course course) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Résultats : ").append(course.getNom())
          .append(" (Tours total : ").append(course.getNombreToursTotal()).append(") ===\n\n");

        List<ParticipationCourse> classement = course.getClassement();
        for (int i = 0; i < classement.size(); i++) {
            ParticipationCourse p = classement.get(i);
            sb.append(String.format("%d. %s [%s] - Tours: %d - Temps théorique: %.2f%n",
                    i + 1,
                    p.getVoiture().getNom(),
                    p.getVoiture().getId(),
                    p.getToursEffectues(),
                    p.getTempsArriveeTheorique()));
        }
        ParticipationCourse gagnant = course.getGagnant();
        if (gagnant != null) {
            sb.append("\nGagnant : ").append(gagnant.getVoiture().getNom());
        }
        return sb.toString();
    }

    private GridBagConstraints creerGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void ajouterLigneFormulaire(JPanel panel, GridBagConstraints gbc, int ligne, String label, Component champ) {
        gbc.gridx = 0;
        gbc.gridy = ligne;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(champ, gbc);
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interface(new GestionnaireCourses()).setVisible(true));
    }
}