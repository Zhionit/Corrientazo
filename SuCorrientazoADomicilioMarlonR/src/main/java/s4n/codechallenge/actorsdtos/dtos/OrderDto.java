package s4n.codechallenge.actorsdtos.dtos;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.CardinalDirection;

@Generated
@Getter
@Setter
@Builder
public class OrderDto {
    private byte orderId;
    private CartesianCoordinateDto finalCartesianCoordinateDto;
    private CardinalDirection finalCardinalDirectionDto;
}
