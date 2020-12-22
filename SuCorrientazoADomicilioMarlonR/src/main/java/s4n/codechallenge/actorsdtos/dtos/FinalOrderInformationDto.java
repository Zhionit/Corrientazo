package s4n.codechallenge.actorsdtos.dtos;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPoint;
import s4n.codechallenge.enums.CardinalDirection;

@Generated
@Getter
@Setter
@Builder
public class FinalOrderInformationDto {
    private int droneId;
    private int orderId;
    private CardinalDirection cardinalDirection;
    private CardinalPoint cardinalPoint;
}
