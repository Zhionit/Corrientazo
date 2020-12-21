package s4n.codechallenge.actorsdtos.communication;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;

import java.util.List;

@Generated
@Getter
@Setter
@Builder
public class FileManagerToRoutePlanningCmd implements RoutePlanningDtoCmd{
    private List<String> encodedOrders;
    private String fileName;
}
