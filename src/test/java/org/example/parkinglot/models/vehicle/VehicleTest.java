package org.example.parkinglot.models.vehicle;

import org.example.parkinglot.models.Size;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {

    private static Stream<Arguments> vehicleAndTheirSizeCombinations() {
        return Stream.of(
                Arguments.of(new Car("1"), Size.MEDIUM),
                Arguments.of(new Bike("2"), Size.SMALL),
                Arguments.of(new Truck("3"), Size.LARGE)
        );
    }

    @ParameterizedTest
    @MethodSource(value = "vehicleAndTheirSizeCombinations")
    public void testVehicleSize(Vehicle vehicle, Size size) {
        assertEquals(vehicle.getSize(), size);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    public void vehicleCreationWithEmptyIdShouldThrow(String id) {
        assertThrows(RuntimeException.class, () -> new Car(id));
        assertThrows(RuntimeException.class, () -> new Bike(id));
        assertThrows(RuntimeException.class, () -> new Truck(id));
    }

    @Test
    public void vehicleCreationWithNullIdShouldThrow() {
        assertThrows(RuntimeException.class, () -> new Car(null));
        assertThrows(RuntimeException.class, () -> new Bike(null));
        assertThrows(RuntimeException.class, () -> new Truck(null));
    }

    @ParameterizedTest
    @MethodSource(value = "differentVehicleTypeWithSameId")
    public void differentVehicleTypeWithSameIdAreNotEqual(Vehicle vehicle1, Vehicle vehicle2) {
        assertFalse(vehicle1.equals(vehicle2));
    }

    private static Stream<Arguments> differentVehicleTypeWithSameId() {
        return Stream.of(
                Arguments.of(new Car("1"), new Truck("1")),
                Arguments.of(new Car("1"), new Bike("1")),
                Arguments.of(new Bike("1"), new Truck("1")),
                Arguments.of(new Bike("1"), new Car("1")),
                Arguments.of(new Truck("1"), new Car("1")),
                Arguments.of(new Truck("1"), new Bike("1"))
        );
    }

    @Test
    public void sameVehicleTypeWithSameIdIsEqual() {
        assertTrue(new Car("1").equals(new Car("1")));
    }

}
