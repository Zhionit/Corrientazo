package s4n.codechallenge.actorsdtos.communication;

import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.DroneDto;

import java.util.List;

public class RoutePlanningToFileManagerDto implements FilesManagerDtoCmd {

    private DroneDto droneDto;
    private List<String> deliveryReportString;
}
