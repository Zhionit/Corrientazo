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
    private final Drone drone;
    private final List<DeliveryOrder> deliveryOrders;
    private final List<RouteCoordinates> routeCoordinates;
}
