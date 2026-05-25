# =============================================================================
# WHISPIN-SOKOBAN — Dockerfile
# Image multi-stage : build + runtime
# =============================================================================

# ── Étape 1 : Compilation ────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Installer JavaFX et les outils nécessaires
RUN apt-get update && apt-get install -y \
    openjfx \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copier les sources et les librairies
COPY lib/     ./lib/
COPY *.java   ./
COPY makefile ./

# Télécharger Jackson si absent
RUN for jar in jackson-core-2.17.2.jar jackson-databind-2.17.2.jar jackson-annotations-2.17.2.jar; do \
      [ -f "lib/$jar" ] || curl -fsSL \
        "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/${jar%-*}/${jar##*-}" \
        -o "lib/$jar"; \
    done

# Compiler
RUN mkdir -p bin && \
    javac \
      --module-path /usr/share/openjfx/lib \
      --add-modules javafx.controls,javafx.fxml \
      -cp "lib/*" \
      -d bin \
      $(find . -name "*.java" -not -path "./bin/*")

# ── Étape 2 : Image finale ───────────────────────────────────────────────────
FROM eclipse-temurin:17-jre

WORKDIR /app

# Installer JavaFX runtime + support affichage graphique
RUN apt-get update && apt-get install -y \
    openjfx \
    libgtk-3-0 \
    libgl1-mesa-glx \
    && rm -rf /var/lib/apt/lists/*

# Copier les fichiers compilés et les ressources
COPY --from=builder /app/bin/    ./bin/
COPY --from=builder /app/lib/    ./lib/
COPY assets/                     ./assets/
COPY niveau/                     ./niveau/

# Créer le dossier de sauvegardes
RUN mkdir -p sauvegardes

# Variable d'affichage (à surcharger avec -e DISPLAY=:0)
ENV DISPLAY=:0
ENV JAVAFX_LIB=/usr/share/openjfx/lib

ENTRYPOINT ["java", \
    "--enable-native-access=javafx.graphics", \
    "--module-path", "/usr/share/openjfx/lib", \
    "--add-modules", "javafx.controls,javafx.fxml", \
    "-cp", "bin:lib/*", \
    "InterfacePrincipale"]
