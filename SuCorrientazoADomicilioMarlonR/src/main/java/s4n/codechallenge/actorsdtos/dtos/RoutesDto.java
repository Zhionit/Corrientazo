package s4n.codechallenge.actorsdtos.dtos;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;

@Generated
@Getter
@Setter
@Builder
public class RoutesDto {
    private byte id;
    private DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto;
    private DeliveryOrderAndCoordinateDto deliveryOrderAndCoordinateDto;
}
