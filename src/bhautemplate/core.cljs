(ns bhautemplate.core
  (:import [goog.dom query])
  (:require
    [goog.dom :as dom]
    [goog.events :as events]
    [rum.core :as rum]
    [cljs.core.async :as async
     :refer [<! >! chan close! sliding-buffer put! alts! timeout]])
  (:require-macros [cljs.core.async.macros :as m :refer [go go-loop alt!]]))



(enable-console-print!)
(println "This text is printed from src/bhautemplate/core.cljs. Go ahead and edit it and see reloading in action.")



(def inputA (chan))

(rum/defc  modal-form [{:keys [mode task-form] :as state}]
           (if mode
             [:div.modal-form
              [:h4 "Add Task"]
              [:form.new-task-form {:on-submit (fn [e]
                                                   (.preventDefault e)
                                                   ;(print e)
                                                   )}
               [:input.form-control.new-task-name {:type        "text"
                                                   :value       (:content task-form)
                                                   :name        "content"
                                                   :placeholder "New Task"}]
               [:p
                [:input {:type     "submit"
                         :value    "Save"
                         :class    "btn btn-primary"
                         :on-click (fn [e]
                                       (.stopPropagation e)
                                       ;(-> e
                                       ;    .-clientX
                                       ;    println)
                                       (put! inputA {:action "Save"
                                                     :msg    "message Save "
                                                     :type   "Button"
                                                     :event  e}))}]

                [:a {:href "#" :class "cancel-new-todo btn btn-default"
                     :on-click (fn [e]
                                   (.stopPropagation e)
                                   (put! inputA {:action "Cancel"
                                                 :msg    "message Cancel "
                                                 :type   "Button"
                                                 :event  e})
                                   )
                     } "cancel"]]]]))


(rum/defc  todo-task [idx {:keys [completed] :as task}]
           (let [control (if completed
                           [:i {:class "icon-ok-sign icon-white"}]
                           [:a {:href "#" :data-task-index 1}
                            [:i {:class "icon-ok-circle icon-white"}]])]
                [:li {:key idx}
                 control
                 [:span {:class (if completed "completed")}
                  (:content task)]]))


(rum/defc  todo-list [{:keys [todo-list] :as state}]
           [:div
            [:p
             [:a {:href "#" :class "new-todo btn btn-primary"
                  :on-click (fn [e]
                                ;(print "ioi222zzZZZo")
                                (put! inputA  {:action "new-todo" :msg "msssg "}))}

              "Add taska"]]
            [:ul {:class "todo-list list-unstyled"}
             (map-indexed todo-task todo-list)]

            (modal-form state)])



(def st0 {:todo-list [{:content "buy mi1111lk"} {:content "buy che1111ese"}]})


(def st1 (assoc {:todo-list [{:content "buy mi222lk"} {:content "buy chee222se"}]} :mode :add-todo-form))


(go
  (loop []
        (rum/mount (todo-list st1 ) (.getElementById js/document "app"))
        (let [input (<! inputA)
              _action (:action input)
              _event (:event input)]
             (case _action
               "Save"     (print "****Save***")
               "new-todo" (print "***new-todo****")
               (print _event))
             (recur))))



;(rum/mount (todo-list st1 ) (.getElementById js/document "app") )

;( .render js/ReactDOM  (sab/html (todo-list st1)) (.getElementById js/document "app"))


(defn on-js-reload [])

