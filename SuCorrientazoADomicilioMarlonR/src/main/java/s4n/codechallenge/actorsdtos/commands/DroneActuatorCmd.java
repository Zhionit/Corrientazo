package s4n.codechallenge.actorsdtos.commands;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.entities.DeliveryOrder;

import java.util.List;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DroneActuatorCmd implements DroneActuatorDtoCmd {
    private byte id;
    private List<DeliveryOrder> deliveryOrders;
    private ActorRef<DroneManagerDtoCmd> replyTo;
}
