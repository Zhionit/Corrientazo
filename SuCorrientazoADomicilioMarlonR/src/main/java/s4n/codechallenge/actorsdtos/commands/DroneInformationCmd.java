package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.dtos.CartesianCoordinateDto;
import s4n.codechallenge.actorsdtos.dtos.OrderDto;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.enums.CardinalDirection;

import java.util.ArrayList;
import java.util.List;


@Generated
@Getter
@Setter
@AllArgsConstructor
public class DroneInformationCmd {
    private List<OrderDto> orders;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinateDto cartesianCoordinate;

    public DroneInformationCmd() {
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinate = new CartesianCoordinateDto();
        this.orders = new ArrayList<>();
    }

    public static DroneInformation toModel(DroneInformationCmd droneInformationCmd) {
        return DroneInformation.builder()
                .cardinalDirection(droneInformationCmd.getCardinalDirection())
                .cartesianCoordinate(CartesianCoordinate.toModel(droneInformationCmd.getCartesianCoordinate()))
                .orders(OrderCmd.toModels(droneInformationCmd.getOrders()))
                .build();
    }
}
