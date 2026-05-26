#!/usr/bin/env bash
[ -f .env ] && source .env
java --enable-native-access=javafx.graphics \
     --module-path "${JAVAFX_LIB}" \
     --add-modules javafx.controls,javafx.fxml \
     -cp "bin:lib/*" \
     InterfacePrincipale
