package com.dealership;

import com.dealership.contracts.Contract;
import com.dealership.contracts.ContractFileManager;
import com.dealership.contracts.LeaseContract;
import com.dealership.contracts.SalesContract;
import com.dealership.vehicles.Dealership;
import com.dealership.vehicles.DealershipFileManager;
import com.dealership.vehicles.Vehicle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class UserInterface {
    private static final double SALES_TAX = 0.05;
    private static final double SMALL_APR = 0.0425;
    private static final double BIG_APR = 0.0525;
    private static final int SHORT_LOAN = 24;
    private static final int LONG_LOAN = 48;
    private static final int RECORDING_FEE = 100;
    private static final int SMALL_PROCESSING_FEE = 295;
    private static final int BIG_PROCESSING_FEE = 495;

    private static final double LEASE_APR = 0.04;
    private static final int LEASE_LENGTH = 36;

    private static final String MAIN_MENU = """
            Enter an option:
            1 - Display all vehicles.
            2 - Display vehicles by a given price range.
            3 - Display vehicles by make and model.
            4 - Display vehicles by year.
            5 - Display vehicles by color.
            6 - Display vehicles by mileage.
            7 - Display vehicles by type.
            8 - Add a vehicle.
            9 - Remove a vehicle.
            10 - Sell or lease a vehicle.
            0 - EXIT
            """;

    private static Dealership dealership;
    private UserInterface(){}

    public static void display() throws IOException {
        init();
        Scanner s = new Scanner(System.in);
        boolean complete = false;
        do {
            String userInput = getInputFromConsole(MAIN_MENU, s);
            int command = Integer.parseInt(userInput);
            switch (command) {
                case 1 -> displayVehicles(dealership.getVehicles(v -> true));
                case 2 -> {
                    double min = Double.parseDouble(getInputFromConsole("Enter the low end of the price range to search.", s));
                    double max = Double.parseDouble(getInputFromConsole("Enter the high end of the price range to search.", s));
                    displayVehicles(dealership.getVehicles(v -> dealership.searchPrice(min, max, v)));
                }
                case 3 -> {
                    String make = getInputFromConsole("Enter the make (manufacturer) of the vehicle to search.", s);
                    String model = getInputFromConsole("Enter the model type to search.", s);
                    displayVehicles(dealership.getVehicles(v -> dealership.searchMakeModel(make, model, v)));
                }
                case 4 -> {
                    int min = Integer.parseInt(getInputFromConsole("Enter the oldest year of the range to search.", s));
                    int max = Integer.parseInt(getInputFromConsole("Enter the most recent year of range to search.", s));
                    displayVehicles(dealership.getVehicles(v -> dealership.searchYear(min, max, v)));
                }
                case 5 -> {
                    String color = getInputFromConsole("Enter the color of vehicle to search.", s);
                    displayVehicles(dealership.getVehicles(v -> dealership.searchColor(color, v)));
                }
                case 6 -> {
                    int min = Integer.parseInt(getInputFromConsole("Enter the low end of the mileage range to search.", s));
                    int max = Integer.parseInt(getInputFromConsole("Enter the high end of the mileage range to search.", s));
                    displayVehicles(dealership.getVehicles(v -> dealership.searchMileage(min, max, v)));
                }
                case 7 -> {
                    String type = getInputFromConsole("Enter the type of vehicle to search for.", s);
                    displayVehicles(dealership.getVehicles(v -> dealership.searchType(type, v)));
                }
                case 8 -> addVehicle(s);
                case 9 -> {
                    int vin = Integer.parseInt(getInputFromConsole("Enter the VIN of the vehicle to remove.", s));
                    Vehicle remove = dealership.getVehicles(v -> v.getVin() == vin).get(0);
                    removeVehicle(remove);
                }
                case 10 -> sellLeaseVehicle(s);
                case 0 -> {
                    System.out.println("Exiting program.");
                    complete = true;
                }
            }
        } while (!complete);
        s.close();
    }

    private static void addVehicle(Scanner s) throws IOException {
        int vin = Integer.parseInt(getInputFromConsole("Enter the VIN of the vehicle you are entering.", s));
        int year = Integer.parseInt(getInputFromConsole("Enter the year the vehicle was manufactured.", s));
        String make = getInputFromConsole("Enter the make (manufacturer) of the vehicle.", s);
        String model = getInputFromConsole("Enter the model of the vehicle.", s);
        String vehicleType = getInputFromConsole("Enter the type of vehicle you are entering.", s);
        String color = getInputFromConsole("Enter the color of the vehicle.", s);
        int odometer = Integer.parseInt(getInputFromConsole("Enter the current miles traveled of the vehicle.", s));
        BigDecimal price = new BigDecimal(getInputFromConsole("Enter the current price of the vehicle.", s));
        dealership.addVehicle(new Vehicle(vin, year, make, model, vehicleType, color, odometer, price));
        DealershipFileManager.saveDealership(dealership);
    }

    private static void removeVehicle(Vehicle v) throws IOException {
        dealership.removeVehicle(v);
        DealershipFileManager.saveDealership(dealership);
    }

    private static void sellLeaseVehicle(Scanner s) throws IOException {
        //Initialize contract object
        Contract contract = null;

        //Figure out what kind of contract this is
        String type = getInputFromConsole("Enter if this is a \"SALE\" or a \"LEASE\".", s);
        while (!type.equalsIgnoreCase("sale") && !type.equalsIgnoreCase("lease")) {
            System.out.println("Incorrect input!");
            type = getInputFromConsole("Enter if this is a \"SALE\" or a \"LEASE\".", s);
        }

        //Get the vehicle that's going out and remove it from the dealership
        int vin = Integer.parseInt(getInputFromConsole("Enter the VIN of the vehicle.", s));
        Vehicle vehicle = dealership.getVehicles(v -> v.getVin() == vin).get(0);
        removeVehicle(vehicle);

        //Create the contract
        String name = getInputFromConsole("Enter the name of the customer buying or leasing this vehicle.", s);
        String email = getInputFromConsole("Enter the email of the customer buying or leasing this vehicle.", s);
        switch (type.toLowerCase()) {
            case "sale" -> {
                String financePrompt = getInputFromConsole("Enter \"Y\" if the customer wants financing on this purchase.\nAny other input will be assumed to be a direct sale.", s);
                boolean financed = financePrompt.equalsIgnoreCase("y");
                double apr = vehicle.getPrice().compareTo(BigDecimal.valueOf(10_000)) < 0 ? SMALL_APR : BIG_APR;
                int processing = vehicle.getPrice().compareTo(BigDecimal.valueOf(10_000)) < 0 ? SMALL_PROCESSING_FEE : BIG_PROCESSING_FEE;
                int loanLength = vehicle.getPrice().compareTo(BigDecimal.valueOf(10_000)) < 0 ? LONG_LOAN : SHORT_LOAN;
                contract = new SalesContract(LocalDate.now().toString(), name, email, vehicle, financed, SALES_TAX, apr, RECORDING_FEE, processing, loanLength);
            }
            case "lease" -> contract = new LeaseContract(LocalDate.now().toString(), name, email, vehicle, LEASE_APR, LEASE_LENGTH);
        }

        //TODO: Add contract to the ledger file
        ContractFileManager.addContract(contract);
    }

    private static void init() throws FileNotFoundException {
        dealership = DealershipFileManager.getDealership();
    }

    private static void displayVehicles(List<Vehicle> display) {
        display.forEach(System.out::println);
    }

    private static String getInputFromConsole(String display, Scanner s) {
        System.out.println(display);
        return s.nextLine().trim();
    }
}
