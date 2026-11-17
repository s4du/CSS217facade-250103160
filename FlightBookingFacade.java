/** Provides one entry point for the complete flight booking workflow. */
public class FlightBookingFacade {
    private final SeatInventorySystem seatSystem;
    private final BaggagePricingEngine baggageEngine;
    private final CurrencyConverter currencyConverter;
    private final PaymentProcessor paymentProcessor;
    private final TicketIssuer ticketIssuer;

    public FlightBookingFacade() {
        this.seatSystem = new SeatInventorySystem();
        this.baggageEngine = new BaggagePricingEngine();
        this.currencyConverter = new CurrencyConverter();
        this.paymentProcessor = new PaymentProcessor();
        this.ticketIssuer = new TicketIssuer();
    }

    public String bookFlight(String passengerName, String flightCode, String seatNumber,
                             double baseFareUSD, int bagCount, double bagWeightKg,
                             String currency, String cardNumber) {
        // Preserve the original API and reuse the same booking pipeline.
        return bookFlight(passengerName, flightCode, seatNumber, baseFareUSD,
                bagCount, bagWeightKg, currency, cardNumber, "STANDARD");
    }

    public String bookFlight(String passengerName, String flightCode, String seatNumber,
                             double baseFareUSD, int bagCount, double bagWeightKg,
                             String currency, String cardNumber, String loyaltyTier) {
        String lockToken = null;
        boolean promoPending = false;
        try {
            // Connect before checking availability or locking a seat.
            seatSystem.connectToReservationServer();
            lockToken = seatSystem.lockSeat(flightCode, seatNumber, passengerName);

            boolean isSilver = "SILVER".equalsIgnoreCase(loyaltyTier);
            boolean isGold = "GOLD".equalsIgnoreCase(loyaltyTier);
            // Eligible tiers skip the baggage subsystem entirely.
            double baggageFeeUSD = (isSilver || isGold)
                    ? 0.0 : baggageEngine.calculateBaggageFeeUSD(bagCount, bagWeightKg);
            double totalUSD = baseFareUSD + baggageFeeUSD;
            double convertedTotal = currencyConverter.convertFromUSD(totalUSD, currency);

            paymentProcessor.initializeGateway();
            if (isSilver) {
                paymentProcessor.applyPromoCode("SILVER-FREE-BAG");
                promoPending = true;
            }
            String receiptId = paymentProcessor.chargeCard(cardNumber, convertedTotal, currency);
            promoPending = false;
            return ticketIssuer.generateTicket(passengerName, flightCode, lockToken, receiptId);
        } catch (RuntimeException ex) {
            // Release only a lock that this booking actually acquired.
            if (lockToken != null) {
                seatSystem.releaseSeatLock(lockToken);
            }
            // Failed charges do not reset the subsystem's promo state themselves.
            if (promoPending) {
                paymentProcessor.applyPromoCode(null);
            }
            System.out.println("[FlightBookingFacade] Booking failed: " + ex.getMessage());
            return "BOOKING_FAILED: " + ex.getMessage();
        } finally {
            // Always close the connection, including after a successful return.
            seatSystem.disconnect();
        }
    }
}
