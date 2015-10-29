#!/bin/bash

java -Djava.library.path=lib/native:lib/native -Dfile.encoding=UTF-8 -classpath .:lib/jinput.jar:lib/lwjgl_util.jar:lib/lwjgl.jar:lib/slick.jar org.bajnarola.game.MainClass
