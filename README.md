# WHISPIN-SOKOBAN

Projet de L2 — Parabox-like game (Sokoban récursif)  
Groupe de 6 · Java + JavaFX · Semestre 2 · 2025/2026

---

## Prérequis

| Outil | Version minimale |
|---|---|
| Java (JDK) | 17+ |
| JavaFX | 17+ |
| Make | toute version |

Les JARs Jackson (persistance JSON) sont inclus dans `lib/` ou téléchargés automatiquement par `install.sh`.

---

## Installation rapide

```bash
git clone <url-du-depot>
cd WHISPIN-SOKOBAN
chmod +x install.sh
./install.sh
```

Le script détecte automatiquement votre système (Ubuntu, Debian, Fedora, Arch, macOS), installe Java et JavaFX si nécessaire, télécharge les dépendances manquantes, compile le projet et génère un fichier `run.sh`.

---

## Lancement

```bash
# Après install.sh
./run.sh

# Ou via make
make run

# Ou install + lancement en une commande
./install.sh --run
```

---

## Commandes Make disponibles

| Commande | Description |
|---|---|
| `make` ou `make build` | Compile tous les fichiers `.java` |
| `make run` | Compile et lance le jeu |
| `make jar` | Crée un fichier `sokoban.jar` |
| `make clean` | Supprime les fichiers compilés |
| `make help` | Affiche l'aide |

---

## Installation avec Docker (bonus)

```bash
# Construire l'image
docker build -t whispin-sokoban .

# Lancer (Linux — partage de l'affichage X11)
xhost +local:docker
docker run -e DISPLAY=$DISPLAY \
           -v /tmp/.X11-unix:/tmp/.X11-unix \
           -v $(pwd)/sauvegardes:/app/sauvegardes \
           whispin-sokoban
```

---

## Si JavaFX n'est pas trouvé automatiquement

```bash
# Indiquer manuellement le chemin de JavaFX
JAVAFX_LIB=/chemin/vers/javafx/lib ./install.sh

# Ou pour make
make run JAVAFX_LIB=/chemin/vers/javafx/lib
```

---

## Structure du projet

```
WHISPIN-SOKOBAN/
├── *.java          — Sources Java
├── bin/            — Classes compilées (généré)
├── lib/            — JARs Jackson
├── assets/         — Sprites, fonds d'écran
├── niveau/         — Fichiers de niveaux (.txt)
├── sauvegardes/    — Sauvegardes automatiques
├── makefile        — Compilation et lancement
├── install.sh      — Script d'installation automatique
├── run.sh          — Script de lancement (généré par install.sh)
└── Dockerfile      — Image Docker
```

---

## Contrôles

| Touche | Action |
|---|---|
| Flèches directionnelles | Déplacer le personnage |
| Clic souris sur une case | Déplacement automatique (A*) |
| `Ctrl+Z` | Annuler le dernier coup |
| `Ctrl+S` | Sauvegarder la partie |

---

## Dépendances

- **JavaFX** — Interface graphique
- **Jackson 2.17.2** — Sérialisation JSON des sauvegardes
  - `jackson-core`
  - `jackson-databind`
  - `jackson-annotations`

---

## Bugs connus

- L'affichage Docker nécessite un serveur X11 actif sur la machine hôte.
- Sur certaines distributions, le chemin JavaFX doit être spécifié manuellement via `JAVAFX_LIB`.
