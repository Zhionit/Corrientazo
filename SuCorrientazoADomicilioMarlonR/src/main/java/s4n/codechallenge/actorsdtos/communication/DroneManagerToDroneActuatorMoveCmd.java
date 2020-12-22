package s4n.codechallenge.actorsdtos.communication;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;

@Generated
@Getter
@Setter
@Builder
public class DroneManagerToDroneActuatorMoveCmd implements DroneActuatorDtoCmd {
    private DroneCmd droneCmd;
    private Boolean shouldDeliverOrder;
    private ActorRef<DroneManagerDtoCmd> replyTo;
}
