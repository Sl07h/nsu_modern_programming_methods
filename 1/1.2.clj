; Задача 1.2. 
; Перепишите программу 1.1. так, чтобы все рекурсивныевызовы были хвостовыми
(load-file "params.clj")

;; добавляет элементы списка l к концу строки s
(defn append-list
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
        (append-list s l '()))
)
(println "Test 1")
(println (append-list "testa" abc))


(defn append-list-to-list 
    ([alp l acc]
        ; пока список не пуст
        (if (> (count l) 0)
            ; к каждому символу добавляем по элементу списка
            (recur  alp
                    (rest l)
                    (concat acc (append-list (first l) alp)))
            acc))
    ([alp l]
        (append-list-to-list alp l '()))
)
(println "\nTest 2")
(def k (append-list-to-list abc abc))
(println k)
(println (append-list-to-list abc k))


(defn task-1-2
    ([alp i acc]
        ; пока не достигли нужной длины сочетаний
        (if (> i 1)
            (recur  alp
                    (dec i)
                    (append-list-to-list alp acc))
            acc))
    ([alp i]
        (task-1-2 alp i alp))
)
(println "\nTest 3")
(def res (task-1-2 alphabet N))
(println res)
(println (str "Expected length: " (int (* alplen (Math/pow (dec alplen) (dec N))))))
(println (str "True length:     " (count res)))