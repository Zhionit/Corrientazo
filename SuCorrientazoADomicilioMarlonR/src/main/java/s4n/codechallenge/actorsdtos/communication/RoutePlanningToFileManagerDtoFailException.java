package s4n.codechallenge.actorsdtos.communication;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
@Builder
public class RoutePlanningToFileManagerDtoFailException {
    private String errorMessage;
    private byte droneId;
}
