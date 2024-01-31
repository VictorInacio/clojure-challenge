(ns problem1
  (:require [clojure.edn :as edn]))

(defn filter-invoice-items
  "Filters items within an invoice that satifies exclusively one of two conditions:
  1- Item has :iva 19%
  2- Item has retention :ret_fuente 1%"
  [invoice]
  (->> invoice
       :invoice/items
       (filter (fn [item]
                 (let [has-iva-19?       (some #(and (= (:tax/category %) :iva)
                                                     (= (:tax/rate %) 19))
                                               (:taxable/taxes item))
                       has-ret-fuente-1? (some #(and (= (:retention/category %) :ret_fuente)
                                                     (= (:retention/rate %) 1))
                                               (:retentionable/retentions item))]
                   ;; Check exclusively one out of two predicates
                   (not= has-iva-19? has-ret-fuente-1?))))))

(comment
  ;; Example invocation
  (let [ex-invoice (edn/read-string (slurp "invoice.edn"))]
    (filter-invoice-items ex-invoice))

  )


