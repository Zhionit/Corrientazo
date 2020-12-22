package s4n.codechallenge.actorsdtos.communication;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.DroneDto;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DroneActuatorToDroneManagerSyncDroneDto implements DroneManagerDtoCmd {
    private DroneDto droneDto;
}
