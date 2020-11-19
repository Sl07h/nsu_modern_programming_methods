; 1. Базовые операции над структурами данных
; Общее условие:Задан набор символов и число n.
; Опишите функцию, которая возвращает список всех строк длины n, состоящих
; из этих символов и не содержащих двух одинаковых символов, идущих подряд.
; Пример: Для символов 'а', 'b', 'c' и n=2 результат должен быть 
;("ab" "ac" "ba" "bc" "ca" "cb") с точностью до перестановки.

; Задача 1.1.
; Решите задачу с помощью элементарных операций над последовательностями и рекурсии.
(load-file "params.clj")

;; добавляет элементы списка l к концу строки s
(defn append-list [s l]
    ; если список не пуст
    (if (> (count l) 0)
        ; если последний символ строки совпал с элементом списка 
        (if (= (str (last s)) (first l))
            ; пропускаем его
            (append-list s (rest l))
            ; добавляем элемент списка к строке и идём дальше
            (concat 
                (list (str s (first l)))
                (append-list s (rest l))
            )
        )))
(println "Test 1")
(println (append-list "testa" abc))


(defn append-list-to-list [alp l]
    ; пока список не пуст
    (if (> (count l) 0)
        ; к каждому символу добавляем по элементу списка
        (concat 
            (append-list (first l) alp)
            (append-list-to-list alp (rest l))
        )))
(println "\nTest 2")
(def k (append-list-to-list abc abc))
(println k)
(println (append-list-to-list abc k))


(defn task-1-1[alp l i]
    ; пока не достигли нужной длины сочетаний
    (if (> i 1)
        (append-list-to-list alp (task-1-1 alp l (dec i)))
        l))
(println "\nTest 3")
(def res (task-1-1 alphabet alphabet N))
(println res)
(println (str "Expected length: " (int (* alplen (Math/pow (dec alplen) (dec N))))))
(println (str "True length:     " (count res)))