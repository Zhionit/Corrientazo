package s4n.codechallenge.actorsdtos.communication;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.commands.RouteCmd;

@Generated
@Getter
@Setter
@Builder
public class RoutesPlanningToDroneManagerCmd implements DroneManagerDtoCmd {
    private final RouteCmd routeCmd;
    private final ActorRef<RoutePlanningDtoCmd> replyTo;
}
