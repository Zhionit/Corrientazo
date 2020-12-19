package s4n.codechallenge.enums;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
@AllArgsConstructor
public enum CardinalDirection {

    NORTE("Norte"), SUR("Sur"), ORIENTE("Oriente"), OCCIDENTE("Occidente");

    private String value;

    @Override
    public String toString() {
        return getValue();
    }
}
