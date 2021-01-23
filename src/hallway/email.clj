(ns hallway.email
  (:require
    [clj-http.client :as http]
    [clojure.pprint :as pp]
    [rum.core :as rum]))

; Tip: for fancier emails, use Mailchimp's email editor, export to HTML, and then render it with
; Selmer.
(def templates
  {:biff.auth/signup
   (fn [{:keys [biff.auth/link]}]
     {:subject "Sign up for hallway"
      :html (rum/render-static-markup
              [:div
               [:p "We received a request to sign up for hallway using this email address."]
               [:p [:a {:href link :target "_blank"} "Click here to create your account."]]
               [:p "If you did not request this link, you can ignore this email."]])})

   :biff.auth/signin
   (fn [{:keys [biff.auth/link]}]
     {:subject "Log in to hallway"
      :html (rum/render-static-markup
              [:div
               [:p "We received a request to log in to hallway using this email address."]
               [:p [:a {:href link :target "_blank"} "Click here to log in."]]
               [:p "If you did not request this link, you can ignore this email."]])})})

; You should take care of this before publicizing your site, especially if your sign-in form is not
; rendered with JS. Otherwise your deliverability will go down, and you'll be spamming innocent
; people. If you want to use recaptcha v3, add :recaptcha/secret-key to config/main.edn. To
; add recaptcha on the front-end, see https://developers.google.com/recaptcha/docs/v3.
(defn human? [{:keys [recaptcha/secret-key params]}]
  (if-not secret-key
    true
    (let [{:keys [success score]}
          (:body
            (http/post "https://www.google.com/recaptcha/api/siteverify"
              {:form-params {:secret secret-key
                             :response (:g-recaptcha-response params)}
               :as :json}))]
      (and success (<= 0.5 score)))))

; To send emails with mailgun, add the following to config/main.edn:
; :mailgun/api-key "your-api-key"
; :mailgun/endpoint "https://api.mailgun.net/v3/yourdomain.example.com/messages"
; :mailgun/from "hallway <contact@yourdomain.example.com>"
(defn send-email [{:keys [to template data]
                   :mailgun/keys [api-key endpoint from]
                   :as opts}]
  (if (and api-key (human? opts))
    (let [template-fn (get templates template)
          email-params (assoc (template-fn data)
                         :from from
                         :to to)]
      (http/post endpoint
        {:basic-auth ["api" api-key]
         :form-params email-params}))
    (pp/pprint [:send-email (select-keys opts [:to :template :data])])))
