(ns problem2
  (:require [clojure.spec.alpha :as s]
            [clojure.data.json :as json]
            [invoice-spec :as invoice-spec]
            [clojure.string :as str])
  (:import (java.time LocalDate)
           (java.time ZoneId)
           (java.time ZonedDateTime)
           (java.time.format DateTimeFormatter)
           (java.time Instant)
           (java.util Date)))

;; Helper functions
(defn string-to-inst [date-string]
  (let [formatter       (DateTimeFormatter/ofPattern "dd/MM/yyyy")
        local-date      (LocalDate/parse date-string formatter)
        zoned-date-time (.atStartOfDay local-date (ZoneId/systemDefault))
        instant         (.toInstant zoned-date-time)]
    (Date/from instant)))

(defn snake-to-kebab [s]
  (str/replace s #"\_" "-"))


(defn qualify-key [ns]
  (fn [k] (keyword ns (snake-to-kebab k))))

;; Core transformation
(defn transform-invoice-ks-vs [invoice]
  (let [qualify-invoice-k  (qualify-key "invoice")
        qualify-items-k    (qualify-key "invoice-item")
        qualify-customer-k (fn [k]
                             (if (= k "company_name")
                               :customer/name
                               ((qualify-key "customer") k)))
        qualify-tax-k      (fn [tax-k]
                             (apply keyword (str/split tax-k #"_")))
        parse-invoice-vals (fn [invoice]
                             (-> invoice
                                 (update :invoice/issue-date string-to-inst)))
        parse-taxes        (fn [taxes]
                             (->> taxes
                                  (mapv #(update-keys % qualify-tax-k))
                                  (mapv (fn [tax]
                                          (-> tax
                                              (update :tax/rate double)
                                              (update :tax/category #(-> %
                                                                         str/lower-case
                                                                         keyword)))))))]
    (-> invoice
        (update-keys qualify-invoice-k)
        parse-invoice-vals
        (update :invoice/customer #(update-keys % qualify-customer-k))
        (update :invoice/items (fn [items]
                                 (->> items
                                      (mapv (fn [item]
                                              (-> item
                                                  (update-keys qualify-items-k)
                                                  (update :invoice-item/taxes parse-taxes))))))))))

;; Slurp and Transform
(defn parse-invoice [file-path]
  (let [invoice-str (slurp file-path)
        invoice-edn (-> invoice-str
                        json/read-str
                        (get "invoice")
                        transform-invoice-ks-vs)]
    invoice-edn))

(comment
  (def invoice (parse-invoice "invoice.json"))

  (s/valid? ::invoice-spec/invoice invoice)
  ;=> true
  (s/explain ::invoice-spec/invoice invoice)
  ;=> Success!
  (s/explain-data ::invoice-spec/invoice invoice)
  ;=> nil
  )