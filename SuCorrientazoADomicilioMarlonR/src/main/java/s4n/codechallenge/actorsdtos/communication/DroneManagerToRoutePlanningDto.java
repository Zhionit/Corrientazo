package s4n.codechallenge.actorsdtos.communication;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.DroneDto;
import s4n.codechallenge.actorsdtos.dtos.RoutesDto;

@Generated
@Getter
@Setter
@Builder
public class DroneManagerToRoutePlanningDto implements RoutePlanningDtoCmd {
    private DroneDto droneDto;
    private RoutesDto routesDto;
}
