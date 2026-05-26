#!/usr/bin/env bash
# =============================================================================
# WHISPIN-SOKOBAN — Script d'installation et de lancement
# Rôle : Installation & Déploiement
# Compatible : Ubuntu/Debian, Fedora/RHEL, Arch Linux, macOS
# =============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

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
        local id_lc="${ID,,}"
        local like_lc="${ID_LIKE,,}"
        case "$id_lc" in
            ubuntu|debian|linuxmint) echo "debian" ;;
            fedora|rhel|centos)      echo "fedora" ;;
            arch|manjaro)            echo "arch"   ;;
            *)
                if [[ "$like_lc" == *"debian"* ]]; then
                    echo "debian"
                elif [[ "$like_lc" == *"rhel"* || "$like_lc" == *"fedora"* ]]; then
                    echo "fedora"
                elif [[ "$like_lc" == *"arch"* ]]; then
                    echo "arch"
                else
                    echo "unknown"
                fi
                ;;
        esac
    else
        echo "unknown"
    fi
}

install_packages() {
    case "$OS" in
        debian)
            sudo apt-get update -q
            sudo apt-get install -y "$@"
            ;;
        fedora)
            sudo dnf install -y "$@"
            ;;
        arch)
            sudo pacman -S --noconfirm "$@"
            ;;
        macos)
            if ! command -v brew >/dev/null 2>&1; then
                err "Homebrew est requis sur macOS pour installer les dépendances."
            fi
            brew install "$@"
            ;;
        *)
            err "Système non supporté automatiquement. Installez les dépendances manuellement."
            ;;
    esac
}

ensure_command() {
    local cmd="$1"
    local pkg_debian="$2"
    local pkg_fedora="$3"
    local pkg_arch="$4"
    local pkg_macos="$5"

    if command -v "$cmd" >/dev/null 2>&1; then
        return
    fi

    warn "$cmd non trouvé. Installation..."
    case "$OS" in
        debian) install_packages "$pkg_debian" ;;
        fedora) install_packages "$pkg_fedora" ;;
        arch)   install_packages "$pkg_arch" ;;
        macos)  install_packages "$pkg_macos" ;;
        *)      err "Impossible d'installer automatiquement $cmd" ;;
    esac
}

OS=$(detect_os)
info "Système détecté : $OS"

# ── 1. Vérification Java ─────────────────────────────────────────────────────
info "Vérification de Java..."

if ! command -v java &> /dev/null; then
    warn "Java non trouvé. Installation..."
    case "$OS" in
        debian) install_packages openjdk-17-jdk ;;
        fedora) install_packages java-17-openjdk-devel ;;
        arch)   install_packages jdk17-openjdk ;;
        macos)  brew install openjdk@17 || err "Installez Java 17 manuellement : https://adoptium.net" ;;
        *)      err "Installez Java 17+ manuellement : https://adoptium.net" ;;
    esac
fi

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
[ "$JAVA_VER" -lt 11 ] 2>/dev/null && err "Java 11+ requis (version détectée : $JAVA_VER)"
ok "Java $JAVA_VER détecté"

if ! command -v javac >/dev/null 2>&1; then
    warn "javac non trouvé. Installation du JDK..."
    case "$OS" in
        debian) install_packages openjdk-17-jdk ;;
        fedora) install_packages java-17-openjdk-devel ;;
        arch)   install_packages jdk17-openjdk ;;
        macos)  brew install openjdk@17 || err "Installez Java 17 manuellement : https://adoptium.net" ;;
        *)      err "Installez un JDK 17+ manuellement." ;;
    esac
fi

ensure_command make make make make make
if ! command -v curl >/dev/null 2>&1 && ! command -v wget >/dev/null 2>&1; then
    warn "curl/wget absent. Installation de curl..."
    case "$OS" in
        debian) install_packages curl ;;
        fedora) install_packages curl ;;
        arch)   install_packages curl ;;
        macos)  install_packages curl ;;
        *)      err "curl ou wget requis." ;;
    esac
fi
ensure_command unzip unzip unzip unzip unzip

# ── 2. Vérification JavaFX ───────────────────────────────────────────────────
info "Vérification de JavaFX..."

JAVAFX_VERSION="21.0.2"
JAVAFX_SDK="$HOME/javafx-sdk-${JAVAFX_VERSION}"
JAVAFX_CANDIDATES=(
    "${JAVAFX_LIB:-}"
    "/usr/share/openjfx/lib"
    "/snap/openjfx/current/sdk/lib"
)

# 1) JavaFX via variable d'environnement ou chemin système connu
FOUND_JFX=""
for candidate in "${JAVAFX_CANDIDATES[@]}"; do
    if [ -n "$candidate" ] && [ -d "$candidate" ] && ls "$candidate"/javafx*.jar >/dev/null 2>&1; then
        FOUND_JFX="$candidate"
        break
    fi
done

# 2) JavaFX SDK local déjà présent
if [ -n "$FOUND_JFX" ]; then
    JAVAFX_LIB="$FOUND_JFX"
    ok "JavaFX détecté : $JAVAFX_LIB"
elif [ -d "$JAVAFX_SDK/lib" ]; then
    JAVAFX_LIB="$JAVAFX_SDK/lib"
    ok "JavaFX $JAVAFX_VERSION déjà présent : $JAVAFX_LIB"
else
    # 3) Installation système ou téléchargement SDK
    warn "JavaFX non détecté. Installation..."
    case "$OS" in
        debian)
            install_packages openjfx
            JAVAFX_LIB="/usr/share/openjfx/lib"
            ;;
        fedora)
            install_packages java-17-openjdk-openjfx
            JAVAFX_LIB="/usr/share/openjfx/lib"
            ;;
        arch)
            install_packages javafx
            JAVAFX_LIB="/usr/share/java/javafx"
            ;;
        macos)
            # OpenJFX via Homebrew puis fallback SDK si besoin.
            install_packages openjfx || true
            if [ -d "$(brew --prefix openjfx 2>/dev/null)/libexec/lib" ]; then
                JAVAFX_LIB="$(brew --prefix openjfx)/libexec/lib"
            else
                JAVAFX_LIB=""
            fi
            ;;
        *)
            JAVAFX_LIB=""
            ;;
    esac

    if [ -z "${JAVAFX_LIB:-}" ] || [ ! -d "$JAVAFX_LIB" ]; then
        warn "Fallback: téléchargement de JavaFX SDK $JAVAFX_VERSION"
        case "$OS" in
            macos) ZIP="openjfx-${JAVAFX_VERSION}_osx-x64_bin-sdk.zip" ;;
            *)     ZIP="openjfx-${JAVAFX_VERSION}_linux-x64_bin-sdk.zip" ;;
        esac
        URL="https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/${ZIP}"

        pushd "$HOME" >/dev/null
        if command -v wget >/dev/null 2>&1; then
            wget -q "$URL" -O "$ZIP"
        else
            curl -fsSL "$URL" -o "$ZIP"
        fi
        unzip -q "$ZIP"
        rm -f "$ZIP"
        popd >/dev/null

        JAVAFX_LIB="$JAVAFX_SDK/lib"
    fi

    [ -d "$JAVAFX_LIB" ] || err "JavaFX introuvable après installation"
    ok "JavaFX prêt : $JAVAFX_LIB"
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
info "Compilation du projet via make..."
make build JAVAFX_LIB="$JAVAFX_LIB" && ok "Compilation réussie" || err "Erreur de compilation"

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
if [[ "${1:-}" == "--run" ]]; then
    info "Lancement du jeu..."
    ./run.sh
fi
