; Задача 3.2.
; Реализуйте ленивый параллельный filter, который должен работать в том числе с бесконечными потоками.
; Продемонстрируйте прирост производительности в сравнении с обычным фильтром.

(ns task_3_2
    (:gen-class))

(defn my-filter
    [condition coll]
    (if-not (empty? coll)
        (if (condition (first coll))
            (cons (first coll) (my-filter condition (rest coll)))
            (my-filter condition (rest coll))
        )
    ))

(defn cut-on-parts [l batchsize]
    ;; делим пока не исчерпаем последовательность l
    (take-while #(boolean (seq %))
        (map first
            (iterate
                (fn [[head rest]] (split-at batchsize rest))
                (split-at batchsize l) )
        )
    )
)


(defn parallel-filter-lazy [pred l batchsize]
    (->>
        (cut-on-parts l batchsize)
        (map #(future (my-filter pred %)))
        (doall)
        (map deref)
        (apply concat)
    )
)



;; тестирование
(ns task_3_2_test
    (:use clojure.test)
    (:use task_3_2))


(def l (range 0 (inc 10)))
(def delay 1)
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
    (is (= (filter f2 l) (parallel-filter-lazy f2 l 1)))
    (is (= (filter f2 l) (parallel-filter-lazy f2 l 10)))
    (is (= (filter f2 l) (parallel-filter-lazy f2 l 100)))

    (testing "Testing f3")
    (is (= (filter f3 l) (parallel-filter-lazy f3 l 1)))
    (is (= (filter f3 l) (parallel-filter-lazy f3 l 10)))
    (is (= (filter f3 l) (parallel-filter-lazy f3 l 100)))

    (testing "Testing f5")
    (is (= (filter f5 l) (parallel-filter-lazy f5 l 1)))
    (is (= (filter f5 l) (parallel-filter-lazy f5 l 10)))
    (is (= (filter f5 l) (parallel-filter-lazy f5 l 100))) )

; (run-tests 'task_3_2_test)


;; замеры времени
(defn -main [& args]
    (time (doall (filter f2 l)))
    (time (doall (filter f3 l)))
    (time (doall (filter f5 l)))
    (time (doall (parallel-filter-lazy f2 l 1)))
    (time (doall (parallel-filter-lazy f3 l 1)))
    (time (doall (parallel-filter-lazy f5 l 1)))
    (time (doall (parallel-filter-lazy f2 l 2)))
    (time (doall (parallel-filter-lazy f3 l 2)))
    (time (doall (parallel-filter-lazy f5 l 2)))
    (time (doall (parallel-filter-lazy f2 l 5)))
    (time (doall (parallel-filter-lazy f3 l 5)))
    (time (doall (parallel-filter-lazy f5 l 5)))

    (shutdown-agents)
)

(println "\n\nВыполняем замеры времени:")
(-main)