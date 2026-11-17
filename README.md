# Flight Booking Lab

Пять готовых классов подсистем из `FacadePatternLabTask.pdf`:

- `SeatInventorySystem` — проверка и блокировка мест.
- `BaggagePricingEngine` — расчёт доплаты за багаж.
- `CurrencyConverter` — конвертация USD в USD, EUR, GBP и KZT.
- `PaymentProcessor` — оплата и применение промокода.
- `TicketIssuer` — выпуск билета.

## Открытие проекта

В IntelliJ IDEA откройте папку `flight-booking-lab`, выберите JDK 17 или новее
и отметьте папку `src` как Sources Root, если IDE не определила её автоматически.
Сторонние библиотеки не нужны.

## Компиляция из терминала

Из папки проекта выполните:

```sh
mkdir -p out
javac -encoding UTF-8 -d out src/*.java
```

Это исходные пять подсистем. Для выполнения лабораторной добавьте
`FlightBookingFacade.java` и `Main.java` в `src`.
