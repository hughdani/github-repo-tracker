(ns github-repo-tracker.views
  (:require
   [re-frame.core :as rf]
   [github-repo-tracker.subs :as subs]
   [github-repo-tracker.events :as events]
   [reagent.core :as r]))

(defn repo-item [repo]
  [:article.media.columns {:style {"margin-top" "25px"}}
   [:figure.media-left.column.is-2
    [:p (:full_name repo)]]
   [:div.media-content
    [:div.content
     [:p (:full_name repo)]
     [:p (:description repo)]]]
   [:div.media-right
    [:button.delete]]])

(defn repo-list []
  (let [repos @(rf/subscribe [::subs/repos])]
    [:div
     (for [repo repos]
       ^{:key (:id repo)}
       [repo-item repo])]))

(defn add-repo-form []
  (let [search-query (r/atom "")
        add-repo #(rf/dispatch [::events/track-repo @search-query])
        clear-search #(reset! search-query "")]
    (fn []
      [:div.field.has-addons
       [:div.control.is-expanded
        [:input.input {:type "text"
                       :value @search-query
                       :auto-focus true
                       :placeholder "Track a repository by its full name (i.e., microsoft/vscode)"
                       :on-change #(reset! search-query
                                           (-> % .-target .-value))
                       :on-key-down #(case (.-keyCode %)
                                       13 (add-repo)
                                       27 (clear-search)
                                       nil)}]]
       [:div.control
        [:a.button.is-info {:on-click add-repo} "Add"]]])))

(defn main-panel []
  [:div.container.is-fluid
   [:header {:style {:margin-top "25px"
                     :margin-bottom "25px"}}
    [:h1.title "GitHub Repo Tracker"]]
   [:main
    [add-repo-form]
    [repo-list]]])
