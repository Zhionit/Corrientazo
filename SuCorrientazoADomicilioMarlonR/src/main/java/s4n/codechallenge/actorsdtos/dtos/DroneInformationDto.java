package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.entities.Order;
import s4n.codechallenge.enums.CardinalDirection;

import java.util.ArrayList;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DroneInformationDto {

    private OrderDto actualOrderDto;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinateDto cartesianCoordinate;

    public DroneInformationDto() {
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinate = new CartesianCoordinateDto();
        this.actualOrderDto = new OrderDto(new CartesianCoordinateDto());
    }

    public static DroneInformationDto toDto(DroneInformation droneInformation) {
        return DroneInformationDto.builder()
                .actualOrderDto(OrderDto.toDto(droneInformation.getOrder()))
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cartesianCoordinate(CartesianCoordinateDto.toDto(droneInformation.getCartesianCoordinate()))
                .build();
    }
}
