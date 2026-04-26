import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Vue du menu principal.
 * Cette classe gère uniquement l'affichage et délègue la logique métier au contrôleur de menu.
 */
public class MenuPrincipal {
    private static final double LARGEUR = 1000;
    private static final double HAUTEUR = 700;
    private static final Color FOND = Color.web("#17352d");

    /**
     * Représente la zone cliquable d'un bouton affiché sur le canvas.
     */
    private static final class Bouton {
        private double x;
        private double y;
        private double largeur;
        private double hauteur;

        private boolean contient(double px, double py) {
            return px >= x && px <= x + largeur && py >= y && py <= y + hauteur;
        }
    }

    private final Consumer<Scene> actionJouer;
    private final ControleurMenu controleurMenu;
    private final Canvas canvas;
    private final Scene scene;
    private final Bouton[] boutonsMenu;
    private final Bouton boutonRetourSecondaire = new Bouton();
    private final Image imageFond;

    /**
     * Crée le menu principal.
     *
     * @param actionJouer action appelée quand l'utilisateur choisit "Jouer"
     */
    public MenuPrincipal(Consumer<Scene> actionJouer) {
        this.actionJouer = actionJouer;
        this.controleurMenu = new ControleurMenu();
        this.boutonsMenu = new Bouton[controleurMenu.getNombreOptions()];

        Image img = null;
        try {
            var url = MenuPrincipal.class.getResource("/fond_principale_ecran-frame0.png");
            if (url != null) {
                img = new Image(url.toExternalForm());
            }
        } catch (Exception ignored) {}
        this.imageFond = img;
        for (int i = 0; i < boutonsMenu.length; i++) {
            boutonsMenu[i] = new Bouton();
        }

        this.canvas = new Canvas(LARGEUR, HAUTEUR);
        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #17352d;");
        this.scene = new Scene(racine, LARGEUR, HAUTEUR);
        this.scene.setFill(FOND);

        this.scene.widthProperty().addListener((obs, oldVal, newVal) -> redessiner());
        this.scene.heightProperty().addListener((obs, oldVal, newVal) -> redessiner());
        this.scene.setOnKeyPressed(e -> gererClavier(e.getCode()));
        this.canvas.setOnMouseClicked(e -> gererClic(e.getX(), e.getY()));
        this.canvas.setFocusTraversable(true);

        redessiner();
    }

    public Scene getScene() {
        return scene;
    }

    /**
     * Redonne le focus clavier au menu.
     */
    public void reprendreFocus() {
        scene.getRoot().requestFocus();
        canvas.requestFocus();
    }

