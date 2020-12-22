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
    private CardinalPointDto cardinalPointDto;

    public DroneInformationDto() {
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cardinalPointDto = new CardinalPointDto();
        this.actualDeliveryOrderDto = new DeliveryOrderDto(new CardinalPointDto());
    }

    public static DroneInformationDto toDto(DroneInformation droneInformation) {
        return DroneInformationDto.builder()
                .actualDeliveryOrderDto(droneInformation.getDeliveryOrder().isPresent() ? DeliveryOrderDto.toDto(droneInformation.getDeliveryOrder().get()) : null)
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cardinalPointDto(CardinalPointDto.toDto(droneInformation.getCartesianCoordinate()))
                .build();
    }
}
