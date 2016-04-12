# find-the-pair

Classic memory game developed with ClojureScript.

## Overview

In this game the board is filled with cards which are turned backwards. You have to turn two cards at a time and find matching icon!
You can try the game [here](http://henrikra.github.io/find-the-pair)

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## License

Copyright © henrikra

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
