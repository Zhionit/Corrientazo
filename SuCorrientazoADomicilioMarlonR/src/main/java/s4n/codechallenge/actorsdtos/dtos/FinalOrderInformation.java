package s4n.codechallenge.actorsdtos.dtos;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.enums.CardinalDirection;

@Generated
@Getter
@Setter
@Builder
public class FinalOrderInformation {
    private byte droneId;
    private byte orderId;
    private CardinalDirection cardinalDirection;
    private CartesianCoordinate cartesianCoordinate;
}
