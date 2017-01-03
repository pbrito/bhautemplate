# bhautemplate

FIXME: Write a one-line description of your library/project.

## Overview

http://rigsomelight.com/2013/07/18/clojurescript-core-async-todos.html

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

## Alternativa

        lein repl
 
depois 

        (use 'figwheel-sidecar.repl-api)
        (start-figwheel!)
        (cljs-repl)
        (ns bhautemplate.core)
        
Aceder às variaveis
        
        st0
        st1
        
outros        
        
        (require '[sablono.core :as sab])
        ;( .render js/ReactDOM  (sab/html (todo-list st1)) (.getElementById js/document "app"))
        
        (require '[cljs.core.async :as async  :refer [<! >! chan close! sliding-buffer put! alts! timeout]])
                

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.