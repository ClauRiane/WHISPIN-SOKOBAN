import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DeuxiemeScene {
    private static final Color FOND = Color.web("#ece8dc");
    private static final int MARGE = 20;
    private static final int ESPACE_TEXTE = 40;
    private static final double FREQUENCE_PULSATION_CIBLE = 4.5;
    private static final double FREQUENCE_PULSATION_CIBLE_VICTOIRE = 8.0;
    private static final double FREQUENCE_OSCILLATION_REPOS = 5.5;
    private static final double FREQUENCE_OSCILLATION_BLOQUE = 22.0;
    private static final double FREQUENCE_OSCILLATION_VICTOIRE = 11.0;
    private static final double FREQUENCE_CLIGNEMENT = 7.0;
    private static final double FREQUENCE_REGARD = 3.8;
    private static final double FREQUENCE_HALO = 8.0;
    private static final double DUREE_FEU_ARTIFICE_SECONDES = 4.0;
    private static final double DUREE_EXPLOSION_SECONDES = 1.1;
    private static final long INTERVALLE_EXPLOSION_NS = 190_000_000L;
    private static final int NOMBRE_ETINCELLES = 18;

    private static final class ExplosionFeuArtifice {
        private final double centreX;
        private final double centreY;
        private final double rayonMax;
        private final Color couleur;
        private final long debutNs;

        private ExplosionFeuArtifice(double centreX, double centreY, double rayonMax, Color couleur, long debutNs) {
            this.centreX = centreX;
            this.centreY = centreY;
            this.rayonMax = rayonMax;
            this.couleur = couleur;
            this.debutNs = debutNs;
        }
    }

    public static Scene creerScene(Stage stage, Scene scenePrecedente, Plateau plateau) {
        Canvas canvas = new Canvas(900, 700);
        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #ece8dc;");
        Scene scene = new Scene(racine, 900, 700);
        scene.setFill(FOND);

        ControleurAnimation controleurAnimation = new ControleurAnimation();
        ArrayList<ExplosionFeuArtifice> explosions = new ArrayList<>();
        long[] debutVictoireNs = {0L};
        long[] dernierLancementExplosionNs = {0L};
        boolean[] fermetureDemandee = {false};

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long maintenantNs) {
                controleurAnimation.initialiserSiNecessaire(maintenantNs);
                controleurAnimation.mettreAJour(plateau.estGagne(), maintenantNs);

                if (plateau.estGagne()) {
                    if (debutVictoireNs[0] == 0L) {
                        debutVictoireNs[0] = maintenantNs;
                        dernierLancementExplosionNs[0] = maintenantNs - INTERVALLE_EXPLOSION_NS;
                    }

                    if (maintenantNs - dernierLancementExplosionNs[0] >= INTERVALLE_EXPLOSION_NS) {
                        ajouterExplosionFeuArtifice(explosions, scene.getWidth(), scene.getHeight(), maintenantNs);
                        dernierLancementExplosionNs[0] = maintenantNs;
                    }

                    nettoyerExplosionsExpirees(explosions, maintenantNs);

                    double tempsVictoire = (maintenantNs - debutVictoireNs[0]) / 1_000_000_000.0;
                    if (tempsVictoire >= DUREE_FEU_ARTIFICE_SECONDES && !fermetureDemandee[0]) {
                        fermetureDemandee[0] = true;
                        stop();
                        Platform.exit();
                        return;
                    }
                }

                redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, explosions, maintenantNs);
            }
        };
        timer.start();

        scene.widthProperty().addListener((obs, oldVal, newVal) -> redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, explosions, System.nanoTime()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, explosions, System.nanoTime()));
        redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, explosions, System.nanoTime());

        scene.setOnKeyPressed(ev -> {
            if (plateau.estGagne()) {
                return;
            }

            boolean changement = false;
            KeyCode touche = ev.getCode();
            Direction directionTentative = null;
            boolean poussaitUneBoite = false;
            long maintenantNs = System.nanoTime();

            if (ev.isControlDown() && touche == KeyCode.Z) {
                changement = plateau.annulerDernierMouvement();
                if (changement) {
                    controleurAnimation.notifierAnnulation(plateau.estGagne(), maintenantNs);
                }
            } else {
                switch (touche) {
                    case UP:
                    case Z:
                    case W:
                        directionTentative = Direction.HAUT;
                        poussaitUneBoite = tentativePoussee(plateau, directionTentative);
                        changement = plateau.deplacer(Direction.HAUT);
                        break;
                    case DOWN:
                    case S:
                        directionTentative = Direction.BAS;
                        poussaitUneBoite = tentativePoussee(plateau, directionTentative);
                        changement = plateau.deplacer(Direction.BAS);
                        break;
                    case LEFT:
                    case Q:
                    case A:
                        directionTentative = Direction.GAUCHE;
                        poussaitUneBoite = tentativePoussee(plateau, directionTentative);
                        changement = plateau.deplacer(Direction.GAUCHE);
                        break;
                    case RIGHT:
                    case D:
                        directionTentative = Direction.DROITE;
                        poussaitUneBoite = tentativePoussee(plateau, directionTentative);
                        changement = plateau.deplacer(Direction.DROITE);
                        break;
                    default:
                        break;
                }
            }

            if (directionTentative != null) {
                if (changement) {
                    controleurAnimation.notifierDeplacementReussi(directionTentative, poussaitUneBoite, plateau.estGagne(), maintenantNs);
                } else {
                    controleurAnimation.notifierDeplacementBloque(directionTentative, maintenantNs);
                }
            }

            redessiner(canvas, scene.getWidth(), scene.getHeight(), plateau, controleurAnimation, explosions, maintenantNs);
        });

        canvas.setOnMouseClicked(ev -> {
            // retour a l'accueil desactive pour cette branche
        });
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        return scene;
    }

    private static boolean tentativePoussee(Plateau plateau, Direction direction) {
        if (!plateau.peutSeDeplacer(direction)) {
            return false;
        }
        Position prochainePos = plateau.getPositionPersonnage().deplacer(direction);
        if (!plateau.estDansLimites(prochainePos)) {
            return false;
        }
        return plateau.getCase(prochainePos).estBoite();
    }

    private static void redessiner(
        Canvas canvas,
        double largeur,
        double hauteur,
        Plateau plateau,
        ControleurAnimation controleurAnimation,
        ArrayList<ExplosionFeuArtifice> explosions,
        long maintenantNs
    ) {
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));
        dessinerPlateau(canvas.getGraphicsContext2D(), plateau, controleurAnimation, explosions, maintenantNs);
    }

    private static void dessinerPlateau(
        GraphicsContext gc,
        Plateau plateau,
        ControleurAnimation controleurAnimation,
        ArrayList<ExplosionFeuArtifice> explosions,
        long maintenantNs
    ) {
        double largeur = gc.getCanvas().getWidth();
        double hauteur = gc.getCanvas().getHeight();

        int nombreLignes = plateau.getGrille().size();
        int nombreColonnes = plateau.getGrille().isEmpty() ? 0 : plateau.getGrille().get(0).size();

        double largeurDisponible = Math.max(largeur - 2 * MARGE, 1);
        double hauteurDisponible = Math.max(hauteur - 2 * MARGE - ESPACE_TEXTE, 1);
        double tailleCase = Math.min(
            largeurDisponible / Math.max(nombreColonnes, 1),
            hauteurDisponible / Math.max(nombreLignes, 1)
        );

        double largeurPlateau = nombreColonnes * tailleCase;
        double hauteurPlateau = nombreLignes * tailleCase;
        double origineX = (largeur - largeurPlateau) / 2.0;
        double origineY = ESPACE_TEXTE + MARGE + (hauteurDisponible - hauteurPlateau) / 2.0;

        gc.setFill(FOND);
        gc.fillRect(0, 0, largeur, hauteur);

        gc.setFill(Color.web("#473728"));
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, Math.max(16, largeur * 0.02)));
        String message = plateau.estGagne()
            ? "Niveau termine ! Feu d'artifice en cours..."
            : "ZQSD pour bouger, Ctrl+Z pour annuler";
        gc.fillText(message, MARGE, ESPACE_TEXTE - 12);

        for (int i = 0; i < nombreLignes; i++) {
            for (int j = 0; j < nombreColonnes; j++) {
                double x = origineX + j * tailleCase;
                double y = origineY + i * tailleCase;
                Case element = plateau.getGrille().get(i).get(j);

                gc.setFill(couleurSol(element));
                gc.fillRect(x, y, tailleCase - 1, tailleCase - 1);
                gc.setStroke(Color.web("#2d241c"));
                gc.strokeRect(x, y, tailleCase - 1, tailleCase - 1);

                if (element instanceof CaseMur) {
                    dessinerMur(gc, x + 1, y + 1, tailleCase - 2);
                    continue;
                }

                dessinerTextureSol(gc, x + 1, y + 1, tailleCase - 2);

                boolean cibleVisible = element instanceof CaseCible
                    || (element instanceof CaseBoite && ((CaseBoite) element).estSurCible())
                    || (element instanceof Personnage && ((Personnage) element).estSurCible());
                if (cibleVisible) {
                    dessinerCible(gc, x + 1, y + 1, tailleCase - 2, maintenantNs, plateau.estGagne());
                }

                if (element instanceof CaseBoite) {
                    CaseBoite boite = (CaseBoite) element;
                    dessinerBoite(gc, x + 1, y + 1, tailleCase - 2, boite.estSurCible(), controleurAnimation, maintenantNs);
                    continue;
                }

                if (element instanceof CaseCible) {
                    continue;
                }

                if (element instanceof CaseVide) {
                    continue;
                }

                if (element instanceof Personnage) {
                    Personnage personnage = (Personnage) element;
                    dessinerPersonnage(gc, x + 1, y + 1, tailleCase - 2, personnage.estSurCible(), controleurAnimation, maintenantNs);
                    continue;
                }

                char symbole = element.getSymbole();
                if (symbole != ' ') {
                    gc.setFill(Color.web("#1f1f1f"));
                    gc.setFont(Font.font("Monospaced", FontWeight.BOLD, Math.max(12, tailleCase * 0.45)));
                    gc.fillText(String.valueOf(symbole), x + tailleCase * 0.32, y + tailleCase * 0.68);
                }
            }
        }

        if (plateau.estGagne()) {
            dessinerExplosionsFeuArtifice(gc, explosions, maintenantNs);
        }
    }

    private static void ajouterExplosionFeuArtifice(
        ArrayList<ExplosionFeuArtifice> explosions,
        double largeurScene,
        double hauteurScene,
        long maintenantNs
    ) {
        double largeur = Math.max(largeurScene, 1.0);
        double hauteur = Math.max(hauteurScene, 1.0);
        double centreX = largeur * (0.15 + Math.random() * 0.70);
        double centreY = hauteur * (0.12 + Math.random() * 0.45);
        double rayonMax = Math.min(largeur, hauteur) * (0.06 + Math.random() * 0.07);

        Color[] palette = new Color[] {
            Color.web("#ff595e"),
            Color.web("#ffca3a"),
            Color.web("#8ac926"),
            Color.web("#1982c4"),
            Color.web("#f15bb5")
        };
        int indexCouleur = (int) (Math.random() * palette.length);
        explosions.add(new ExplosionFeuArtifice(centreX, centreY, rayonMax, palette[indexCouleur], maintenantNs));
    }

    private static void nettoyerExplosionsExpirees(ArrayList<ExplosionFeuArtifice> explosions, long maintenantNs) {
        explosions.removeIf(explosion -> progressionExplosion(explosion, maintenantNs) >= 1.0);
    }

    private static void dessinerExplosionsFeuArtifice(
        GraphicsContext gc,
        ArrayList<ExplosionFeuArtifice> explosions,
        long maintenantNs
    ) {
        for (ExplosionFeuArtifice explosion : explosions) {
            double progression = progressionExplosion(explosion, maintenantNs);
            if (progression >= 1.0) {
                continue;
            }

            double opacite = Math.max(0.0, 1.0 - progression);
            double rayon = explosion.rayonMax * progression;

            gc.setLineWidth(Math.max(1.0, rayon * 0.045));
            for (int i = 0; i < NOMBRE_ETINCELLES; i++) {
                double angle = (Math.PI * 2.0 * i) / NOMBRE_ETINCELLES;
                double x1 = explosion.centreX + Math.cos(angle) * (rayon * 0.35);
                double y1 = explosion.centreY + Math.sin(angle) * (rayon * 0.35);
                double x2 = explosion.centreX + Math.cos(angle) * rayon;
                double y2 = explosion.centreY + Math.sin(angle) * rayon;

                gc.setStroke(
                    Color.color(
                        explosion.couleur.getRed(),
                        explosion.couleur.getGreen(),
                        explosion.couleur.getBlue(),
                        opacite
                    )
                );
                gc.strokeLine(x1, y1, x2, y2);
            }

            double rayonNoyau = Math.max(2.0, rayon * 0.08);
            gc.setFill(Color.color(1.0, 1.0, 1.0, opacite * 0.9));
            gc.fillOval(
                explosion.centreX - rayonNoyau,
                explosion.centreY - rayonNoyau,
                rayonNoyau * 2,
                rayonNoyau * 2
            );
        }
    }

    private static double progressionExplosion(ExplosionFeuArtifice explosion, long maintenantNs) {
        return Math.max(0.0, (maintenantNs - explosion.debutNs) / (DUREE_EXPLOSION_SECONDES * 1_000_000_000.0));
    }

    private static void dessinerMur(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#5c6770"));
        gc.fillRoundRect(x, y, taille, taille, 8, 8);

        double hauteurBrique = Math.max(4, taille * 0.24);
        gc.setStroke(Color.web("#3d4650"));
        gc.setLineWidth(Math.max(1.0, taille * 0.02));
        for (double offsetY = hauteurBrique; offsetY < taille; offsetY += hauteurBrique) {
            gc.strokeLine(x, y + offsetY, x + taille, y + offsetY);
        }

        double largeurBrique = Math.max(8, taille * 0.35);
        boolean decale = false;
        for (double offsetY = 0; offsetY < taille; offsetY += hauteurBrique) {
            double depart = decale ? largeurBrique * 0.5 : 0;
            for (double offsetX = depart; offsetX < taille; offsetX += largeurBrique) {
                gc.strokeLine(x + offsetX, y + offsetY, x + offsetX, y + Math.min(offsetY + hauteurBrique, taille));
            }
            decale = !decale;
        }
    }

    private static void dessinerTextureSol(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#f2dfb4"));
        gc.fillRect(x, y, taille, taille);
        gc.setFill(Color.web("#d6be8b"));
        double pas = Math.max(6, taille * 0.22);
        for (double offsetY = pas * 0.5; offsetY < taille; offsetY += pas) {
            for (double offsetX = pas * 0.5; offsetX < taille; offsetX += pas) {
                gc.fillOval(x + offsetX - 1.5, y + offsetY - 1.5, 3, 3);
            }
        }
    }

    private static void dessinerCible(GraphicsContext gc, double x, double y, double taille, long maintenantNs, boolean gagne) {
        double temps = maintenantNs / 1_000_000_000.0;
        double pulsation = 0.12 * (Math.sin(temps * (gagne ? FREQUENCE_PULSATION_CIBLE_VICTOIRE : FREQUENCE_PULSATION_CIBLE)) + 1.0);
        double rayonExterieur = taille * (0.42 + pulsation * 0.06);
        double cx = x + taille / 2.0;
        double cy = y + taille / 2.0;

        gc.setFill(Color.web("#fff1c2"));
        gc.fillOval(cx - rayonExterieur, cy - rayonExterieur, rayonExterieur * 2, rayonExterieur * 2);
        gc.setStroke(Color.web("#b08b2d"));
        gc.setLineWidth(Math.max(1.5, taille * 0.04));
        gc.strokeOval(cx - rayonExterieur, cy - rayonExterieur, rayonExterieur * 2, rayonExterieur * 2);

        double rayonInterieur = rayonExterieur * 0.55;
        gc.setStroke(Color.web("#7f6a28"));
        gc.strokeOval(cx - rayonInterieur, cy - rayonInterieur, rayonInterieur * 2, rayonInterieur * 2);
    }

    private static void dessinerBoite(
        GraphicsContext gc,
        double x,
        double y,
        double taille,
        boolean surCible,
        ControleurAnimation controleurAnimation,
        long maintenantNs
    ) {
        double rebond = 0.0;
        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.POUSSEE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_POUSSEE_SECONDES, 1.0);
            rebond = Math.sin(Math.PI * phase) * taille * 0.06;
        }

        double bx = x + taille * 0.12;
        double by = y + taille * 0.12 - rebond;
        double bs = taille * 0.76;

        gc.setFill(surCible ? Color.web("#df9b2d") : Color.web("#a86a2a"));
        gc.fillRoundRect(bx, by, bs, bs, 8, 8);
        gc.setStroke(surCible ? Color.web("#7e5415") : Color.web("#6f4317"));
        gc.setLineWidth(Math.max(1.5, taille * 0.04));
        gc.strokeRoundRect(bx, by, bs, bs, 8, 8);

        gc.strokeLine(bx + bs * 0.5, by + bs * 0.15, bx + bs * 0.5, by + bs * 0.85);
        gc.strokeLine(bx + bs * 0.15, by + bs * 0.5, bx + bs * 0.85, by + bs * 0.5);
    }
    caca

    private static void dessinerPersonnage(
        GraphicsContext gc,
        double x,
        double y,
        double taille,
        boolean surCible,
        ControleurAnimation controleurAnimation,
        long maintenantNs
    ) {
        double temps = maintenantNs / 1_000_000_000.0;
        double oscillationGlobale = Math.sin(temps * FREQUENCE_OSCILLATION_REPOS) * taille * 0.01;

        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.MARCHE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_MARCHE_SECONDES, 1.0);
            oscillationGlobale += Math.sin(Math.PI * phase) * taille * 0.05;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.POUSSEE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_POUSSEE_SECONDES, 1.0);
            oscillationGlobale += Math.sin(Math.PI * phase) * taille * 0.03;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.BLOQUE) {
            double phase = Math.min(controleurAnimation.dureeEcouleeEnSecondes(maintenantNs) / ControleurAnimation.DUREE_BLOCAGE_SECONDES, 1.0);
            oscillationGlobale += Math.sin(phase * FREQUENCE_OSCILLATION_BLOQUE) * taille * 0.02;
        } else if (controleurAnimation.getEtat() == ControleurAnimation.Etat.VICTOIRE) {
            oscillationGlobale += Math.sin(temps * FREQUENCE_OSCILLATION_VICTOIRE) * taille * 0.03;
        }

        double cx = x + taille * 0.5;
        double cy = y + taille * 0.56 - oscillationGlobale;
        double rayonCorps = taille * 0.31;
        double rayonTete = taille * 0.21;

        gc.setFill(surCible ? Color.web("#2f9aab") : Color.web("#168ca3"));
        gc.fillOval(cx - rayonCorps, cy - rayonCorps * 0.7, rayonCorps * 2, rayonCorps * 1.65);

        gc.setFill(Color.web("#f4d5b7"));
        double teteY = cy - taille * 0.2;
        gc.fillOval(cx - rayonTete, teteY - rayonTete, rayonTete * 2, rayonTete * 2);

        double regardX = 0.0;
        double regardY = 0.0;
        switch (controleurAnimation.getDirectionRegard()) {
            case GAUCHE:
                regardX = -taille * 0.03;
                break;
            case DROITE:
                regardX = taille * 0.03;
                break;
            case HAUT:
                regardY = -taille * 0.025;
                break;
            case BAS:
            default:
                regardY = taille * 0.015;
                break;
        }

        double clignement = Math.sin(temps * FREQUENCE_CLIGNEMENT);
        double ouverture = clignement > 0.95 ? 0.15 : 1.0;
        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.BLOQUE) {
            ouverture = 0.2;
        }

        double oeilAmplitude = Math.sin(temps * FREQUENCE_REGARD) * taille * 0.01;
        double oeilY = teteY - taille * 0.03 + regardY;
        double ecart = taille * 0.07;
        double rayonX = taille * 0.026;
        double rayonY = rayonX * ouverture;

        gc.setFill(Color.web("#1f1f1f"));
        gc.fillOval(cx - ecart + regardX + oeilAmplitude - rayonX, oeilY - rayonY, rayonX * 2, Math.max(1.5, rayonY * 2));
        gc.fillOval(cx + ecart + regardX + oeilAmplitude - rayonX, oeilY - rayonY, rayonX * 2, Math.max(1.5, rayonY * 2));

        if (controleurAnimation.getEtat() == ControleurAnimation.Etat.VICTOIRE) {
            gc.setStroke(Color.web("#f7c548"));
            gc.setLineWidth(Math.max(1.5, taille * 0.03));
            double halo = taille * (0.47 + 0.05 * Math.sin(temps * FREQUENCE_HALO));
            gc.strokeOval(cx - halo, cy - halo, halo * 2, halo * 2);
        }
    }

    private static Color couleurSol(Case element) {
        if (element instanceof CaseMur) {
            return Color.web("#85929a");
        }
        if (element instanceof CaseVide) {
            return Color.web("#e9d8a6");
        }
        if (element instanceof CaseCible) {
            return Color.web("#e8ddb7");
        }
        if (element instanceof CaseBoite boite) {
            return boite.estSurCible() ? Color.web("#e8ddb7") : Color.web("#e9d8a6");
        }
        if (element instanceof Personnage joueur) {
            return joueur.estSurCible() ? Color.web("#e8ddb7") : Color.web("#e9d8a6");
        }
        return Color.web("#d62828");
    }
}
