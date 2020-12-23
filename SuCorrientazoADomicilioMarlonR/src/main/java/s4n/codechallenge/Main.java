package s4n.codechallenge;

import akka.actor.typed.ActorSystem;
import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;
import s4n.codechallenge.actorsdtos.communication.MainToFileManagerCmd;
import s4n.codechallenge.services.impl.FilesManagerImpl;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        final ActorSystem<FilesManagerDtoCmd> routePlanningDtoCmdActorSystem =
                ActorSystem.create(FilesManagerImpl.create(), "FilesActuator");

        try {
            MainToFileManagerCmd mainToFileManagerCmd = MainToFileManagerCmd.builder().build();
            routePlanningDtoCmdActorSystem.tell(mainToFileManagerCmd);
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ignored) {
        } finally {
            routePlanningDtoCmdActorSystem.terminate();
        }
    }
}
