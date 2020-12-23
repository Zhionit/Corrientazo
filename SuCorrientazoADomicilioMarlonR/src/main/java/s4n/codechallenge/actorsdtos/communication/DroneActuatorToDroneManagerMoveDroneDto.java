package s4n.codechallenge.actorsdtos.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto.SyncDroneDto;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DroneActuatorToDroneManagerMoveDroneDto implements DroneManagerDtoCmd {
    private SyncDroneDto moveDroneDto;
}
