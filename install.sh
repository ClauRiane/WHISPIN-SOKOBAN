#!/usr/bin/env bash
# =============================================================================
# WHISPIN-SOKOBAN — Script d'installation et de lancement
# Rôle : Installation & Déploiement
# Compatible : Ubuntu/Debian, Fedora/RHEL, Arch Linux, macOS
# =============================================================================

set -e

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; BOLD='\033[1m'; RESET='\033[0m'

ok()   { echo -e "${GREEN}[OK]${RESET}  $1"; }
info() { echo -e "${BLUE}[INFO]${RESET} $1"; }
warn() { echo -e "${YELLOW}[WARN]${RESET} $1"; }
err()  { echo -e "${RED}[ERR]${RESET}  $1"; exit 1; }

echo -e "${BOLD}"
echo "╔══════════════════════════════════════════════╗"
echo "║         WHISPIN-SOKOBAN — Installation       ║"
echo "╚══════════════════════════════════════════════╝"
echo -e "${RESET}"

# ── Détection de l'OS ────────────────────────────────────────────────────────
detect_os() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "macos"
    elif [ -f /etc/os-release ]; then
        . /etc/os-release
        case "$ID" in
            ubuntu|debian|linuxmint) echo "debian" ;;
            fedora|rhel|centos)      echo "fedora" ;;
            arch|manjaro)            echo "arch"   ;;
            *)                       echo "unknown";;
        esac
    else
        echo "unknown"
    fi
}

OS=$(detect_os)
info "Système détecté : $OS"

# ── 1. Vérification Java ─────────────────────────────────────────────────────
info "Vérification de Java..."

if ! command -v java &> /dev/null; then
    warn "Java non trouvé. Installation..."
    case "$OS" in
        debian) sudo apt-get update -q && sudo apt-get install -y openjdk-17-jdk ;;
        fedora) sudo dnf install -y java-17-openjdk-devel ;;
        arch)   sudo pacman -S --noconfirm jdk17-openjdk ;;
        macos)  brew install openjdk@17 || err "Installez Java 17 manuellement : https://adoptium.net" ;;
        *)      err "Installez Java 17+ manuellement : https://adoptium.net" ;;
    esac
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
[ "$JAVA_VER" -lt 11 ] 2>/dev/null && err "Java 11+ requis (version détectée : $JAVA_VER)"
ok "Java $JAVA_VER détecté"

# ── 2. Vérification JavaFX ───────────────────────────────────────────────────
info "Vérification de JavaFX..."

JAVAFX_VERSION="21.0.2"
JAVAFX_SDK="$HOME/javafx-sdk-${JAVAFX_VERSION}"

# Vérifie si le JavaFX système est compatible avec la version de Java
JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
SYSTEM_JFX_VER=$(dpkg -l 2>/dev/null | grep openjfx | awk '{print $3}' | cut -d'.' -f1 | head -1)

if [ -d "$JAVAFX_SDK/lib" ]; then
    # JavaFX 21 déjà téléchargé
    JAVAFX_LIB="$JAVAFX_SDK/lib"
    ok "JavaFX $JAVAFX_VERSION déjà présent : $JAVAFX_LIB"

elif [ -n "$SYSTEM_JFX_VER" ] && [ "$SYSTEM_JFX_VER" -ge 17 ] 2>/dev/null; then
    # JavaFX système compatible
    JAVAFX_LIB="/usr/share/openjfx/lib"
    ok "JavaFX système compatible ($SYSTEM_JFX_VER) : $JAVAFX_LIB"

