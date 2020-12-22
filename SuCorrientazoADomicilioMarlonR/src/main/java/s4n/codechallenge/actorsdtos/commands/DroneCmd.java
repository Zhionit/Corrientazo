package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.Drone;

@Generated
@Getter
@Setter
@Builder
public class DroneCmd {
    private int droneId;

    public static Drone toModel(DroneCmd droneCmd) {
        return Drone.builder()
                .id(droneCmd.getDroneId())
                .build();
    }
}
