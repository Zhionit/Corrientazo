package s4n.codechallenge.actorsdtos.communication;

import akka.actor.typed.ActorRef;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;

import java.util.Set;

@Generated
@Getter
@Setter
@Builder
public class FileManagerToRoutePlanningCmd implements RoutePlanningDtoCmd {
    private Set<String> encodedOrders;
    private String fileName;
    private ActorRef<FilesManagerDtoCmd> replyTo;
}
