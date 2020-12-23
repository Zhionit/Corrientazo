package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class RouteCmd {
    private int routeId;
    private DroneCmd drone;
    private Set<DeliveryOrderCmd> deliveryOrdersCmds;
    private Set<CardinalPointWithDirectionCmd> cardinalPointCmds;
}
