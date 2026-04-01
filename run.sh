#!/bin/bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"
mvn -q -DskipTests package

cd target
java \
  --module-path stargate-1.0.0.jar:libs \
  --add-modules ALL-MODULE-PATH \
  --enable-native-access=javafx.graphics,javafx.media \
  -m net.dobiasovsky.michal.stargate/net.dobiasovsky.michal.stargate.App

