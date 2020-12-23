package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import s4n.codechallenge.enums.RouteStatus;

import java.util.Set;

@Generated
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Route {
    private int routeId;
    private Drone drone;
    private Set<DeliveryOrder> deliveryOrders;
    private Set<CardinalPoint> cardinalPoints;
    private RouteStatus routeStatus;
}
