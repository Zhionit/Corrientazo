package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class RouteCmd {
    private int routeId;
    private DroneCmd drone;
    private List<DeliveryOrderCmd> deliveryOrdersCmds;
    private List<CardinalPointCmd> cardinalPointCmds;
}
