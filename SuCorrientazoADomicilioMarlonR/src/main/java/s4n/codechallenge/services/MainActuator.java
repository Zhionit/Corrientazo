package s4n.codechallenge.services;

import akka.actor.typed.Behavior;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.GeneralActuatorDtoCmd;

public interface MainActuator {
    Behavior<RoutePlanningDtoCmd> respondToParentActuator(GeneralActuatorDtoCmd generalActuatorDtoCmd);

    Behavior<RoutePlanningDtoCmd> listenChildCall(GeneralActuatorDtoCmd generalActuatorDtoCmd);
}
