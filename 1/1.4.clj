; Задача 1.4.
; Изменить решение задачи 1.1/1.2 таким образом, чтобы вместо
; рекурсивных вызовов использовались map/reduce/filter

(ns task_1_4
    (:gen-class))
(load-file "params.clj")

; добавляет отфильтрованные элементы списка l к концу строки s
(defn append-list[s l]
    (map #(str s (first %))
        (filter #(not= (str(last s)) %) l )
    ))

(defn append-list-to-list[alp l]
    ; reduce нужен, чтобы "выпрямить массив"
    ; ((a b) (c d) (e f)) => (a b c d e f)
    (reduce concat
            (map #(append-list % alp) l)
    ))

(defn task-1-4
    ([alp i acc]
        ; пока не достигли нужной длины сочетаний
        (if (> i 1)
            (recur  alp
                    (dec i)
                    (append-list-to-list alp acc))
            acc))
    ([alp i]
        (task-1-4 alp i alp))
)



;; тестирование
(ns task_1_4_test
    (:use clojure.test)
    (:use task_1_4))

(deftest append-list-test
    (testing "append-list")
    (is (=  (append-list "qwea" '("a" "b" "c" "d"))
            '("qweab" "qweac" "qwead") ))
    (is (=  (append-list "qweZ" '("a" "b" "c" "d"))
            '("qweZa" "qweZb" "qweZc" "qweZd") )))

(deftest append-list-to-list-test
    (testing "append-list-to-list")
    (is (=  (append-list-to-list '("a" "b" "c") '("a" "b" "c"))
            '("ab" "ac" "ba" "bc" "ca" "cb") ))
    (is (=  (append-list-to-list '("a" "b" "c") '("ab" "ac" "ba" "bc" "ca" "cb"))
            '("aba" "abc" "aca" "acb" "bab" "bac" "bca" "bcb" "cab" "cac" "cba" "cbc") )))

(deftest task-1-4-test
    (testing "task-1-4")
    (is (=  (task-1-4 '("a" "b" "c") 2) 
            '("ab" "ac" "ba" "bc" "ca" "cb") ))
    (is (=  (task-1-4 '("a" "b" "c") 3) 
            '("aba" "abc" "aca" "acb" "bab" "bac" "bca" "bcb" "cab" "cac" "cba" "cbc") ))
    (let [N 2, abc '("a" "b" "c"), len (count abc)]
        (is (=  (count(task-1-4 abc N))
        (int (* len (Math/pow (dec len) (dec N))) ))) )
    (let [N 5, abc '("a" "b" "c" "d"), len (count abc)]
        (is (=  (count(task-1-4 abc N))
        (int (* len (Math/pow (dec len) (dec N))) ))) ))

(run-tests 'task_1_4_test)