#!/bin/bash

#require theese parameters on startup
PLAYERS=2
TIMEOUT=20000

java org/bajnarola/lobby/LobbyServer "$PLAYERS" "$TIMEOUT"
