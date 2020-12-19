package s4n.codechallenge.dtos;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.Order;
import s4n.codechallenge.enums.CardinalDirection;
import s4n.codechallenge.enums.DroneStatus;

import java.util.ArrayList;
import java.util.List;

@Generated
@Getter
@Setter
@AllArgsConstructor
public class DroneInformationDto {

    private DroneStatus droneStatus;
    private List<OrderDto> orders;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinateDto cartesianCoordinate;

    public DroneInformationDto() {
        this.droneStatus = DroneStatus.AVAILABLE;
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinate = new CartesianCoordinateDto();
        this.orders = new ArrayList<>();
    }
}
