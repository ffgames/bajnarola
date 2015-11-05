#!/bin/bash

JVERS=`java -version 2>&1 | head -n 1 | cut -d "\"" -f 2 | cut -d "_" -f 1`
DEST_DIR="build/java_$JVERS"

jar cfm lobby.jar lobbyManifest.txt org/bajnarola/lobby/LobbyServer.class org/bajnarola/lobby/LobbyServer_Stub.class org/bajnarola/lobby/LobbyController.class org/bajnarola/networking/NetPlayer.class org/bajnarola/utils/BajnarolaRegistry.class

#compile game

mkdir -p "$DEST_DIR"

mv lobby.jar "$DEST_DIR"
#mv game.jar "$DEST_DIR"
