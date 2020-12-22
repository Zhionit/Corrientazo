package s4n.codechallenge.actorsdtos.communication;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.commands.CardinalPointWithDirectionCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DroneManagerToDroneActuatorMoveCmd implements DroneActuatorDtoCmd {
    private MoveDroneCmd moveDroneCmd;
    private ActorRef<DroneManagerDtoCmd> replyTo;

    @Generated
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public class MoveDroneCmd {
        private DroneCmd droneCmd;
        private CardinalPointWithDirectionCmd cardinalPointWithDirectionCmd;
        private Boolean shouldDeliverOrder;
    }
}
