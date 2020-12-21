package s4n.codechallenge.entities;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Generated
@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class RouteCoordinates {
    private final Optional<ValueAndCoordinate> beforeOptional;
    private final Optional<ValueAndCoordinate> actualOptional;
    private final Optional<ValueAndCoordinate> nextOptional;
}
