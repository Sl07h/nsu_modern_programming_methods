; Задача 1.3
; Определить функции my-map и my-filter, аналогичные map (для одного списка) и filter,
; выразив их через reduce и базовые операции над списками (cons, first, concat и т.п.)

; с сокращённой формой анонимной функции
(defn my-map1 [f l]
    (seq (reduce #(conj %1 (f %2))
                 []
                 l)))
(println (my-map1 inc (list 1 2 3 4)))


; с анонимной функцией
(defn my-map2 [f l]
    (seq (reduce (fn [acc x] (conj acc (f x)))
                 []
                 l)))
(println (my-map2 inc (list 1 2 3 4)))


; с сокращённой формой анонимной функции
(defn my-filter1[f l]
    (seq (reduce #(if (f %2)
                        (conj %1 %2)
                        %1)
                 []
                 l)))
(println (my-filter1 #(not= 0 (mod % 3))
                    (list 1 2 3 4)))


; с анонимной функцией
(defn my-filter2[f l]
    (seq (reduce (fn [acc x] 
                    (if (f x)
                        (conj acc x)
                    acc))
                 []
                 l)))
(println (my-filter2 (fn [x] (not= 0 (mod x 3)))
                    (list 1 2 3 4)))
