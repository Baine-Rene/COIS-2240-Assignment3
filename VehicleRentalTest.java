import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

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