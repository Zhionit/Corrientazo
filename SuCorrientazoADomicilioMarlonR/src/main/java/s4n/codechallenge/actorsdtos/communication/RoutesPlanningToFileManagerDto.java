package s4n.codechallenge.actorsdtos.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class RoutesPlanningToFileManagerDto implements FilesManagerDtoCmd {
    private int droneId;
    private String deliveryOrderReport;

    public RoutesPlanningToFileManagerDto() {
    }
}
