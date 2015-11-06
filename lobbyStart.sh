#!/bin/bash

#require theese parameters on startup
PLAYERS=${1:-2}
NAME=${2:-""}
TIMEOUT=${3:-20000}

java org/bajnarola/lobby/LobbyServer "$PLAYERS" "$NAME" "$TIMEOUT" 
