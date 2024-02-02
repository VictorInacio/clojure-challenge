(ns problem3
  (:require [clojure.test :refer :all]
            [invoice-item :refer :all]))

(deftest subtotal-test
  (testing "Round Numbers Invoice"
    (is (= 100.0 (subtotal {:invoice-item/precise-quantity 1
                            :invoice-item/precise-price    100})) "No discount")
    (is (= 90.0 (subtotal {:invoice-item/precise-quantity 1
                           :invoice-item/precise-price    100
                           :invoice-item/discount-rate    10})) "With discount"))
  (testing "Decimal Numbers Invoice"
    (is (= 116.97 (subtotal {:invoice-item/precise-quantity 1.5
                             :invoice-item/precise-price    77.98})) "No discount")
    (is (= 97.263565 (subtotal {:invoice-item/precise-quantity 1.9
                                :invoice-item/precise-price    55.342
                                :invoice-item/discount-rate    7.5})) "With discount"))
  (testing "Zero Quantity"
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 0
                          :invoice-item/precise-price    999999.99})) "No discount")
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 0
                          :invoice-item/precise-price    55.342
                          :invoice-item/discount-rate    6.4})) "With discount"))
  (testing "Zero Price"
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 9999.76
                          :invoice-item/precise-price    0})) "No discount")
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 87645.123324
                          :invoice-item/precise-price    0.0
                          :invoice-item/discount-rate    5.2})) "With discount"))
  (testing "Total discount"
    (is (= 0.0 (subtotal {:invoice-item/precise-quantity 1111
                          :invoice-item/precise-price    8888
                          :invoice-item/discount-rate    100})) "With discount"))
  (testing "Over discount"
    (is (= -10.000000000000009 (subtotal {:invoice-item/precise-quantity 2
                                          :invoice-item/precise-price    50
                                          :invoice-item/discount-rate    110})) "With discount"))
  (testing "Negative Price"
    (is (= -50.0 (subtotal {:invoice-item/precise-quantity 1
                            :invoice-item/precise-price    -50})) "No discount")
    (is (= -90.0 (subtotal {:invoice-item/precise-quantity 2
                            :invoice-item/precise-price    -50
                            :invoice-item/discount-rate    10})) "With discount"))
  (testing "Wrong Params"
    (is (thrown? NullPointerException (subtotal {:invoice-item/precise-quantityy 1
                                                 :invoice-item/precise-pricee    -50})))
    )
  )

(comment
  (run-test subtotal-test)

  ;Testing problem3
  ;
  ;Ran 1 tests containing 13 assertions.
  ;0 failures, 0 errors.
  ;=> {:test 1, :pass 13, :fail 0, :error 0, :type :summary}
  )
