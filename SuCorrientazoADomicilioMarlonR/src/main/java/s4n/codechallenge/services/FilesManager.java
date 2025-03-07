package s4n.codechallenge.services;

import akka.actor.typed.Behavior;
import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;
import s4n.codechallenge.actorsdtos.communication.MainToFileManagerCmd;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToFileManagerDto;

import java.nio.file.Path;

public interface FilesManager {

    void readFiles(String fileName);

    void writeFiles(Path path, byte[] dataBytes);

    void callChildActuator(String data, String fileName);

    Behavior<FilesManagerDtoCmd> listenChildCall(RoutesPlanningToFileManagerDto routesPlanningToFileManagerDto);

    Behavior<FilesManagerDtoCmd> listenMainCall(MainToFileManagerCmd filesManagerCmd);
}
