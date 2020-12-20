package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;

@Generated
@Getter
@Setter
@Builder
public class MoveDroneCmd implements DroneActuatorDtoCmd {
    private byte id;
    private DroneInformationCmd droneInformationDto;
    private Boolean shouldDeliverOrder;
}
