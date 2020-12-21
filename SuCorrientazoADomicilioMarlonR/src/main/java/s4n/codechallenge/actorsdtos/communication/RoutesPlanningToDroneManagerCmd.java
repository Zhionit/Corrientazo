package s4n.codechallenge.actorsdtos.communication;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.commands.DeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;
import s4n.codechallenge.actorsdtos.commands.RouteCoordinatesCmd;

import java.util.List;

@Generated
@Getter
@Setter
@Builder
public class RoutesPlanningToDroneManagerCmd implements DroneManagerDtoCmd {
    private final byte routeId;
    private final DroneCmd droneCmd;
    private final List<DeliveryOrderCmd> deliveryOrdersCmds;
    private final List<RouteCoordinatesCmd> routesCoordinates;
    private final ActorRef<RoutePlanningDtoCmd> replyTo;
}
