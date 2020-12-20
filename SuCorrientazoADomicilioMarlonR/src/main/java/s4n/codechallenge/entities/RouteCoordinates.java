package s4n.codechallenge.entities;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Generated
@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class RouteCoordinates {
    private final CartesianCoordinate before;
    private final CartesianCoordinate actual;
    private final CartesianCoordinate next;
}
