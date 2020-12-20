package s4n.codechallenge.actorsdtos.dtos;

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
public class DroneInformationDto {

    private DeliveryOrderDto actualDeliveryOrderDto;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinateDto cartesianCoordinateDto;

    public DroneInformationDto() {
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinateDto = new CartesianCoordinateDto();
        this.actualDeliveryOrderDto = new DeliveryOrderDto(new CartesianCoordinateDto());
    }

    public static DroneInformationDto toDto(DroneInformation droneInformation) {
        return DroneInformationDto.builder()
                .actualDeliveryOrderDto(droneInformation.getDeliveryOrder().isPresent() ? DeliveryOrderDto.toDto(droneInformation.getDeliveryOrder().get()) : null)
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cartesianCoordinateDto(CartesianCoordinateDto.toDto(droneInformation.getCartesianCoordinate()))
                .build();
    }
}
