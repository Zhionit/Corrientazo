package s4n.codechallenge.actorsdtos.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.CartesianCoordinateDto;
import s4n.codechallenge.actorsdtos.dtos.DeliveryOrderDto;
import s4n.codechallenge.actorsdtos.dtos.DroneInformationDto;
import s4n.codechallenge.entities.DroneInformation;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DroneActuatorToDroneManagerMoveDroneDto implements DroneManagerDtoCmd {
    private byte id;
    private DroneInformationDto droneInformation;

    public static DroneInformationDto toDto(DroneInformation droneInformation) {
        return DroneInformationDto.builder()
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cartesianCoordinateDto(CartesianCoordinateDto.toDto(droneInformation.getCartesianCoordinate()))
                .actualDeliveryOrderDto(droneInformation.getDeliveryOrder().isPresent() ? DeliveryOrderDto.toDto(droneInformation.getDeliveryOrder().get()) : null)
                .build();
    }
}
