public enum Mouvement {
    HAUT('H'),
    BAS('B'),
    GAUCHE('G'),
    DROITE('D');

    private final char code;

    Mouvement(char code) {
        this.code = code;
    }

    public char obtenirCode() {
        return code;
    }
    
    /**
     * Convertit un caractère en mouvement.
     *
     * Cette méthode :
     * 1) transforme le caractère en majuscule (ex: 'h' devient 'H'),
     * 2) parcourt les mouvements de l'énumération,
     * 3) retourne le mouvement correspondant au code trouvé.
     *
     * Si aucun mouvement ne correspond au code fourni,
     * une IllegalArgumentException est levée.
     *
     * @param code le caractère représentant un mouvement
     * @return le mouvement correspondant
     */
    public static Mouvement depuisCode(char code) {
        char majuscule = Character.toUpperCase(code);
        for (Mouvement mouvement : values()) {
            if (mouvement.code == majuscule) {
                return mouvement;
            }
        }
        throw new IllegalArgumentException("Code de mouvement invalide: " + code);
    }
}
