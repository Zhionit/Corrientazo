package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.CardinalDirection;
import s4n.codechallenge.enums.DroneStatus;

import java.util.Optional;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DroneInformation {

    private byte droneId;
    private DroneStatus droneStatus;
    private Optional<DeliveryOrder> deliveryOrder;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinate cartesianCoordinate;
    private Boolean shouldDeliverOrder;

    public DroneInformation() {
        this.droneId = 0;
        this.droneStatus = DroneStatus.AVAILABLE;
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinate = new CartesianCoordinate();
        this.deliveryOrder = Optional.of(new DeliveryOrder(new CartesianCoordinate()));
    }
}
