; 1.2. Перепишите программу 1.1. так, чтобы все рекурсивныевызовы были хвостовыми
(load-file "params.clj")

;; добавляет элементы списка l к концу строки s
(defn append_list
    ([s l acc]
        ; если список не пуст
        (if (> (count l) 0)
            ; если последний символ строки совпал с элементом списка 
            (if (= (str (last s)) (first l))
                ; пропускаем его
                (recur s (rest l) acc)
                ; добавляем элемент списка к строке и идём дальше
                (recur  s 
                        (rest l) 
                        (concat acc (list (str s (first l))))))
            acc))
    ([s, l] 
        (append_list s l '()))
)
(println "Test 1")
(println (append_list "testa" abc))


(defn append_list_to_list 
    ([alp l acc]
        ; пока список не пуст
        (if (> (count l) 0)
            ; к каждому символу добавляем по элементу списка
            (recur  alp
                    (rest l)
                    (concat acc (append_list (first l) alp)))
            acc))
    ([alp l]
        (append_list_to_list alp l '()))
)
(println "\nTest 2")
(def k (append_list_to_list abc abc))
(println k)
(println (append_list_to_list abc k))


(defn task_1_2
    ([alp i acc]
        ; пока не достигли нужной длины сочетаний
        (if (> i 1)
            (recur  alp
                    (dec i)
                    (append_list_to_list alp acc))
            acc))
    ([alp i]
        (task_1_2 alp i alp))
)
(println "\nTest 3")
(def res (task_1_2 alphabet N))
(println res)
(println (str "Expected length: " (int (* alp_len (Math/pow (dec alp_len) (dec N))))))
(println (str "True length:     " (count res)))