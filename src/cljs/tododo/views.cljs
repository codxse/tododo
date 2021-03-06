(ns tododo.views
    (:require [re-frame.core :as f]
              [reagent.core :as r]
              [cljsjs.material-ui]
              [cljs-react-material-ui.core :refer [get-mui-theme color]]
              [cljs-react-material-ui.reagent :as ui]
              [cljs-react-material-ui.icons :as ic]))

(defn cardr []
  (let [leorm (f/subscribe [:leorm])
        exp (f/subscribe [:expanded])]
    (fn []
      [ui/card
       {:expanded @exp
        :initialy-expanded true
        :style {:width 300
                :margin 25}}
       [ui/card-header
        {:title "Some title"
         :subtitle "subtitle"
         :act-as-expander true
         :show-expandable-button true
         :on-click #(f/dispatch-sync [:click/title])
         :on-touch-tap #(f/dispatch-sync [:click/title])}]
       [ui/card-actions
        [ui/flat-button {:label "Action 1"}]
        [ui/flat-button {:label "Action 2"}]]
       [ui/card-text {:expandable true}
        @leorm]])))

(def styles {:root {:display "flex"
                    :margin-top 25
                    :flex-wrap "wrap"
                    :justify-content "space-around"}
             :grid-list {:display "flex"
                         :flex-wrap "nowarp"
                         :overflow-x "auto"}})

(defn todo []
  (fn []
    [:div {:style (:root styles)}
     [ui/grid-list {:style (:grid-list styles)
                    :cols 2.2}
      (repeat 5 [ui/grid-tile {:title "Some title"}
                 [:img {:src "http://www.material-ui.com/images/grid-list/00-52-29-429_640.jpg"}]])]]))

(defn my-new-card [idx-column]
  "idx-column as identity of current column.
  Use it for adding new card."
  (fn [idx-column]
    ;(s/console.log "IDX COLUMN" idx-column)
    [ui/card
     [ui/card-title {:title "+ Add new card!"
                     :on-click #(f/dispatch-sync [:column/add-new-card! idx-column])}]]))

(defn my-card [card idx-column idx-card]
  (let [default-value (atom {})]
    (fn [{:keys [title editing id]} idx-column idx-card]
      (if editing
        ^{:key id}
        [ui/card
          [ui/text-field {:class-name "CSFX"
                          :default-value title
                          :underline-style {:display "none"}
                          :input-style {:color "#ccc"}
                          :on-change #(do
                                        (reset! default-value (.. % -target -value))
                                        (f/dispatch [:card/update-title! idx-column idx-card @default-value]))
                          :on-blur #(f/dispatch [:card/set-editing! idx-column idx-card false])
                          :on-key-press #(when (= (.-charCode %) 13)
                                           (f/dispatch [:card/set-editing! idx-column idx-card false]))}]]
        ^{:key id}
        [ui/card
         [ui/card-text title]]))))

(defn my-new-column []
  (fn []
    [:div.column
     [ui/card {:style {:background-color "#ddd"}}
      [ui/card-title {:title "+ Add new column"
                      :on-click #(f/dispatch-sync [:board/add-column!])}]]]))

(defn my-column [column idx-column]
  (let [default-value (atom "")]
    (fn [{:keys [title cards editing id]} idx-column]
      ^{:key idx-column}
      [:div.column
       (if editing
         [ui/card
          ^{:key id}
          [ui/text-field {:class-name "CSFE"
                          :default-value title
                          :style {:padding-top 5
                                  :font-size 20}
                          :underline-style {:display "none"}
                          :on-change #(do
                                        (reset! default-value (.. % -target -value))
                                        ;(println "DEF" @default-value))}]]
                                        (f/dispatch [:column/update-title! idx-column @default-value]))
                          ;:on-blur #(f/dispatch [:column/set-editing! idx-column false])
                          :on-key-press #(when (= (.-charCode %) 13) ;; 13 is code for enter
                                           (f/dispatch [:column/set-editing! idx-column false]))}]]
         [ui/card {:style {:background-color "#ddd"}}
          [ui/card-title {:title title}]])
       (map-indexed (fn [idx-card card] [my-card card idx-column idx-card]) cards)
       [my-new-card idx-column]])))

(defn main-panel []
  (let [name (f/subscribe [:name])
        title (f/subscribe [:title])
        columns (f/subscribe [:board/columns])]
    (fn []
      [ui/mui-theme-provider {:mui-theme (get-mui-theme {:palette {:text-color (color :green600)}})}
       [:div
        [ui/app-bar {:title @title
                     :icon-element-right
                            (r/as-element [ui/icon-button
                                           (ic/action-account-balance-wallet)])}]
        [:div.board
         (js/console.log "COLUMNS" @columns)
         (map-indexed (fn [idx-column c] ^{:key idx-column} [my-column c idx-column])
                      @columns)
         ;(for [c columns]
         ;  [my-column c 1])
         [my-new-column]]]])))
