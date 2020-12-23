package s4n.codechallenge.actorsdtos.communication;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;

@Generated
@Getter
@Setter
@Builder
public class MainToFileManagerCmd implements FilesManagerDtoCmd {
    private String[] args;
}
