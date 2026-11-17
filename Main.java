/** Demonstrates booking through the facade without accessing any subsystem. */
public class Main {
    public static void main(String[] args) {
        FlightBookingFacade bookingFacade = new FlightBookingFacade();

        System.out.println("=== TEST 1: Standard Successful Booking ===");
        String ticket1 = bookingFacade.bookFlight(
                "Amina Suleimenova", "KZ204", "14B", 320.00,
                2, 24.5, "EUR", "4532015112830366");
        System.out.println("Result 1: " + ticket1 + "\n");

        System.out.println("=== TEST 2: Blocked Crew Seat (Row 13) ===");
        String ticket2 = bookingFacade.bookFlight(
                "Daniyar Kassymov", "KZ204", "13A", 320.00,
                1, 15.0, "USD", "4532015112830366");
        System.out.println("Result 2: " + ticket2 + "\n");

        System.out.println("=== TEST 3: Payment Failure and Automatic Seat Rollback ===");
        String ticket3 = bookingFacade.bookFlight(
                "Timur Bekov", "KZ204", "08C", 320.00,
                1, 18.0, "KZT", "1234");
        System.out.println("Result 3: " + ticket3 + "\n");

        System.out.println("=== TEST 4: Silver Loyalty Benefits ===");
        String ticket4 = bookingFacade.bookFlight(
                "Sara Omarova", "KZ204", "16A", 320.00,
                3, 30.0, "EUR", "4532015112830366", "SILVER");
        System.out.println("Result 4: " + ticket4 + "\n");
    }
}
