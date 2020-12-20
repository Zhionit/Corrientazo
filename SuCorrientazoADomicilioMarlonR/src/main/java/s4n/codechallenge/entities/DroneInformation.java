package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.CardinalDirection;
import s4n.codechallenge.enums.DroneStatus;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DroneInformation {

    private DroneStatus droneStatus;
    private DeliveryOrder deliveryOrder;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinate cartesianCoordinate;

    public DroneInformation() {
        this.droneStatus = s4n.codechallenge.enums.DroneStatus.AVAILABLE;
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinate = new CartesianCoordinate();
        this.deliveryOrder = new DeliveryOrder(new CartesianCoordinate());
    }
}
