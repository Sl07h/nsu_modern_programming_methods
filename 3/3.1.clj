; Задача 3.1.
; Реализуйте параллельный вариант filter(не обязательно ленивый) с помощью future.
; Параллельная обработка должна производиться блоками по заданному числу элементов.
; Размер блоков следует вычислять вручную, без использования готовых функций вроде
; partition(для разделения последовательности следует использовать take и drop).
; Продемонстрируйте прирост производительности в сравнении с обычным фильтром.

(ns task_3_1
    (:gen-class))

(defn my-filter
    [condition coll]
    (if-not (empty? coll)
        (if (condition (first coll))
            (cons (first coll) (my-filter condition (rest coll)))
            (my-filter condition (rest coll))
        )
    ))

(defn handle-subseq [pred l i batchsize]
    (->> l
        ;; если бы в clojure были срезы, то l[i*batchsize:(i+1)*batchsize]
        (drop (* i batchsize))  ; [(i*batchsize):]
        (take batchsize)        ; [:batchsize]
        (my-filter pred)
    ))

(defn parallel-filter [pred l batchsize]
    (->> 
        (range 0 (/ (count l) batchsize))
        (map #(future (handle-subseq pred l % batchsize)))
        (doall)
        (map deref)
        (apply concat)
    ))



;; тестирование
(ns task_3_1_test
    (:use clojure.test)
    (:use task_3_1))


(def l (range 0 (inc 10)))
(def delay 10)
(defn f2[x] 
    (Thread/sleep delay)
    (= 0 (mod x 2)))
(defn f3[x] 
    (Thread/sleep delay)
    (= 0 (mod x 3)))
(defn f5[x] 
    (Thread/sleep delay)
    (= 0 (mod x 5)))



(deftest test_parallel_filter
    (testing "Testing f2")
    (is (= (filter f2 l) (parallel-filter f2 l 1)))
    (is (= (filter f2 l) (parallel-filter f2 l 2)))
    (is (= (filter f2 l) (parallel-filter f2 l 5)))

    (testing "Testing f3")
    (is (= (filter f3 l) (parallel-filter f3 l 1)))
    (is (= (filter f3 l) (parallel-filter f3 l 2)))
    (is (= (filter f3 l) (parallel-filter f3 l 5)))

    (testing "Testing f5")
    (is (= (filter f5 l) (parallel-filter f5 l 1)))
    (is (= (filter f5 l) (parallel-filter f5 l 2)))
    (is (= (filter f5 l) (parallel-filter f5 l 5))) )

; (run-tests 'task_3_1_test)


;; замеры времени
(defn -main [& args]
    ; (time (doall (filter f2 l)))
    ; (time (doall (filter f3 l)))
    ; (time (doall (filter f5 l)))
    ; (time (doall (parallel-filter f2 l 1)))
    ; (time (doall (parallel-filter f3 l 1)))
    ; (time (doall (parallel-filter f5 l 1)))
    ; (time (doall (parallel-filter f2 l 2)))
    ; (time (doall (parallel-filter f3 l 2)))
    ; (time (doall (parallel-filter f5 l 2)))
    ; (time (doall (parallel-filter f2 l 5)))
    ; (time (doall (parallel-filter f3 l 5)))
    ; (time (doall (parallel-filter f5 l 5)))


    (time (filter f2 l))
    (time (filter f3 l))
    (time (filter f5 l))
    (time (parallel-filter f2 l 1))
    (time (parallel-filter f3 l 1))
    (time (parallel-filter f5 l 1))
    (time (parallel-filter f2 l 2))
    (time (parallel-filter f3 l 2))
    (time (parallel-filter f5 l 2))
    (time (parallel-filter f2 l 5))
    (time (parallel-filter f3 l 5))
    (time (parallel-filter f5 l 5))
    (shutdown-agents)
)

(println "\n\nВыполняем замеры времени")
(-main)