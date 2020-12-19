package s4n.codechallenge.entities;

import lombok.*;
import s4n.codechallenge.dtos.DroneInformationDto;
import s4n.codechallenge.enums.CardinalDirection;
import s4n.codechallenge.enums.DroneStatus;

import java.util.ArrayList;
import java.util.List;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DroneInformation {

    private DroneStatus droneStatus;
    private List<Order> orders;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinate cartesianCoordinate;

    public DroneInformation() {
        this.droneStatus = s4n.codechallenge.enums.DroneStatus.AVAILABLE;
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinate = new CartesianCoordinate();
        this.orders = new ArrayList<>();
    }

    public DroneInformation toModel(DroneInformationDto droneInformationDto) {
        return DroneInformation.builder()
                .cardinalDirection(droneInformationDto.getCardinalDirection())
                .cartesianCoordinate(CartesianCoordinate.toModel(droneInformationDto.getCartesianCoordinate()))
                .orders(Order.toModels(droneInformationDto.getOrders()))
                .droneStatus(droneInformationDto.getDroneStatus())
                .build();
    }
}
