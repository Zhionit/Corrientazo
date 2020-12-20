package s4n.codechallenge.actorsdtos.commands;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.entities.Order;

import java.util.List;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DroneCmd implements DroneActuatorDtoCmd {
    private byte id;
    private List<Order> orders;
    private ActorRef<DroneManagerDtoCmd> replyTo;
}
