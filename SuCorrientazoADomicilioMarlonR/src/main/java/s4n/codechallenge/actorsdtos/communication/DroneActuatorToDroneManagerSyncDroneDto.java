package s4n.codechallenge.actorsdtos.communication;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.DroneInformationDto;
import s4n.codechallenge.entities.Drone;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DroneActuatorToDroneManagerSyncDroneDto implements DroneManagerDtoCmd {
    private byte id;
    private DroneInformationDto droneInformation;

    public static DroneActuatorToDroneManagerSyncDroneDto toDto(Drone drone) {
        return DroneActuatorToDroneManagerSyncDroneDto.builder()
                .id(drone.getId())
                .droneInformation(DroneInformationDto.toDto(drone.getDroneInformation()))
                .build();
    }
}
