package s4n.codechallenge.actorsdtos.communication;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.commands.DeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;
import s4n.codechallenge.actorsdtos.commands.RouteCoordinatesCmd;
import s4n.codechallenge.services.RoutePlanning;

import java.util.List;

@Generated
@Getter
@Setter
@Builder
public class PlanningRoutesToDroneManagerCmd implements DroneManagerDtoCmd {
    private final DroneCmd droneCmd;
    private final List<DeliveryOrderCmd> deliveryOrdersCmds;
    private final List<RouteCoordinatesCmd> routesCoordinates;
    private final ActorRef<RoutePlanning> replyTo;
}
