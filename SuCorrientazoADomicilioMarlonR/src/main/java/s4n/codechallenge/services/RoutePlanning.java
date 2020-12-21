package s4n.codechallenge.services;

import akka.actor.typed.Behavior;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningDto;
import s4n.codechallenge.actorsdtos.communication.FileManagerToRoutePlanningCmd;

import java.util.List;

public interface RoutePlanning {

    Behavior<RoutePlanningDtoCmd> extractDrone(FileManagerToRoutePlanningCmd fileManagerToRoutePlanningCmd);

    void validateDrone(byte amountsOrders, int ordersAmount);

    void generateDroneRoute(List<String> encodedOrders);

    void sendInformationToDroneManager();

    Behavior<RoutePlanningDtoCmd> respondToParentActuator(DroneManagerToRoutePlanningDto droneManagerToRoutePlanningDto);
}
