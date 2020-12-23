package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class MoveDroneDto {
    private DroneDto droneDto;
    private CardinalPointWithDirectionDto cardinalPointWithDirectionDto;
    private Boolean deliveredOrder;
}
