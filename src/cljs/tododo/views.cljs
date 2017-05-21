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

(defn my-new-card []
  (fn []
    [ui/card
     [ui/card-title {:title "+ Add new card!"}]]))

(defn my-card [card]
  (fn [{:keys [title editing]}]
    (if editing
      [ui/card
        [ui/text-field {:hint-text title
                        :underline-style {:display "none"}
                        ;;:style {:padding-left 15}
                        :input-style {:color "#ccc"}}]]
      [ui/card
       [ui/card-text title]])))

(defn my-new-column []
  (fn []
    [:div.column
     [ui/card {:style {:background-color "#ddd"}}
      [ui/card-title {:title "+ Add new column"}]]]))

(defn my-column [column]
  (fn [{:keys [title cards editing]}]
    [:div.column
     (if editing
       [ui/card
        [ui/text-field {:hint-text title
                        :style {:padding-top 5
                                :font-size 20}
                        :underline-style {:display "none"}}]]
       [ui/card {:style {:background-color "#ddd"}}
        [ui/card-title {:title title}]])
     (for [c cards]
       [my-card c])
     [my-new-card]]))

(defn my-board []
  [:div.board])


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
         ;(map my-column @(f/subscribe [:board/columns]))
         (for [c @columns]
           [my-column c])
         [my-new-column]]]])))
