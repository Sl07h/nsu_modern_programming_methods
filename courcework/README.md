# DI-контейнер на c++

## Немного теории

Паттерн внедрение зависимости позволяет нам снизить связность кода, что позволяет упростить unit-тестирование.
Данный подход позволяет не передавать параметры вручную через конструктор класса.

Альтернативным решением проблемы является использование паттерна service locator, однако, последний по сути создаёт лишнюю сущность.


## На каких нововведениях с++ 11 он основан


1. шаблоны переменной длины
    * [docs](https://en.cppreference.com/w/cpp/language/parameter_pack)
    * [wikipedia](https://ru.wikipedia.org/wiki/%D0%92%D0%B0%D1%80%D0%B8%D0%B0%D1%82%D0%B8%D0%B2%D0%BD%D1%8B%D0%B9_%D1%88%D0%B0%D0%B1%D0%BB%D0%BE%D0%BD)

2. move-семантика
    * [r-value-ссылки](http://sergeyteplyakov.blogspot.com/2012/05/c-11-faq.html#rval)
    * [управление поведением по умолчанию](http://sergeyteplyakov.blogspot.com/2012/05/c-11-faq.html#default)
    * [оригинал](https://www.stroustrup.com/C++11FAQ.html#default)



## Компиляция и запуск

```
make all 
```

Результат:

```
-------------------------------------------------------------------------------
Devirtualization of FindSquares
  When the return values are captured.
-------------------------------------------------------------------------------
main.cpp:110
...............................................................................

benchmark name                                  iters   elapsed ns      average 
-------------------------------------------------------------------------------
No virtual function calls involved.                 1      1303644   1.30364 ms 
Virtual function calls involved.                    1      1803386   1.80339 ms 
Static function calls involved.                     1      1495112   1.49511 ms 

-------------------------------------------------------------------------------
Devirtualization of FindSquares
  When the return values are ignored.
-------------------------------------------------------------------------------
main.cpp:130
...............................................................................

benchmark name                                  iters   elapsed ns      average 
-------------------------------------------------------------------------------
No virtual function calls involved.                 1      1540448   1.54045 ms 
Virtual function calls involved.                    1      1680738   1.68074 ms 
Static function calls involved.                     1      1418933   1.41893 ms 

===============================================================================
All tests passed (2 assertions in 2 test cases)
```


## Зависимости

[Ссылка на catch v2.5.0](https://github.com/catchorg/Catch2/releases/download/v2.5.0/catch.hpp)

