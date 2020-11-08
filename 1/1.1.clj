; 1. Базовые операции над структурами данных
; Общее условие:Задан набор символов и число n.
; Опишите функцию, которая возвращает список всех строк длины n, состоящих
; из этих символов и не содержащих двух одинаковых символов, идущих подряд.
; Пример: Для символов 'а', 'b', 'c' и n=2 результат должен быть 
;("ab" "ac" "ba" "bc" "ca" "cb") с точностью до перестановки.
; 1.1. Решите задачу с помощью элементарных операций над последовательностями и рекурсии
(load-file "params.clj")

;; добавляет элементы списка l к концу строки s
(defn append_list [s l]
    ; если список не пуст
    (if (> (count l) 0)
        ; если последний символ строки совпал с элементом списка 
        (if (= (str (last s)) (first l))
            ; пропускаем его
            (append_list s (rest l))
            ; добавляем элемент списка к строке и идём дальше
            (concat 
                (list (str s (first l)))
                (append_list s (rest l))
            )
        )))
(println "Test 1")
(println (append_list "testa" abc))


(defn append_list_to_list [alp l]
    ; пока список не пуст
    (if (> (count l) 0)
        ; к каждому символу добавляем по элементу списка
        (concat 
            (append_list (first l) alp)
            (append_list_to_list alp (rest l))
        )))
(println "\nTest 2")
(def k (append_list_to_list abc abc))
(println k)
(println (append_list_to_list abc k))


(defn task_1_1[alp l i]
    ; пока не достигли нужной длины сочетаний
    (if (> i 1)
        (append_list_to_list alp (task_1_1 alp l (dec i)))
        l))
(println "\nTest 3")
(def res (task_1_1 alphabet alphabet N))
(println res)
(println (str "Expected length: " (int (* alp_len (Math/pow (dec alp_len) (dec N))))))
(println (str "True length:     " (count res)))