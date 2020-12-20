package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import s4n.codechallenge.entities.DroneInformation;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DroneDto {
    private byte id;
    private DroneInformationDto droneInformation;

    public static DroneInformationDto toDto(DroneInformation droneInformation) {
        return DroneInformationDto.builder()
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cartesianCoordinate(CartesianCoordinateDto.toDto(droneInformation.getCartesianCoordinate()))
                .orders(OrderDto.toDtos(droneInformation.getOrders()))
                .build();
    }
}
