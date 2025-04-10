import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

//Subclass for testing
class TestVehicle extends Vehicle {
    public TestVehicle(String make, String model, int year) {
        super(make, model, year);
    }
}

public class VehicleRentalTest {
    private Vehicle vehicle;
    
    @BeforeEach
    public void setUp() {
        vehicle = new TestVehicle("Toyota", "Camry", 2020);
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
    
}