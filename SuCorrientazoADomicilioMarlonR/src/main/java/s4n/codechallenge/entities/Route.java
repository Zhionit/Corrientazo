package s4n.codechallenge.entities;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Generated
@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class Route {
    private Drone drone;
    private List<Order> orders;
    private List<RouteCoordinates> routeCoordinates;
}
