package s4n.codechallenge.actorsdtos.dtos;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.CartesianDirection;

@Generated
@Getter
@Setter
@Builder
public class OrderDto {
    private int orderId;
    private CardinalPointDto finalCardinalPointDto;
    private CartesianDirection finalCartesianDirection;
}
