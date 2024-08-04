package org.example.parkinglot.models.vehicle;

import java.util.Objects;

public abstract class AbstractVehicle implements Vehicle {
    private final String id;

    protected AbstractVehicle(String id) {
        if (id==null || id.trim().isEmpty()) {
            throw new RuntimeException();
        }
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractVehicle that = (AbstractVehicle) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
