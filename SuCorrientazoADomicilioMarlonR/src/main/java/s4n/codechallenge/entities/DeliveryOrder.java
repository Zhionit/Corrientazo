package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.DeliveryOrderStatus;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DeliveryOrder {
    private byte id;
    private DeliveryOrderStatus deliveryOrderStatus;
    private CartesianCoordinate cartesianCoordinateOfDestination;
    private DeliveryOrder nextDeliveryOrder;

    public DeliveryOrder(CartesianCoordinate cartesianCoordinateOfDestination) {
        this.deliveryOrderStatus = DeliveryOrderStatus.UNDELIVERED;
        this.cartesianCoordinateOfDestination = cartesianCoordinateOfDestination;
    }
}
