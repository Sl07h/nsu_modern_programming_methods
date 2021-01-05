;; автоматическое дифференциирование на clojure через шаблон Chain of responsibilities
;; http://ccfit.nsu.ru/~shadow/DT6/
;; http://ccfit.nsu.ru/~shadow/DT6/pdf/lecture_2_4_sym_comp.pdf


(declare diff)

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




;; порождение суммы
(defn sum [expr & rest]
    (cons ::sum (cons expr rest)))
;; проверка типа для суммы
(defn sum? [expr]
    (= ::sum (first expr)))
;; порождение произведения
(defn- collapse-prod-constants [exprs]
    (let [
        consts (filter constant? exprs)
        other-exprs
            (remove constant? exprs)
        combined-const
            (reduce
                (fn [acc entry]
                    (* acc
                    (constant-value entry)))
                1 consts)]
        (cond 
            (= combined-const 0)
                (list (constant 0))
            (= combined-const 1)
                (if (empty? other-exprs)
                    (list (constant 1))
                     other-exprs)
            :default 
                (cons (constant combined-const)
                 other-exprs))))
(defn product [expr & rest]
    (let [normalized-exprs
            (collapse-prod-constants
                (cons expr rest))]
        (if (= 1 (count normalized-exprs))
            (first normalized-exprs)
            (cons ::product
                normalized-exprs))))
;; проверка типа для произведения
(defn product? [expr]
    (= ::product (first expr)))
;; списокаргументов выражения
(defn args [expr]
    (rest expr))
;; порождение обратного выражения
(defn invert [expr]
    (list ::inv expr))
;; проверка типа для обратного выражения
(defn invert? [expr]
    (= ::inv (first expr)))




;; список правил вывода
(def diff-rules
    (list
        ;; [pred_i transform_i]
        ;; константа
        [(fn [expr vr] (constant? expr))
         (fn [expr vr] (constant 0))]
        
        ;; переменная дифференциирования
        [(fn [expr vr]
            (and
                (variable? expr)
                (same-variables? expr vr)))
         (fn [expr vr] (constant 1))]
        
        ;; другая переменная
        [(fn [expr vr] (variable? expr))
         (fn [expr vr] (constant 0))]

        ;; сумма
        [(fn [expr vr] (sum? expr))
         (fn [expr vr]
            (apply sum
                (map
                    #(diff % vr)
                    (args expr))))]

        ;; произведение
        [(fn [expr vr] (product? expr))
         (fn [expr vr]
            (let [
                first-arg (first (args expr))
                rest-prod
                    (apply product
                    (rest (args expr)))]
            (sum
                (product
                    (diff first-arg vr) rest-prod)
                (product first-arg
                    (diff rest-prod vr)))))]
    ))


(defn diff [expr vr]
    ((some (fn [rule]
            (if ((first rule) expr vr)
                (second rule)
                false))
        diff-rules)
    expr vr))

;; d(2+x) / dx = 0 + 1 = 1
(def s 
    (diff (sum
            (constant 2)
            (variable :x))
    (variable :x)))

;; d(2*x) / dx = 0*x + 2*1
(def p
    (diff (product 
            (constant 2)
            (variable :x))
    (variable :x)))


(println s)
(println p)