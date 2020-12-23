package s4n.codechallenge.actorsdtos.communication;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;

import java.util.List;

@Generated
@Getter
@Setter
@AllArgsConstructor
public class DroneManagerToRoutePlanningContainerDto implements RoutePlanningDtoCmd {
    List<DroneManagerToRoutePlanningDto> droneManagerToRoutePlanningDtos;
}
