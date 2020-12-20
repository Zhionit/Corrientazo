package s4n.codechallenge.entities;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.OrderStatus;

@Generated
@Setter
@Getter
@Builder
public class Order {
    private byte id;
    private OrderStatus orderStatus;
    private CartesianCoordinate cartesianCoordinateOfDestination;

    public Order(CartesianCoordinate cartesianCoordinateOfDestination) {
        this.orderStatus = OrderStatus.UNDELIVERED;
        this.cartesianCoordinateOfDestination = cartesianCoordinateOfDestination;
    }
}
