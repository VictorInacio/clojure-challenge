(ns problem2
  (:require [clojure.spec.alpha :as s]
            [clojure.data.json :as json]
            [invoice-spec :as invoice-spec]
            [clojure.string :as str]))


(defn snake-to-kebab [s]
  (str/replace s #"\_" "-"))

(defn qualify-key [ns]
  (fn [k] (keyword ns (snake-to-kebab k))))

(defn qualify-keys [invoice]
  (let [qualify-invoice-k (qualify-key "invoice")
        qualify-items-k (qualify-key "invoice-item")]
    (-> invoice
        (update-keys qualify-invoice-k)
        (update :invoice/items (fn [items]
                                 (map #(update-keys % qualify-items-k) items))))))

(defn parse-invoice [file-path]
  (let [invoice-str (slurp file-path)
        invoice-edn (-> invoice-str
                        json/read-str
                        (get "invoice")
                        qualify-keys)]
    invoice-edn))

(comment
  (def invoice (parse-invoice "invoice.json"))

  (s/valid? ::invoice-spec/invoice invoice)
  (s/explain ::invoice-spec/invoice invoice)
  (s/explain-data ::invoice-spec/invoice invoice)
  )