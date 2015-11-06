#!/bin/bash

#require theese parameters on startup
PLAYERS=${1:-2}
PORT=${2:-""}
TIMEOUT=${3:-20000}

java org/bajnarola/lobby/LobbyServer "$PLAYERS" "$PORT" "$TIMEOUT" 
