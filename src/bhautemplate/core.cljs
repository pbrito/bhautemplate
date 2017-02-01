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


(def st1 {:current_Page {:page :page_Home }
          :page_Home    {:html {:type "home-page" :layout [:butao2 :todoList] :label "Home"}}
          :page_New     {:html {:type "modal-form" :layout [:textBox1, :butao0, :butao1] :background :page_Home :label "New"}}
          :butao2       {:action "new-todo" :html {:type "simpleBut" :label "add new todo" :class "btn btn-primary"}}
          :todoList     {:html {:type "table-todo"} :todos {:list [:todo0 :todo1]} }
          :todo0        {:action "Edit" :html {:type "line-todo"} :todos {:content "buy milk"   :completed false}}
          :todo1        {:action "Edit" :html {:type "line-todo"} :todos {:content "buy cheese" :completed false}}
          :textBox1     {:action "todo text" :html {:type "textBox" :label "todo"} :text ""}
          :butao0       {:action "Save"      :html {:type "simpleBut" :label "Save" :class "btn btn-primary"}}
          :butao1       {:action "Cancel"    :html {:type "simpleBut" :label "Cancel" :class "btn btn-primary"}}
          } )

(def world (atom st1))

(defn newPage [w new_page modal]
  (-> w
      (assoc-in [:current_Page] {:page new_page})
      ;(assoc-in [:modal] modal)
      )
  )

(defn buttonSimpleX
  "{:action \"new-todo\" :html {:type \"simpleBut\" :label \"add new todo\" :class \"btn btn-primary\"}}"
  [botao]
  [:a
   {:href     "#" :class (:class (:html botao))
    :on-click (fn [e] (.stopPropagation e)
                      (put! inputA {:action (:action botao) :msg "msssg "}))}
       (:label (:html botao))
       ]

      )
(defn inputSimpleX
  "docstring"
  [semanticElem]
  [:input.form-control.new-task-name {:type        "text"
                                      :value       (:text semanticElem)
                                      :name        "content"
                                      :placeholder "New Task"
                                      :on-change   (fn [e]

                                                     (put! inputA {:action "Edit" :msg (.. e -target -value)})

                                                     ; (print (.. e -target -value))
                                                     )
                                      }]
  )

(defn lay
      "docstring"
      [semanticElem]
      (case
        (:type (:html semanticElem))
        "simpleBut"  (buttonSimpleX  semanticElem)
        "textBox"    (inputSimpleX   semanticElem)
        "table-todo" (let [
                         listTODO  (map (fn [a] (a @world)) (:list (:todos semanticElem ) ) )
                         ]
                       ; [:ul {:class "todo-list list-unstyled"} (map-indexed todo-task listTODO )]
                       ;(map-indexed todo-task listTODO )]

                       [:ul
                        (map-indexed (fn [i a] [:li {:key i} (:content (:todos a))]) listTODO)
                        ]
                     )
        ))

(rum/defc rend [{:keys [current_Page] :as world} ]
          (case (:page current_Page)

            :page_Home (let
                         [l   (:page_Home world)
                          pageElems (map (fn [a] (a world)) (:layout (:html l)) )
                          pl (map lay pageElems)
                          ]
                         [:div  pl])

            :page_New  (let
                         [l          ((:page current_Page)  world)
                          background (:background (:html l))
                          pageElems  (map (fn [a] (a world)) (:layout (:html l)) )
                          pl         (map lay pageElems)
                          ]
                         (if background
                           (let
                             [
                              b2  (map (fn [a] (a world)) (:layout (:html (background world))))
                              pl2 (map lay b2)]
                             [:div [:div.modal-form  [:h1 "Insert"]  pl] [:div pl2]])

                           [:div pl])

                          )))




(go
  (loop [stat [] ]
    ;selciona os elem que vao ser renderizados
        (rum/mount (rend @world ) (.getElementById js/document "app"))
        (let [input (<! inputA)
              _action (:action input)
              _msg  (:msg input)
              _page (:page (:current_Page @world))
              ]

             (do
               (print _action)
               (case _page
                     :page_Home(case _action
                                     "new-todo" (do
                                                  (swap! world newPage :page_New )
                                                  (recur stat)))

                     :page_New (case _action
                                   "Save" (do
                                            (swap! world newPage :page_Home )
                                            (recur stat))
                                   "Cancel" (do
                                              (swap! world newPage :page_Home )
                                              (recur stat))
                                   "Edit" (do
                                            ;(print "mmm")
                                            ;(print _msg)
                                            (swap! world  assoc :textBox1 {:action "todo text",
                                                                           :html {:type "textBox",
                                                                                  :label "todo"},
                                                                           :text _msg} )

                                            (recur stat))
                                  (recur stat))
                     (recur stat))))))
