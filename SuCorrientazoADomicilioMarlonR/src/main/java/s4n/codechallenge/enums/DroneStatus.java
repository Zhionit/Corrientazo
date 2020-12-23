package s4n.codechallenge.enums;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
@AllArgsConstructor
public enum DroneStatus {
    AVAILABLE("Available"), BUSY("Busy");

    private String value;

    @Override
    public String toString() {
        return getValue();
    }
}
