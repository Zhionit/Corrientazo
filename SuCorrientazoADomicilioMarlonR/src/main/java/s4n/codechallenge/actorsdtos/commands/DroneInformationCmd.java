package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.enums.CardinalDirection;

import java.util.Optional;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DroneInformationCmd {
    private byte droneId;
    private Optional<NewDeliveryOrderCmd> newDeliveryOrderCmd;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinateCmd cartesianCoordinateCmd;

    public DroneInformationCmd() {
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinateCmd = CartesianCoordinateCmd.builder().build();
        this.newDeliveryOrderCmd = Optional.of(NewDeliveryOrderCmd.builder().build());
    }

    public static DroneInformationCmd toCmd(DroneInformation droneInformation) {
        return DroneInformationCmd.builder()
                .droneId(droneInformation.getDroneId())
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cartesianCoordinateCmd(CartesianCoordinateCmd.toCmd(droneInformation.getCartesianCoordinate()))
                .newDeliveryOrderCmd(NewDeliveryOrderCmd.toCmd(droneInformation.getDeliveryOrder()))
                .build();
    }
}
