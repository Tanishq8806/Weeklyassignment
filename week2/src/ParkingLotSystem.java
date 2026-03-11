import java.util.*;

public class ParkingLotSystem {

    // Parking spot status
    static class Spot {
        String licensePlate;
        long entryTime;
        boolean occupied;

        Spot() {
            licensePlate = null;
            occupied = false;
        }
    }

    private Spot[] table;
    private int capacity = 500;
    private int totalProbes = 0;
    private int parkOperations = 0;

    public ParkingLotSystem() {
        table = new Spot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new Spot();
        }
    }

    // hash function
    private int hash(String license) {
        return Math.abs(license.hashCode()) % capacity;
    }

    // park vehicle using linear probing
    public void parkVehicle(String license) {

        int index = hash(license);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = license;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        totalProbes += probes;
        parkOperations++;

        System.out.println("parkVehicle(\"" + license + "\") → Assigned spot #" +
                index + " (" + probes + " probes)");
    }

    // exit vehicle
    public void exitVehicle(String license) {

        int index = hash(license);

        while (table[index].occupied) {

            if (table[index].licensePlate.equals(license)) {

                long duration = System.currentTimeMillis() - table[index].entryTime;

                double hours = duration / (1000.0 * 60 * 60);

                double fee = hours * 5; // $5 per hour

                table[index].occupied = false;

                System.out.println("exitVehicle(\"" + license + "\") → Spot #" +
                        index + " freed, Duration: " +
                        String.format("%.2f", hours) +
                        "h, Fee: $" +
                        String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found.");
    }

    // statistics
    public void getStatistics() {

        int occupiedCount = 0;

        for (Spot s : table) {
            if (s.occupied) {
                occupiedCount++;
            }
        }

        double occupancy = (occupiedCount * 100.0) / capacity;

        double avgProbes = parkOperations == 0 ? 0 :
                (double) totalProbes / parkOperations;

        System.out.println("\nParking Statistics:");
        System.out.println("Occupancy: " +
                String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " +
                String.format("%.2f", avgProbes));
    }

    public static void main(String[] args) throws InterruptedException {

        ParkingLotSystem parking = new ParkingLotSystem();

        parking.parkVehicle("ABC-1234");
        parking.parkVehicle("ABC-1235");
        parking.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        parking.exitVehicle("ABC-1234");

        parking.getStatistics();
    }
}