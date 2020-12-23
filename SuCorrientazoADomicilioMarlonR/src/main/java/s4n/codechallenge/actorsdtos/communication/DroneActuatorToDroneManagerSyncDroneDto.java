package s4n.codechallenge.actorsdtos.communication;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.CardinalPointWithDirectionDto;
import s4n.codechallenge.actorsdtos.dtos.DroneDto;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DroneActuatorToDroneManagerSyncDroneDto implements DroneManagerDtoCmd {

    private SyncDroneDto syncDroneDto;

    @Generated
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class SyncDroneDto {
        private int orderId;
        private DroneDto droneDto;
        private CardinalPointWithDirectionDto cardinalPointWithDirectionDto;
    }
}
