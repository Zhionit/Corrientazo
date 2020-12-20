package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.entities.DroneInformation;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class MoveDroneDto implements DroneManagerDtoCmd {
    private byte id;
    private DroneInformationDto droneInformation;

    public static DroneInformationDto toDto(DroneInformation droneInformation) {
        return DroneInformationDto.builder()
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cartesianCoordinate(CartesianCoordinateDto.toDto(droneInformation.getCartesianCoordinate()))
                .actualOrderDto(OrderDto.toDto(droneInformation.getOrder()))
                .build();
    }
}
