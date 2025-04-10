import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class TestVehicle extends Vehicle {
    public TestVehicle(String make, String model, int year) {
        super(make, model, year);
    }
}

public class VehicleRentalTest {
    private RentalSystem rentalSystem;
    private Vehicle vehicle;
    private Customer customer;
    
    @BeforeEach
    public void setUp() {
        rentalSystem = RentalSystem.getInstance();
        vehicle = new Car("Toyota", "Camry", 2022, 5);
        vehicle.setLicensePlate("ABC123");
        customer = new Customer(1, "John Doe");
    }
    
    
    @Test
    public void testLicensePlateValidation() {

        assertDoesNotThrow(() -> vehicle.setLicensePlate("AAA100"));
        assertDoesNotThrow(() -> vehicle.setLicensePlate("ABC567"));
        assertDoesNotThrow(() -> vehicle.setLicensePlate("ZZZ999"));
        assertDoesNotThrow(() -> vehicle.setLicensePlate("aaa100")); 
        assertDoesNotThrow(() -> vehicle.setLicensePlate("AbC123")); 
        assertDoesNotThrow(() -> vehicle.setLicensePlate(null)); 
        
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("ZZZ99"));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("123ABC"));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("A1B2C3"));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("ABC12"));
        assertThrows(IllegalArgumentException.class, () -> vehicle.setLicensePlate("ABCDEF"));
        
        vehicle.setLicensePlate("abc123");
        assertEquals("ABC123", vehicle.getLicensePlate());
    }
    
    @Test
    public void testRentAndReturnVehicle() {

        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus());
        
        boolean rentSuccess = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 50.0);
        assertTrue(rentSuccess, "Renting should be successful");
        assertEquals(Vehicle.VehicleStatus.RENTED, vehicle.getStatus(), "Vehicle should be marked as RENTED");
        
        boolean rentAgain = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), 50.0);
        assertFalse(rentAgain, "Renting an already rented vehicle should fail");

        boolean returnSuccess = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
        assertTrue(returnSuccess, "Returning should be successful");
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, vehicle.getStatus(), "Vehicle should be marked as AVAILABLE");
        
        boolean returnAgain = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), 0.0);
        assertFalse(returnAgain, "Returning an already available vehicle should fail");
    }
}