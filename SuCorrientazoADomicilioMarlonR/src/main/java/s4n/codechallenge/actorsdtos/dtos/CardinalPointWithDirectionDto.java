package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.CartesianDirection;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CardinalPointWithDirectionDto {
    private CartesianDirection cartesianDirection;
    private CardinalPointDto cardinalPointDto;
}
