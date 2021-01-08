; Задача 4.
; По аналогии с задачей дифференцирования реализовать представление символьных
; булевых выражений с операциями конъюнкции, дизъюнкции отрицания, импликации.
; Выражения могут включать как булевы константы, так и переменные.
; Реализовать подстановку значения переменной в выражение с его приведением к ДНФ.

(ns task_4
    (:gen-class))


(defn print [expr]
    (def s0 (str expr))
    (def s1 (clojure.string/replace s0 #":task_4\/" ""))
    (def s2 (clojure.string/replace s1 #"\(const (\d+)\)" "$1"))
    (def s3 (clojure.string/replace s2 #"\(var :([a-zA-Z]+)\)" "$1"))
    (println s3)
    )

(defn in? 
    "true if coll contains elm"
    [coll elm]  
    (some #(= elm %) coll))



(declare dnf)

(defn constant [num]
    {:pre [(number? num)]}
    (list ::const num))

(defn constant? [expr]
    (= (first expr) ::const))

(defn constant-value [v]
    (second v))

(defn same-constants? [v1 v2]
    (and
        (constant? v1)
        (constant? v2)
        (= (constant-value v1)
           (constant-value v2))))




(defn variable [name]
    {:pre [(keyword? name)]}
    (list ::var name))

(defn variable? [expr]
    (= (first expr) ::var))

(defn variable-name [v]
    (second v))

(defn same-variables? [v1 v2]
    (and
        (variable? v1)
        (variable? v2)
        (= (variable-name v1)
           (variable-name v2))))




;; порождение дизъюнкции
(defn bool_or [expr & rest]
    (cons ::or (cons expr rest)))
;; проверка типа для дизъюнкции
(defn bool_or? [expr]
    (= ::or (first expr)))
;; порождение конъюнкции
(defn bool_and
    ([rest]
        (cons ::and rest))
    ([expr & rest]
        (cons ::and (cons expr rest)))
    )
;; проверка типа для конъюнкции
(defn bool_and? [expr]
    (= ::and (first expr)))
;; список аргументов выражения
(defn args [expr]
    (rest expr))
;; порождение обратного выражения
(defn bool_not [expr]
    (list ::not expr))
(defn bool_not? [expr]
    (= ::not (first expr)))
;; проверка типа для обратного выражения
(defn single_not? [expr]
    (let [token2 (first (second expr))]
    (and
        (bool_not? expr)
        (not (bool_and? (second expr)))
        (not (bool_or? (second expr)))

    )))
;; порождение импликации
(defn bool_impl [v1 & v2]
    (cons ::impl (cons v1 v2)))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; провеки правил:
; 1. есть ли импликация
(defn bool_impl? [expr]
    (= ::impl (first expr)))

; 2. есть ли отрицание скобки
(defn not-expr? [expr]
    (and
        (bool_not? expr)
        (or
            (bool_and? (second expr)) ;; `(A * B)
            (bool_or? (second expr))  ;; `(A v B)
        )))

; 3. есть ли двойное отрицание
(defn not-not? [expr]
    (and
        (bool_not? expr)
        (bool_not? (second expr))
    ))

; 4. дистрибутивность
(defn or-in-and? [expr]
    (and
        (bool_and? expr)
        (> (count
                (filter 
                        #(= ::or (first %))
                        (args expr)
                ))
            0
        )
    ))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; служебные функции
(defn my-concat-and [expr & rest]
    (let [exprs2 (cons expr rest)]
        (if (= 1 (count exprs2))
            (first exprs2)
            (cons ::and exprs2))))


(defn my-concat-or [expr & rest]
    (let [exprs2 (cons expr rest)]
        (if (= 1 (count exprs2))
            (first exprs2)
            (cons ::or exprs2))))


(defn append-to-and-or [expr or_list]
    (let [
        t (first expr)
        expr_args
            (case t
                ::not (list expr)
                ::impl expr
                ::or (args expr)
                ::and (args expr)
                (list expr)
            )
        or_list_args
            (if (variable? or_list)
                or_list
                (args or_list)
            )
        result 
                (map
                    #(apply bool_and (concat expr_args (list %)))
                    or_list_args)
        ]

        result
    ))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; список правил вывода
(def dnf-rules
    (list
        ;; [pred_i transform_i]
        ; https://en.wikipedia.org/wiki/Disjunctive_normal_form

        ;; [+] 1) Избавиться от всех логических операций, содержащихся в формуле, заменив их основными:
        ;; конъюнкцией, дизъюнкцией, отрицанием. Это можно сделать, используя равносильные формулы: 
        [(fn [expr] (bool_impl? expr))
         (fn [expr]
            (let [
                first-arg (bool_not (first (args expr)))
                second-arg (second (args expr))
                result (bool_or
                            first-arg
                            second-arg)
            ]
            ; (println "\n\nbool_impl")
            result
        ))]

        ; [+] 2. Заменить знак отрицания, относящийся ко всему выражению, знаками отрицания,
        ;; относящимися к отдельным переменным высказываниям на основании формул
        [(fn [expr] (not-expr? expr))
         (fn [expr]
          
            (let [
                case_and_expr
                    (apply bool_or
                            (map
                                bool_not
                                (args (second expr))))
                case_or_expr
                    (apply bool_and
                            (map
                                bool_not
                                (args (second expr))))
                ]
            ; (println "\n\nnot-expr")
            (if (bool_and? (second expr))
                case_and_expr
                case_or_expr )
        ))]

        ;; [+] 3. Избавиться от двойного отрицания
        [(fn [expr] (not-not? expr))
         (fn [expr] (second (second expr)))]

        ;; [+] 4. Дистрибутивность
        [(fn [expr] (or-in-and? expr))
         (fn [expr]
            (let [
                or_list 
                    (first
                        (filter 
                            #(= ::or (first %))
                            (args expr)
                        ))
                other
                    (remove 
                        #(= or_list %)
                        (args expr))

                tmp (map
                        #(append-to-and-or % or_list)
                        other
                    )
                l
                    (first (map
                        #(append-to-and-or % or_list)
                        other
                    ))
                result
                    (apply bool_or l)
            ]
                ; (println "\n\nor-in-and")
                result
            )
        )]

        ; выход из рекурсии        
        [(fn [expr] (constant? expr))
         (fn [expr] expr)]
        
        [(fn [expr] (variable? expr))
         (fn [expr] expr)]


        [(fn [expr] (single_not? expr))
         (fn [expr]
            (
                let [result (bool_not (dnf (second expr)))]
                ; (println "\n\nsingle_not")
                ; (print result)
                result
        )
        )]

        [(fn [expr] (bool_not? expr))
         (fn [expr] 
            (println "\n\nbool_not")
            expr)]


        [(fn [expr] (bool_and? expr))
         (fn [expr]
            (
                let [
                    ; A and A = A    идемпотентность
                    new_expr (distinct expr)
                    ; A and `A = 1   закон противоречия
                    has_inverted 
                        (boolean 
                            (some
                                #(in? (args new_expr) (bool_not %))
                                (args new_expr)))
                                        ; 1 or ... = 1
                    ; 0 and ... = 0
                    has_zeros
                        (boolean 
                            (in? 
                                (args new_expr)
                                (constant 0)))
                ]
                (if (or 
                        has_inverted
                        has_zeros
                    )
                        (constant 0)
                        (apply  my-concat-and
                            (map
                                #(dnf %)
                                (args new_expr))))
            )
        )]

        [(fn [expr] (bool_or? expr))
         (fn [expr]
            (
                let [
                    ; A or A = A    идемпотентность
                    new_expr (distinct expr)
                    ; A or `A = 1   закон исключающего третьего
                    has_inverted 
                        (boolean 
                            (some
                                #(in? (args new_expr) (bool_not %))
                                (args new_expr)))
                    ; 1 or ... = 1
                    has_ones
                        (boolean 
                            (in? 
                                (args new_expr)
                                (constant 1)))
                    ]
                (if (or 
                        has_inverted
                        has_ones
                    )
                        (constant 1)
                        (apply  my-concat-or
                            (map
                                #(dnf %)
                                (args new_expr))))
            )
        )]

    ))


(defn dnf [expr]
    ; (print expr)
   ((some (fn [rule]
            (if ((first rule) expr)
                (second rule)
                false))
        dnf-rules)
    expr))


(defn my-dnf [expr]
    (let [prev expr
          curr (dnf expr)]
        ; (print expr) ; вывод на каждой итерации
        (if (= prev curr)
            curr
            (my-dnf curr)
        )
    )
)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; тестирование
(ns task_4_test
    (:use clojure.test)
    (:use task_4))


(deftest test_private_functions
    (testing "Метод нужный при реализации дистрибутивности")
    (is (= 
            (list
                (bool_and
                    (variable :A)
                    (variable :B)
                    (variable :C))
                (bool_and
                    (variable :A)
                    (variable :B)
                    (variable :D)) )
            
            (append-to-and-or 
                (bool_and
                    (variable :A)
                    (variable :B))
                (bool_or
                    (variable :C)
                    (variable :D)) )
        )) 
    )


(deftest test_no_recurion
    (testing "Замена импликации на +,*,`")
    (is (= 
            (bool_or
                (bool_not (variable :A))
                (variable :B)
            )
            (my-dnf
                (bool_impl
                    (variable :A)
                    (variable :B)
                )
            )
        ))
    (is (= 
            (bool_or
                (variable :A)
                (variable :B)
            )
            (my-dnf
                (bool_impl
                    (bool_not (variable :A))
                    (variable :B)
                )
            )
        ))

    (testing "Раскрытие отрицание от выражения")
    (is (= 
            (bool_and
                (bool_not (variable :A))
                (bool_not (variable :B))
            )
            (my-dnf
                (bool_not
                    (bool_or
                        (variable :A)
                        (variable :B)
                    )
                )
            )
        ))
    (is (= 
            (bool_or
                (bool_not (variable :A))
                (bool_not (variable :B))
            )
            (my-dnf
                (bool_not
                    (bool_and
                        (variable :A)
                        (variable :B)
                    )
                )
            )
        ))

    (testing "Избавляемся от знаков двойного отрицания")
    (is (= 
            (bool_and
                (variable :A)
                (variable :B))
            (my-dnf
                (bool_not       ; 2 лишних
                    (bool_not   ; отрицания
                        (bool_and
                            (variable :A)
                            (variable :B)
                        ))))
        ))
    (is (= 
            (bool_and
                (variable :A)
                (variable :B))
            (my-dnf
                (bool_not               ; 4 лишних
                    (bool_not           ; отрицания
                        (bool_not       ; 4 лишних
                            (bool_not   ; отрицания
                                (bool_and
                                    (variable :A)
                                    (variable :B)
                                ))))))
        ))

    (testing "Используем закон дистрибутивности")
    (is (= 
            (bool_or
                (bool_and
                    (variable :A)
                    (variable :B)
                    (variable :C))
                (bool_and
                    (variable :A)
                    (variable :B)
                    (variable :D)))
            (my-dnf
                (bool_and
                    (bool_and
                        (variable :A)
                        (variable :B))
                    (bool_or
                        (variable :C)
                        (variable :D))))
        ))


    (testing "Идемпотентность конъюнкции и дизъюнкции")
    (is (= 
            (bool_and
                    (variable :X)
                    (bool_not (variable :Y)))
            (my-dnf
                (bool_and
                    (variable :X)
                    (bool_not (variable :Y))
                    (bool_not (variable :Y))
                ))
        ))
    (is (= 
            (bool_or
                    (variable :X)
                    (bool_not (variable :Y)))
            (my-dnf
                (bool_or
                    (variable :X)
                    (bool_not (variable :Y))
                    (bool_not (variable :Y))
                ))
        ))

    (testing "Закон исключающего третьего")
    (is (= 
        (constant 1)
        (my-dnf
            (bool_or
                (variable :X)
                (bool_not (variable :X))
            ))
        ))

    (testing "Закон противоречия")
    (is (= 
        (constant 0)
        (my-dnf
            (bool_and
                (variable :X)
                (bool_not (variable :X))
            ))
        ))
    )


(deftest test_complex_example
    (testing "Составной пример с википедии")
    (is (= 
            (bool_or
                (bool_and
                    (variable :X)
                    (bool_not (variable :Y)))
                (bool_and
                    (variable :X)
                    (bool_not (variable :Y))
                    (variable :Z))
            )
            (my-dnf
                (bool_not
                    (bool_or
                        (bool_impl
                            (variable :X)
                            (variable :Y))
                        (bool_not
                            (bool_impl
                                (variable :Y)
                                (variable :Z)))
                    )
                )
            )
        ))
    )


(deftest test_const
    (testing "проверяем, что (1 or ... = 1)")
    (is (= 
            (constant 1)
            (my-dnf
                (bool_or
                    (constant 1)
                    (bool_not
                        (bool_or
                            (bool_impl
                                (variable :X)
                                (variable :Y))
                            (bool_not
                                (bool_impl
                                    (variable :Y)
                                    (variable :Z)))
                        )
                    )
                )
            )
        ))
    (testing "проверяем, что (0 and ... = 0)")
    (is (= 
            (constant 0)
            (my-dnf
                (bool_and
                    (constant 0)
                    (bool_not
                        (bool_or
                            (bool_impl
                                (variable :X)
                                (variable :Y))
                            (bool_not
                                (bool_impl
                                    (variable :Y)
                                    (variable :Z)))
                        )
                    )
                )
            )
        ))
    )


(run-tests 'task_4_test)