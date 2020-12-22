package s4n.codechallenge.actorsdtos.communication;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;

@Generated
@Getter
@Setter
public class RoutePlanningToFileManagerDtoFailException extends RoutesPlanningToFileManagerDto implements FilesManagerDtoCmd {
    private String errorMessage;

    public RoutePlanningToFileManagerDtoFailException() {
        super();
    }
}
