import javafx.scene.input.KeyCode;

/**
 * Contrôleur d'état du menu principal.
 * Centralise la navigation clavier/souris et les actions à exécuter.
 */
public class ControleurMenu {
    public enum Ecran {
        MENU,
        REGLES,
        PARAMETRES,
        SAUVEGARDE
    }

    public enum Action {
        AUCUNE,
        REDESSINER,
        JOUER,
        QUITTER
    }

    private static final class OptionMenu {
        private final String texte;
        private final Ecran destination;
        private final Action action;

        private OptionMenu(String texte, Ecran destination, Action action) {
            this.texte = texte;
            this.destination = destination;
            this.action = action;
        }
    }

    private final OptionMenu[] optionsMenu = new OptionMenu[] {
        new OptionMenu("Jouer", null, Action.JOUER),
        new OptionMenu("Regles du jeu", Ecran.REGLES, Action.REDESSINER),
        new OptionMenu("Parametre", Ecran.PARAMETRES, Action.REDESSINER),
        new OptionMenu("Sauvegarde", Ecran.SAUVEGARDE, Action.REDESSINER),
        new OptionMenu("Quitter", null, Action.QUITTER)
    };

    private Ecran ecranActuel = Ecran.MENU;
    private int indexSelectionne = 0;

    public Ecran getEcranActuel() {
        return ecranActuel;
    }

    public boolean estSurMenuPrincipal() {
        return ecranActuel == Ecran.MENU;
    }

    public int getIndexSelectionne() {
        return indexSelectionne;
    }

    public int getNombreOptions() {
        return optionsMenu.length;
    }

    public String getTexteOption(int index) {
        return optionsMenu[index].texte;
    }

    public String getTitreEcranSecondaire() {
        return switch (ecranActuel) {
            case REGLES -> "Regles du jeu";
            case PARAMETRES -> "Parametre";
            case SAUVEGARDE -> "Sauvegarde";
            default -> "";
        };
    }

    public String[] getLignesEcranSecondaire() {
        return switch (ecranActuel) {
            case REGLES -> new String[] {
                "- Deplace l'abeille avec ZQSD.",
                "- Pousse les fleurs vers les ruches.",
                "- Une fleur ne peut pas traverser un mur.",
                "- Gagne quand toutes les fleurs sont dans les ruches.",
                "- Ctrl+Z pour annuler le dernier coup."
            };
            case PARAMETRES -> new String[] {
                "Parametres graphiques et sonores",
                "(pas encore implémenté).",
                "",
                "Conseil: lance une partie via 'Jouer'",
                "pour tester les animations."
            };
            case SAUVEGARDE -> new String[] {
                "Les sauvegardes du projet sont stockees dans:",
                "PERSISTANCE/solution/",
                "",
                "Cet ecran pourra accueillir plus tard",
                "charger/sauvegarder une partie depuis le menu."
            };
            default -> new String[0];
        };
    }

    public Action gererTouche(KeyCode code) {
        if (estSurMenuPrincipal()) {
            if (code == KeyCode.Z || code == KeyCode.UP || code == KeyCode.Q || code == KeyCode.LEFT) {
                indexSelectionne = (indexSelectionne - 1 + optionsMenu.length) % optionsMenu.length;
                return Action.REDESSINER;
            }
            if (code == KeyCode.S || code == KeyCode.DOWN || code == KeyCode.D || code == KeyCode.RIGHT) {
                indexSelectionne = (indexSelectionne + 1) % optionsMenu.length;
                return Action.REDESSINER;
            }
            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                return validerSelectionCourante();
            }
            if (code == KeyCode.ESCAPE) {
                return Action.QUITTER;
            }
            return Action.AUCUNE;
        }

        if (code == KeyCode.ENTER || code == KeyCode.SPACE || code == KeyCode.ESCAPE || code == KeyCode.Q || code == KeyCode.LEFT) {
            ecranActuel = Ecran.MENU;
            return Action.REDESSINER;
        }

        return Action.AUCUNE;
    }

    public Action gererSelectionSouris(int index) {
        indexSelectionne = index;
        return validerSelectionCourante();
    }

    public Action gererRetourSecondaire() {
        ecranActuel = Ecran.MENU;
        return Action.REDESSINER;
    }

    private Action validerSelectionCourante() {
        OptionMenu option = optionsMenu[indexSelectionne];
        if (option.action == Action.REDESSINER && option.destination != null) {
            ecranActuel = option.destination;
        }
        return option.action;
    }
}