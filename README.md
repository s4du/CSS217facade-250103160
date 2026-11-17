# Flight Booking Lab: Facade Pattern

A Java console project implementing both phases of the lab.
Requires JDK 17 or newer. No external libraries are needed.

## Run

In IntelliJ IDEA, open this folder, select JDK 17 or newer, and run
`src/Main.java`. Mark `src` as Sources Root if necessary.

Alternatively, run from the project directory:

```sh
mkdir -p out
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

## Classes

| Class | Responsibility |
| --- | --- |
| Main | Demonstrates four scenarios using only the facade. |
| FlightBookingFacade | Coordinates booking, loyalty rules, errors, and cleanup. |
| SeatInventorySystem | Connects to the seat server and manages seat locks. |
| BaggagePricingEngine | Calculates baggage fees in USD. |
| CurrencyConverter | Converts USD into USD, EUR, GBP, or KZT. |
| PaymentProcessor | Initializes payments, applies a promo, and returns a receipt. |
| TicketIssuer | Generates a ticket from the seat lock and payment receipt. |

The five supplied subsystem files are unchanged. The implementation adds
`FlightBookingFacade.java` and `Main.java` in `src`.

## Booking workflow

The facade constructor creates five private subsystem objects. Main only calls
`bookFlight(...)` and does not access the subsystem objects.

The eight-argument method delegates to the nine-argument overload with STANDARD
as the default loyalty tier. Both methods share this workflow:

1. Connect to the reservation server and lock the seat.
2. Calculate baggage fees, unless the passenger is SILVER or GOLD.
3. Add the fee to the base fare and convert the total into the billing currency.
4. Initialize the payment gateway and apply the SILVER promo when eligible.
5. Charge the card and generate the ticket using the receipt and seat lock.

The local `lockToken` starts as null. The catch block releases the lock only if
one was acquired, prints the failure, and returns `BOOKING_FAILED: ...`.
A failed SILVER charge also clears its pending promo to prevent it from leaking
into the next booking on the same facade. The finally block always disconnects,
including after a successful return.

## Loyalty rules

| Tier | Baggage fee | Payment discount |
| --- | --- | --- |
| STANDARD | Calculated normally | None |
| SILVER | Zero; baggage engine is skipped entirely | 10% |
| GOLD | Zero; baggage engine is skipped entirely | None |

Tier comparisons ignore case. Null or unknown tiers receive standard pricing.
Existing eight-argument calls continue to work without changes.

## Four demonstration scenarios

1. Successful booking: KZ204, seat 14B, USD 320 base fare, two bags weighing
   24.5 kg, paid in EUR. Baggage costs 35 + (24.5 - 20) * 12 = USD 89.
   The total is 409 * 0.92 = EUR 376.28. An ETKT ticket is returned.
2. Blocked seat: 13A is rejected, the connection closes, and the program continues.
3. Payment failure: 08C is locked, but card 1234 is rejected. The log shows
   `Released lock: LOCK-KZ204-08C`, the failure message, and disconnection.
4. SILVER: three bags weighing 30 kg produce no BaggageEngine output.
   The base fare converts to 320 * 0.92 = EUR 294.40. The promo reduces
   the charge to 294.40 * 0.90 = EUR 264.96.

Decimal separators in console output depend on the system locale.

## Why use a facade?

Main requests a complete business operation through a single interface.
Only the facade knows subsystem initialization, execution order, loyalty rules,
and cleanup requirements. Adding loyalty support does not require existing
clients to change or learn subsystem APIs.

## Scope

The supplied subsystems simulate booking with console output. They do not
connect to a real airline, persist seat reservations, or charge real cards.
Rollback releases a seat lock; the lab provides no payment refund API.
