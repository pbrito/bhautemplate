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


(rum/defc  todo-task [idx {:keys [completed] :as task}]
           (let [control (if completed
                           [:i {:class "icon-ok-sign icon-white"}]
                           [:a {:href "#" :data-task-index 1}
                            [:i {:class "icon-ok-circle icon-white"}]])]
                [:li {:key idx}
                 control
                 [:span {:class (if completed "completed")}
                  (:content task)]]))


(def st1 {:current_Page :page_Home
          :page_Home  [:todoList :butao2]
          :butao2     {:type "simpleBut" :label "add new todo"  :action "new-todo" :html "btn btn-primary"}
          :todoList   {:type "todoList" :list [:todo0 :todo1]}
          :todo0 "buy milk"
          :todo1 "buy cheese"
          :page_New [:textBox1 :butao0 :butao1]
          :textBox1 {:text "" :type "textBox" }
          :butao0   {:type "simpleBut" :label "Save"   :action "Save"  :html "btn btn-primary"}
          :butao1   {:type "simpleBut" :label "Cancel" :action "Cancel" :html "btn btn-primary"}
          } )

;(assoc (assoc-in st1 [:page] "New"):mode :add-todo-form)

(def world (atom st1))

(defn newPage [i new_page modal]
  (-> i
      (assoc-in [:current_Page ] new_page)
      (assoc-in [:modal ] modal))
  )

(defn buttonSimpleX [botao]
      [:a
       {:href     "#" :class (:html botao)
        :on-click (fn [e]
                      (.stopPropagation e)
                      (put! inputA {:action (:action botao) :msg "msssg "}))}
       (:label botao)
       ]

      )

(defn lay
      "docstring"
      [semanticElem]
      (case
        (:type semanticElem)
        "simpleBut" (buttonSimpleX  semanticElem )
        "textBox"  [:input.form-control.new-task-name {:type        "text"
                                                       :value       (:text semanticElem)
                                                       :name        "content"
                                                       :placeholder "New Task"}]
        "todoList" (let [
                         listTODO (map (fn [a] (hash-map  :content (a @world))) (:list semanticElem ) )
                         ]
                     [:ul
                      {:class "todo-list list-unstyled"}
                      (map-indexed todo-task listTODO )]
                     )
        ))

(rum/defc rend [{:keys [current_Page] :as world} ]
          (case current_Page

                :page_Home (let
                             [l           (current_Page world)
                              pageElems (map (fn [a] (a world)) l)
                              pl (map lay pageElems)
                              ]
                             [:div  pl])

                :page_New  (let
                             [l (current_Page world)
                              ;lista ((first l) world)
                              pageElems (map (fn [a] (a world)) l)
                              pl (map lay pageElems)
                              ]
                             [:div pl])))




(go
  (loop [stat [] ]
        (rum/mount (rend @world ) (.getElementById js/document "app"))
        (let [input (<! inputA)
              _action (:action input)
              _page   (:current_Page   @world)
              ]
             (do
               (case _page
                     :page_Home(case _action
                                     "new-todo" (do
                                                  (swap! world newPage :page_New :mod)
                                                  (recur stat)))

                     :page_New (case _action
                                   "Save" (do
                                            (swap! world newPage :page_Home )
                                            (recur stat))
                                   "Cancel" (do
                                              (swap! world newPage :page_Home )
                                              (recur stat))
                                  (recur stat)
                                   )
                       (recur stat)
                       ))
             )
        ))



;(rum/mount (todo-list st1 ) (.getElementById js/document "app") )

;( .render js/ReactDOM  (sab/html (todo-list st1)) (.getElementById js/document "app"))


(defn on-js-reload [])

