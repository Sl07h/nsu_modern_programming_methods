; 1.4. Изменить решение задачи 1.1/1.2 таким образом, чтобы вместо
; рекурсивных вызовов использовались map/reduce/filter
(load-file "params.clj")

; добавляет отфильтрованные элементы списка l к концу строки s
(defn append_list[s l]
    (map #(str s (first %))
        (filter #(not= (str(last s)) %) l )
    ))
(println "Test 1")
(println (append_list "testa" abc))


(defn append_list_to_list[alp l]
    ; reduce нужен, чтобы "выпрямить массив"
    ; ((a b) (c d)) => (a b c d)
    (reduce concat
            (map #(append_list % alp) l)
    ))
(println "\nTest 2")
(def k (append_list_to_list abc abc))
(println k)
(println (append_list_to_list abc k))


(defn task_1_4
    ([alp i acc]
        ; пока не достигли нужной длины сочетаний
        (if (> i 1)
            (recur  alp
                    (dec i)
                    (append_list_to_list alp acc))
            acc))
    ([alp i]
        (task_1_4 alp i alp))
)
(println "\nTest 3")
(def res (task_1_4 alphabet N))
(println res)
(println (str "Expected length: " (int (* alp_len (Math/pow (dec alp_len) (dec N))))))
(println (str "True length:     " (count res)))