    private void redessiner() {
        double largeur = scene.getWidth();
        double hauteur = scene.getHeight();
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(FOND);
        gc.fillRect(0, 0, largeur, hauteur);

        if (imageFond != null && imageFond.isBackgroundLoading() == false && !imageFond.isError()) {
            gc.drawImage(imageFond, 0, 0, largeur, hauteur);
        } else {
            gc.setFill(Color.web("#22463b", 0.70));
            gc.fillOval(-largeur * 0.08, -hauteur * 0.10, largeur * 0.34, hauteur * 0.42);
            gc.setFill(Color.web("#2f6a58", 0.30));
            gc.fillOval(largeur * 0.72, hauteur * 0.58, largeur * 0.26, hauteur * 0.28);
            gc.setFill(Color.web("#f2d974", 0.10));
            gc.fillOval(largeur * 0.78, hauteur * 0.10, largeur * 0.12, hauteur * 0.17);
        }

        double panneauLargeur = largeur * 0.72;
        double panneauHauteur = controleurMenu.estSurMenuPrincipal() ? hauteur * 0.72 : hauteur * 0.62;
        double panneauX = (largeur - panneauLargeur) / 2.0;
        double panneauY = (hauteur - panneauHauteur) / 2.0;

        gc.setFill(Color.web("#102820", 0.35));
        gc.fillRoundRect(panneauX + 10, panneauY + 14, panneauLargeur, panneauHauteur, 28, 28);

        gc.setFill(Color.web("#23483d"));
        gc.fillRoundRect(panneauX, panneauY, panneauLargeur, panneauHauteur, 24, 24);
        gc.setStroke(Color.web("#d7ece4", 0.35));
        gc.setLineWidth(2);
        gc.strokeRoundRect(panneauX, panneauY, panneauLargeur, panneauHauteur, 24, 24);

        gc.setFill(Color.web("#2f5f50", 0.75));
        gc.fillRoundRect(panneauX + panneauLargeur * 0.04, panneauY + panneauHauteur * 0.08, panneauLargeur * 0.92, panneauHauteur * 0.10, 18, 18);

        gc.setFill(Color.web("#e8f3ee"));
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, Math.max(28, largeur * 0.05)));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("WHISPIN PARABOX", panneauX + panneauLargeur / 2.0, panneauY + panneauHauteur * 0.15);
        gc.setTextAlign(TextAlignment.LEFT);

        if (controleurMenu.estSurMenuPrincipal()) {
            dessinerMenu(gc, panneauX, panneauY, panneauLargeur, panneauHauteur);
            return;
        }

        dessinerEcranSecondaire(gc, panneauX, panneauY, panneauLargeur, panneauHauteur);
    }

    private void dessinerMenu(GraphicsContext gc, double panneauX, double panneauY, double panneauLargeur, double panneauHauteur) {
        double zoneBoutonsX = panneauX + panneauLargeur * 0.18;
        double zoneBoutonsLargeur = panneauLargeur * 0.64;
        double zoneSuperieure = panneauY + panneauHauteur * 0.34;
        double zoneInferieure = panneauY + panneauHauteur * 0.88;
        double espaceDisponible = zoneInferieure - zoneSuperieure;
        double espacement = Math.max(12, panneauHauteur * 0.028);
        double boutonHauteur = (espaceDisponible - espacement * (boutonsMenu.length - 1)) / boutonsMenu.length;
        boutonHauteur = Math.max(36, Math.min(boutonHauteur, panneauHauteur * 0.11));
        double hauteurTotale = boutonsMenu.length * boutonHauteur + (boutonsMenu.length - 1) * espacement;
        double premierY = zoneSuperieure + (espaceDisponible - hauteurTotale) / 2.0;

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.web("#d7ece4"));
        gc.setFont(Font.font("SansSerif", FontWeight.NORMAL, Math.max(15, panneauLargeur * 0.022)));
        gc.fillText(
            "Deplace le cube joueur et range tous les cubes cibles.",
            panneauX + panneauLargeur / 2.0,
            panneauY + panneauHauteur * 0.25
        );

        gc.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, Math.max(16, panneauLargeur * 0.03)));

        for (int i = 0; i < boutonsMenu.length; i++) {
            Bouton bouton = boutonsMenu[i];
            bouton.x = zoneBoutonsX;
            bouton.y = premierY + i * (boutonHauteur + espacement);
            bouton.largeur = zoneBoutonsLargeur;
            bouton.hauteur = boutonHauteur;

            boolean selectionne = i == controleurMenu.getIndexSelectionne();
            gc.setFill(Color.web("#102820", 0.35));
            gc.fillRoundRect(bouton.x + 6, bouton.y + 6, bouton.largeur, bouton.hauteur, 16, 16);

            gc.setFill(selectionne ? Color.web("#f2d974") : Color.web("#2e5b4e"));
            gc.fillRoundRect(bouton.x, bouton.y, bouton.largeur, bouton.hauteur, 16, 16);
            gc.setStroke(selectionne ? Color.web("#fff6c7") : Color.web("#d7ece4", 0.65));
            gc.setLineWidth(selectionne ? 2.5 : 2);
            gc.strokeRoundRect(bouton.x, bouton.y, bouton.largeur, bouton.hauteur, 16, 16);

            if (selectionne) {
                gc.setFill(Color.web("#fff4b0", 0.45));
                gc.fillRoundRect(bouton.x + 6, bouton.y + 4, bouton.largeur - 12, bouton.hauteur * 0.26, 12, 12);
            }

            gc.setFill(selectionne ? Color.web("#2d241c") : Color.web("#e8f3ee"));
            gc.fillText(controleurMenu.getTexteOption(i), bouton.x + bouton.largeur / 2.0, bouton.y + bouton.hauteur * 0.62);
        }

        gc.setFill(Color.web("#bdd9cf"));
        gc.setFont(Font.font("SansSerif", Math.max(12, panneauLargeur * 0.016)));
        gc.fillText(
            "Navigation : ZQSD ou fleches  •  Entree pour valider  •  Echap pour quitter",
            panneauX + panneauLargeur / 2.0,
            panneauY + panneauHauteur * 0.94
        );

        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void dessinerEcranSecondaire(GraphicsContext gc, double panneauX, double panneauY, double panneauLargeur, double panneauHauteur) {
        double margeX = panneauLargeur * 0.09;
        double centreX = panneauX + panneauLargeur / 2.0;
        double titreY = panneauY + panneauHauteur * 0.20;
        double carteX = panneauX + panneauLargeur * 0.07;
        double carteY = panneauY + panneauHauteur * 0.27;
        double carteLargeur = panneauLargeur * 0.86;
        double carteHauteur = panneauHauteur * 0.44;

        gc.setFill(Color.web("#2c5b4d"));
        gc.fillRoundRect(carteX, carteY, carteLargeur, carteHauteur, 22, 22);
        gc.setStroke(Color.web("#d7ece4", 0.28));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(carteX, carteY, carteLargeur, carteHauteur, 22, 22);

        gc.setFill(Color.web("#e8f3ee"));
        gc.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, Math.max(18, panneauLargeur * 0.035)));

        String titre = controleurMenu.getTitreEcranSecondaire();
        String[] lignes = controleurMenu.getLignesEcranSecondaire();

        boutonRetourSecondaire.largeur = panneauLargeur * 0.28;
        boutonRetourSecondaire.hauteur = Math.max(42, panneauHauteur * 0.12);
        boutonRetourSecondaire.x = panneauX + (panneauLargeur - boutonRetourSecondaire.largeur) / 2.0;
        boutonRetourSecondaire.y = panneauY + panneauHauteur * 0.80;

        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(titre, centreX, titreY);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("SansSerif", Math.max(14, panneauLargeur * 0.022)));
        double texteY = carteY + carteHauteur * 0.22;
        double lineHeight = Math.max(24, panneauHauteur * 0.070);
        double limiteTexteY = carteY + carteHauteur * 0.88;
        for (int i = 0; i < lignes.length; i++) {
            double y = texteY + i * lineHeight;
            if (y > limiteTexteY) {
                break;
            }
            gc.fillText(lignes[i], carteX + margeX * 0.55, y);
        }

        gc.setFill(Color.web("#102820", 0.35));
        gc.fillRoundRect(boutonRetourSecondaire.x + 6, boutonRetourSecondaire.y + 6, boutonRetourSecondaire.largeur, boutonRetourSecondaire.hauteur, 16, 16);
        gc.setFill(Color.web("#2e5b4e"));
        gc.fillRoundRect(boutonRetourSecondaire.x, boutonRetourSecondaire.y, boutonRetourSecondaire.largeur, boutonRetourSecondaire.hauteur, 16, 16);
        gc.setStroke(Color.web("#d7ece4"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(boutonRetourSecondaire.x, boutonRetourSecondaire.y, boutonRetourSecondaire.largeur, boutonRetourSecondaire.hauteur, 16, 16);

        gc.setFill(Color.web("#6f9f91"));
        gc.setFont(Font.font("SansSerif", Math.max(12, panneauLargeur * 0.016)));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Entree, Espace, Q ou Fleche gauche pour revenir", centreX, panneauY + panneauHauteur * 0.74);

        gc.setFill(Color.web("#e8f3ee"));
        gc.setFont(Font.font("SansSerif", FontWeight.SEMI_BOLD, Math.max(15, panneauLargeur * 0.03)));
        gc.fillText("Retour", boutonRetourSecondaire.x + boutonRetourSecondaire.largeur / 2.0, boutonRetourSecondaire.y + boutonRetourSecondaire.hauteur * 0.62);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void gererClic(double x, double y) {
        if (controleurMenu.estSurMenuPrincipal()) {
            for (int i = 0; i < boutonsMenu.length; i++) {
                Bouton bouton = boutonsMenu[i];
                if (!bouton.contient(x, y)) {
                    continue;
                }

                appliquerAction(controleurMenu.gererSelectionSouris(i));
                return;
            }
            return;
        }

        if (boutonRetourSecondaire.contient(x, y)) {
            appliquerAction(controleurMenu.gererRetourSecondaire());
        }
    }

    private void gererClavier(KeyCode code) {
        appliquerAction(controleurMenu.gererTouche(code));
    }

    private void appliquerAction(ControleurMenu.Action action) {
        switch (action) {
            case AUCUNE -> {
            }
            case REDESSINER -> redessiner();
            case JOUER -> actionJouer.accept(scene);
            case QUITTER -> Platform.exit();
        }
    }
}