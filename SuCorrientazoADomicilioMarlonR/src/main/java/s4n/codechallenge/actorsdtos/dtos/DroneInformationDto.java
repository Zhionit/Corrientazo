package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.enums.CardinalDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DroneInformationDto {

    private List<OrderDto> orders;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinateDto cartesianCoordinate;

    public DroneInformationDto() {
        this.cardinalDirection = CardinalDirection.NORTE;
        this.cartesianCoordinate = new CartesianCoordinateDto();
        this.orders = new ArrayList<>();
    }

    public static DroneInformationDto toDto(DroneInformation droneInformation) {
        return DroneInformationDto.builder()
                .orders(OrderDto.toDtos(droneInformation.getOrders()))
                .cardinalDirection(droneInformation.getCardinalDirection())
                .cartesianCoordinate(CartesianCoordinateDto.toDto(droneInformation.getCartesianCoordinate()))
                .build();
    }
}
