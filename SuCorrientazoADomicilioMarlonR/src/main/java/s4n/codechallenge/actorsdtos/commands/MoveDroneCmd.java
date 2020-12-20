package s4n.codechallenge.actorsdtos.commands;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;

@Generated
@Getter
@Setter
@Builder
public class MoveDroneCmd implements DroneActuatorDtoCmd {
    private byte droneId;
    private DroneInformationCmd droneInformationDto;
    private Boolean shouldDeliverOrder;
    private ActorRef<DroneManagerDtoCmd> replyTo;
}
