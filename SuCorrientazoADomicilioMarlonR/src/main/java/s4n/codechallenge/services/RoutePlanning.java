package s4n.codechallenge.services;

import akka.actor.typed.Behavior;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.communication.FileManagerToRoutePlanningCmd;
import s4n.codechallenge.entities.RoutePlanningIndexes;

import java.util.List;

public interface RoutePlanning extends MainActuator {

    Behavior<RoutePlanningDtoCmd> extractDrone(FileManagerToRoutePlanningCmd fileManagerToRoutePlanningCmd);

    void validateDrone(byte amountsOrders, int ordersAmount);

    void generateDroneRoute(List<String> encodedOrders, RoutePlanningIndexes routePlanningIndexes);

    void sendInformationToDroneManager();
}
