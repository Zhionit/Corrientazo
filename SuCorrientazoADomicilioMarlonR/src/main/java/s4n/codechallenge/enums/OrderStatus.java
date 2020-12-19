package s4n.codechallenge.enums;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
@AllArgsConstructor
public enum OrderStatus {
    DELIVERED("Delivered"), UNDELIVERED("No entregado");

    String value;

    @Override
    public String toString() {
        return getValue();
    }
}
