; 2. Численное интегрирование. 
; Общее условие:
; Реализовать функцию (оператор), принимающую аргументом функцию от одной переменной
; f и возвращающую функцию одной переменной, вычисляющую (численно) выражение:
; Можно использовать метод трапеций с постоянным шагом. 
; При оптимизации исходить из того, что полученная первообразная будет использоваться
; для построения графика (т.е. вызываться многократно в разных точках)

; Задча 2.1.
; Оптимизируйте функцию с помощью мемоизации

(ns task_2_1
    (:gen-class))

(defn f-linear [x] x)
(defn f-parabola [x] (* x x))
(defn f-polinom [x] (- (* x x) (* 5 x) 3))

(defn trapezoid [f a b]
    ; h = b - a
    (* (* (+ (f a) (f b)) (- b a)) 0.5) )

(defn integral [f a b h]
    (if (< a b)
        (+ 
            (trapezoid f a (+ a h))
            (integral f (+ a h) b h)
        )
        0 
    ) )

(def integral-mem (memoize integral))



;; тестирование
(ns task_2_1_test
    (:use clojure.test)
    (:use task_2_1))

(deftest functions-test
    (testing "Testing f(x)=x")
    (is (= (f-linear -10) -10))
    (is (= (f-linear 0) 0))
    (is (= (f-linear 10) 10))
    (testing "Testing f(x)=x^2")
    (is (= (f-parabola -10) 100))
    (is (= (f-parabola 0) 0))
    (is (= (f-parabola 10) 100))
    (testing "Testing f(x)=x^2 - 5x - 3")
    (is (= (f-polinom -10) 147))
    (is (= (f-polinom 0) -3))
    (is (= (f-polinom 10) 47)) )

(deftest trapezoid-test
    (testing "Testing trapezoid f(x)=x")
    (is (= (trapezoid f-linear -10 10 ) 0.0))
    (is (= (trapezoid f-linear 0 10 ) 50.0))
    (is (= (trapezoid f-linear -10 0 ) -50.0))
    (testing "Testing trapezoid f(x)=x^2")
    (is (= (trapezoid f-parabola -10 10 ) 2000.0))
    (is (= (trapezoid f-parabola 0 10 ) 500.0))
    (is (= (trapezoid f-parabola -10 0 ) 500.0))
    (testing "Testing trapezoid f(x)=x^2 - 5x - 3")
    (is (= (trapezoid f-polinom -10 10 ) 1940.0))
    (is (= (trapezoid f-polinom 0 10 ) 220.0))
    (is (= (trapezoid f-polinom -10 0 ) 720.0)) )

(deftest integral-test
    (testing "Testing integral f(x)=x")
    (is (= (integral f-linear -10 10 5) 0.0))
    (is (= (integral f-linear -10 10 10) 0.0))
    (is (= (integral f-linear 0 10 5) 50.0))
    (is (= (integral f-linear 0 10 10) 50.0))
    (is (= (integral f-linear -10 0 5) -50.0))
    (is (= (integral f-linear -10 0 10) -50.0))
    (testing "Testing integral f(x)=x^2")
    (is (= (integral f-parabola -10 10 5) 750.0))
    (is (= (integral f-parabola -10 10 10) 1000.0))
    (is (= (integral f-parabola 0 10 5) 375.0))
    (is (= (integral f-parabola 0 10 10) 500.0))
    (is (= (integral f-parabola -10 0 5) 375.0))
    (is (= (integral f-parabola -10 0 10) 500.0))
    (testing "Testing integral f(x)=x^2 - 5x - 3")
    (is (= (integral f-polinom -10 10 5) 690.0))
    (is (= (integral f-polinom -10 10 10) 940.0))
    (is (= (integral f-polinom 0 10 5) 95.0))
    (is (= (integral f-polinom 0 10 10) 220.0))
    (is (= (integral f-polinom -10 0 5) 595.0))
    (is (= (integral f-polinom -10 0 10) 720.0)) )

(deftest integral-memoize-test
    (testing "Testing memoized integral with f(x)=x")
    (is (= (integral-mem f-linear -10 10 5) 0.0))
    (is (= (integral-mem f-linear -10 10 10) 0.0))
    (is (= (integral-mem f-linear 0 10 5) 50.0))
    (is (= (integral-mem f-linear 0 10 10) 50.0))
    (is (= (integral-mem f-linear -10 0 5) -50.0))
    (is (= (integral-mem f-linear -10 0 10) -50.0))
    (testing "Testing memoized integral with f(x)=x^2")
    (is (= (integral-mem f-parabola -10 10 5) 750.0))
    (is (= (integral-mem f-parabola -10 10 10) 1000.0))
    (is (= (integral-mem f-parabola 0 10 5) 375.0))
    (is (= (integral-mem f-parabola 0 10 10) 500.0))
    (is (= (integral-mem f-parabola -10 0 5) 375.0))
    (is (= (integral-mem f-parabola -10 0 10) 500.0))
    (testing "Testing memoized integral with f(x)=x^2 - 5x - 3")
    (is (= (integral-mem f-polinom -10 10 5) 690.0))
    (is (= (integral-mem f-polinom -10 10 10) 940.0))
    (is (= (integral-mem f-polinom 0 10 5) 95.0))
    (is (= (integral-mem f-polinom 0 10 10) 220.0))
    (is (= (integral-mem f-polinom -10 0 5) 595.0))
    (is (= (integral-mem f-polinom -10 0 10) 720.0)) )

(run-tests 'task_2_1_test)


;; замеры времени
(defn -main [& args]  
    (time (integral f-polinom 0 50 0.5))
    (time (integral f-polinom 0 50 0.5))
    (time (integral-mem f-polinom -100 -50 0.5))
    (time (integral-mem f-polinom -50 0 0.5))
)

(println "\n\nВыполняем замеры времени:")
(-main)