import java.util.List;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
	
	private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    
    
    public RentalSystem() {
        this.vehicles = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.rentalHistory = new RentalHistory();
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
    }
    
    public static synchronized RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    private void saveVehicle(Vehicle vehicle) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("vehicles.txt", true))) {
            String type = vehicle instanceof Car ? "Car" : "Motorcycle";
            pw.printf("%s,%s,%s,%s,%d,%s%n",
                    type, vehicle.getLicensePlate(), vehicle.getMake(),
                    vehicle.getModel(), vehicle.getYear(), vehicle.getStatus());
        } catch (IOException e) {
            System.err.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("customers.txt", true))) {
            pw.printf("%d,%s%n", customer.getCustomerId(), customer.getCustomerName());
        } catch (IOException e) {
            System.err.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("rental_records.txt", true))) {
            pw.printf("%s,%d,%s,%.2f,%s%n",
                    record.getVehicle().getLicensePlate(), record.getCustomer().getCustomerId());
        } catch (IOException e) {
            System.err.println("Error saving rental record: " + e.getMessage());
        }
    }

    private void loadVehicles() {
        File file = new File("vehicles.txt");
        if (!file.exists()) return;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) {
                    String type = parts[0];
                    String licensePlate = parts[1];
                    String make = parts[2];
                    String model = parts[3];
                    int year = Integer.parseInt(parts[4]);
                    Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[5]);
                    
                    Vehicle vehicle = type.equals("Car") ? new Car(licensePlate, make, model, year, status)
                                                           : new Motorcycle(licensePlate, make, model, year, status);
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading vehicles.txt: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        File file = new File("customers.txt");
        if (!file.exists()) return;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    customers.add(new Customer(id, name));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading customers.txt: " + e.getMessage());
        }
    }

    private void loadRentalRecords() {
        File file = new File("rental_records.txt");
        if (!file.exists()) return;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String plate = parts[0];
                    int customerId = Integer.parseInt(parts[1]);
                    LocalDate date = LocalDate.parse(parts[2]);
                    double amount = Double.parseDouble(parts[3]);
                    String type = parts[4];
                    
                    Vehicle vehicle = findVehicleByPlate1(plate);
                    Customer customer = findCustomerById1(customerId);
                    if (vehicle != null && customer != null) {
                        rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, type));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading rental_records.txt: " + e.getMessage());
        }
    }

    public Vehicle findVehicleByPlate1(String plate) {
        return vehicles.stream().filter(v -> v.getLicensePlate().equalsIgnoreCase(plate)).findFirst().orElse(null);
    }

    public Customer findCustomerById1(int id) {
        return customers.stream().filter(c -> c.getCustomerId() == id).findFirst().orElse(null);
    }


    public void displayAvailableVehicles() {
    	System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
    	System.out.println("---------------------------------------------------------------------------------");
    	 
        for (Vehicle v : vehicles) {
            if (v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
            }
        }
        System.out.println();
    }
    
    public void displayAllVehicles() {
        for (Vehicle v : vehicles) {
            System.out.println("  " + v.getInfo());
        }
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }

    public Customer findCustomerByName(String name) {
        for (Customer c : customers)
            if (c.getCustomerName().equalsIgnoreCase(name))
                return c;
        return null;
    }
}