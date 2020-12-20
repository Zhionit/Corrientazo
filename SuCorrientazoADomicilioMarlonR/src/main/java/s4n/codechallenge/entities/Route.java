package s4n.codechallenge.entities;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import s4n.codechallenge.enums.RouteStatus;

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
    private RouteStatus routeStatus;
}
