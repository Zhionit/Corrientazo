package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.enums.CardinalDirection;


@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DroneInformationCmd {
    private NewActualDeliveryOrderCmd actualNewActualDeliveryOrderCmd;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinateCmd cartesianCoordinateCmd;

    public DroneInformationCmd() {
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinateCmd = new CartesianCoordinateCmd();
        this.actualNewActualDeliveryOrderCmd = new NewActualDeliveryOrderCmd();
    }

    public static DroneInformationCmd toModel(DroneInformation droneInformation) {
        return DroneInformationCmd.builder()
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cartesianCoordinateCmd(CartesianCoordinateCmd.toCmd(droneInformation.getCartesianCoordinate()))
                .actualNewActualDeliveryOrderCmd(NewActualDeliveryOrderCmd.toCmd(droneInformation.getDeliveryOrder()))
                .build();
    }
}
