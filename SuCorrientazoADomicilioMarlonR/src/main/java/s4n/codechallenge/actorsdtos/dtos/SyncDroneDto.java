package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.entities.Drone;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class SyncDroneDto implements DroneManagerDtoCmd {
    private byte id;
    private DroneInformationDto droneInformation;

    public static SyncDroneDto toDto(Drone drone) {
        return SyncDroneDto.builder()
                .id(drone.getId())
                .droneInformation(DroneInformationDto.toDto(drone.getDroneInformation()))
                .build();
    }
}
