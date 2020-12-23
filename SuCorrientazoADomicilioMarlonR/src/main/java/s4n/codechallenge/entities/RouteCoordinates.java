package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RouteCoordinates {
    private CardinalPointWithDirection cardinalPointWithDirection;
    private Optional<RouteCoordinates> nextOptional;
}
