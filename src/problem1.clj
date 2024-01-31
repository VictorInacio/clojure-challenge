(ns problem1
  (:require [clojure.edn]))

(defn filter-invoice-items [invoice]
  (->> invoice
       :invoice/items
       (filter (fn [item]
                 (let [has-iva-19?       (some #(and (= (:tax/category %) :iva)
                                                     (= (:tax/rate %) 19))
                                               (:taxable/taxes item))
                       has-ret-fuente-1? (some #(and (= (:retention/category %) :ret_fuente)
                                                     (= (:retention/rate %) 1))
                                               (:retentionable/retentions item))]
                   (not= has-iva-19? has-ret-fuente-1?))))))

(comment

  (let [sample-invoice (clojure.edn/read-string (slurp "invoice.edn"))]
    (filter-invoice-items sample-invoice))

  )