else
    # JavaFX absent ou trop vieux → téléchargement de JavaFX 21
    warn "JavaFX absent ou incompatible (détecté : ${SYSTEM_JFX_VER:-aucun}). Téléchargement de JavaFX $JAVAFX_VERSION..."
    
    ZIP="openjfx-${JAVAFX_VERSION}_linux-x64_bin-sdk.zip"
    URL="https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/${ZIP}"
    
    cd ~
    if command -v wget &>/dev/null; then
        wget -q "$URL" -O "$ZIP"
    elif command -v curl &>/dev/null; then
        curl -fsSL "$URL" -o "$ZIP"
    else
        err "curl ou wget requis pour télécharger JavaFX"
    fi
    
    unzip -q "$ZIP"
    rm "$ZIP"
    cd - > /dev/null
    
    JAVAFX_LIB="$JAVAFX_SDK/lib"
    ok "JavaFX $JAVAFX_VERSION installé : $JAVAFX_LIB"
    
    # Ajout permanent dans .bashrc
    grep -q "JAVAFX_LIB" ~/.bashrc || \
        echo "export JAVAFX_LIB=$JAVAFX_LIB" >> ~/.bashrc
    warn "JAVAFX_LIB ajouté dans ~/.bashrc — relancez votre terminal ou faites : source ~/.bashrc"
fi

# ── 3. Vérification des JARs Jackson ────────────────────────────────────────
info "Vérification des dépendances Jackson..."

mkdir -p lib

JACKSON_VERSION="2.17.2"
JACKSON_BASE="https://repo1.maven.org/maven2/com/fasterxml/jackson"
JARS=(
    "core/jackson-core/${JACKSON_VERSION}/jackson-core-${JACKSON_VERSION}.jar"
    "core/jackson-databind/${JACKSON_VERSION}/jackson-databind-${JACKSON_VERSION}.jar"
    "core/jackson-annotations/${JACKSON_VERSION}/jackson-annotations-${JACKSON_VERSION}.jar"
)

for jar_path in "${JARS[@]}"; do
    jar_name=$(basename "$jar_path")
    if [ ! -f "lib/$jar_name" ]; then
        warn "$jar_name manquant. Téléchargement..."
        if command -v curl &>/dev/null; then
            curl -fsSL "${JACKSON_BASE}/${jar_path}" -o "lib/$jar_name" \
                || err "Échec du téléchargement de $jar_name"
        elif command -v wget &>/dev/null; then
            wget -q "${JACKSON_BASE}/${jar_path}" -O "lib/$jar_name" \
                || err "Échec du téléchargement de $jar_name"
        else
            err "curl ou wget requis pour télécharger $jar_name"
        fi
    fi
    ok "$jar_name présent"
done

# ── 4. Compilation ───────────────────────────────────────────────────────────
info "Compilation du projet..."

mkdir -p bin
SRC=$(find . -name "*.java" -not -path "./bin/*" | tr '\n' ' ')

javac \
    --module-path "$JAVAFX_LIB" \
    --add-modules javafx.controls,javafx.fxml \
    -cp "lib/*" \
    -d bin \
    $SRC && ok "Compilation réussie" || err "Erreur de compilation"

# ── 5. Sauvegarde de la config ───────────────────────────────────────────────
cat > .env << ENVEOF
JAVAFX_LIB=${JAVAFX_LIB}
ENVEOF
ok "Configuration sauvegardée dans .env"

# ── 6. Lancement ─────────────────────────────────────────────────────────────
echo ""
echo -e "${BOLD}Installation terminée !${RESET}"
echo ""
echo -e "Pour lancer le jeu :  ${BOLD}make run${RESET}"
echo -e "Ou directement :      ${BOLD}./run.sh${RESET}"
echo ""

# Générer run.sh
cat > run.sh << RUNEOF
#!/usr/bin/env bash
[ -f .env ] && source .env
java --enable-native-access=javafx.graphics \\
     --module-path "\${JAVAFX_LIB}" \\
     --add-modules javafx.controls,javafx.fxml \\
     -cp "bin:lib/*" \\
     InterfacePrincipale
RUNEOF
chmod +x run.sh
ok "run.sh généré"

# Lancer automatiquement si --run est passé
if [[ "$1" == "--run" ]]; then
    info "Lancement du jeu..."
    ./run.sh
fi
