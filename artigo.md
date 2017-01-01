#Manifest

The four dogmas of the web programmer:
- ####Components
When a programmer see a web page tries to map what he sees with his mental 
model of the program and programer ways. So he use the same techniques and 
imagines little boxes and calls that components. A consequence of this is 
the cascading divs.Then came all the stuff of OO.
- ####loop
Many programers now days sees thats there is a loop in the interaction between
program and user(we can see this in many "reactive" web frameworks ).
But the programer is run in a web browser and the browser has it's own loop. So
we end up with two concurrent loops.
- ####css
It's a global place where you put a bunch of declarations about how the programmers
wants it to look. But once again there is the browser, that will take that information
and apply on top its own rules, before showing the result to the user.
CSS give a indirect control over the result, so the programmer has to guess what the browser
will do.
- ####routing
Browser have an address bar that is standard way that the user uses it. In principle that has 
nothing to do with the program that was made. But the bar started to be used as a drop
place of information between the server and the program running in the browser.
 Rapidly this became a way of storing the application progress with several frameworks.
 
 ##My View
Instead of using the loaded word components I will use the word entity refering to 
"things" that are display on the screen and also to "things" related to the application
 model.

Go chanel:

    (def inputA (chan))
 
Example of a action associated width a button:
            
    :on-click (fn [e]
                 (put! inputA {:action "Save"
                               :msg "message Save "
                               :type "Button"
                               :event (data-from-event e) }))
      
I don't do :
        
        :on-click #(remove-todo! td)
                       

Loop:
    
    (go
      (loop []
            (rum/mount (todo-list st1 ) (.getElementById js/document "app"))
            (let [val (<! inputA)]
                 (case (:action val)
                   "Save"     (print "****Save***")
                   "new-todo" (print "***new-todo****")
                   (print (:action val)))
                 (recur))))
                 
So the rendering part has no logic, the only side effect is putting a "action" in the chanel.It's powerless 
side effect with delayed consequences.
Is in the app loop that the consequences of the actions are realized. 
    Now this looks like old text adventures games
where the games stops and waits for the player input (action).
 
- *ref http://gameprogrammingpatterns.com/game-loop.html

## State

State and state machine