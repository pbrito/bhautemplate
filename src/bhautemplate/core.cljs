(ns bhautemplate.core
  (:import [goog.dom query])
  (:require
    [goog.dom :as dom]
    [goog.events :as events]
    [sablono.core :as sab]
    [rum.core :as rum]
    [cljs.core.async :as async
     :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
            )
  (:require-macros [cljs.core.async.macros :as m :refer [go go-loop alt!]])
  )


(enable-console-print!)

(println "This text is printed from src/bhautemplate/core.cljs. Go ahead and edit it and see reloading in action.")

; lein repl
; (use 'figwheel-sidecar.repl-api)
;(start-figwheel!)
;(cljs-repl)
;(ns bhautemplate.core)
;st0
;st1
;(require '[sablono.core :as sab])
;( .render js/ReactDOM  (sab/html (todo-list st1)) (.getElementById js/document "app"))

;; define your app data so that it doesn't get over-written on reload
;;    <div id="app">
;;      <h2>Figwheel template</h2>
;;     <p>Checkout your developer console.</p>
;;   </div>
;;____________TEMPLATES
;;



;(defn form-submit-chan [form-selector msg-name fields]
;  (let [rc (chan)]
;    (on ($ "body") :submit form-selector {}
;        (fn [e]
;          (jq/prevent e)
;          (put! rc [msg-name (fields-value-map form-selector fields)])))
;    rc))

;document.getElementById('form1').addEventListener('submit', function(evt){
;evt.preventDefault();
;document.getElementById('donate').style.display = 'none';
;document.getElementById('topMessage').style.display = 'none';
;})
(def inputA (chan))

(rum/defc  modal-form [{:keys [mode task-form] :as state}]
           (if mode
             [:div.modal-form
              [:h4 "Add Task"]
              [:form.new-task-form {:on-submit (fn [e] (.preventDefault e) (print e)  ) }
               [:input.form-control.new-task-name {:type        "text"
                                                   :value       (:content task-form)
                                                   :name        "content"
                                                   :placeholder "New Task"}]
               [:p
                [:input {:type "submit" :value "Save" :class "btn btn-primary" :on-click (fn [e]
                                                                                             (put! inputA {:action "Save" :msg "msssgA "})
                                                                                             )}]
                [:a {:href "#" :class "cancel-new-todo btn btn-default"} "cancel"]]]]))


(rum/defc  todo-task [idx {:keys [completed] :as task}]
           (let [control (if completed
                           [:i {:class "icon-ok-sign icon-white"}]
                           [:a {:href "#" :data-task-index 1}
                            [:i {:class "icon-ok-circle icon-white"}]])]
                [:li {:key idx}
                 control
                 [:span {:class (if completed "completed")}
                  (:content task)]]
                ))

(rum/defc  todo-list [{:keys [todo-list] :as state}]
           [:div
            [:p
             [:a {:href "#" :class "new-todo btn btn-primary"
                  :on-click (fn [e]
                                ;(print "ioi222zzZZZo")
                                (put! inputA  {:action "new-todo" :msg "msssg "})
                                )}
              "Add taska"]]
            [:ul {:class "todo-list list-unstyled"}
             (map-indexed todo-task todo-list)
             ]
            (modal-form state)
            ])

;;____________TEMPLATES____________________________________________________
;;
;;
;;_____________UTILS
;;
;(defn click-chan [selector msg-name]
;  (let [rc (chan)]
;    (on ($ "body") :click selector {}  ;;retirar on
;        (fn [e]
;          (jq/prevent e)
;          (put! rc [msg-name (data-from-event e)])))
;    rc))

;;______________UTILS____________________________________________________
;;

(def st0 {:todo-list [{:content "buy mi1111lk"} {:content "buy che1111ese"}]})


(def st1 (assoc {:todo-list [{:content "buy mi222lk"} {:content "buy chee222se"}]} :mode :add-todo-form))
(defn cc []
      (.render js/ReactDOM (sab/html (todo-list st0)) (.getElementById js/document "app"))

      )


(go
  (loop []
        (rum/mount (todo-list st1 ) (.getElementById js/document "app") )
        (let [val (<! inputA)]
             (print (:action val) )
             (recur)))
  )


;(rum/mount (todo-list st1 ) (.getElementById js/document "app") )

;( .render js/ReactDOM  (sab/html (todo-list st1)) (.getElementById js/document "app"))



(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
      ;( .render js/ReactDOM  (sab/html (todo-list st1)) (.getElementById js/document "app"))

      ;; optionally touch your app-state to force rerendering depending on
      ;; your application
      ;; (swap! app-state update-in [:__figwheel_counter] inc)
      )
