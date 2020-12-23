package s4n.codechallenge.services;

import akka.actor.typed.Behavior;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningContainerDto;
import s4n.codechallenge.actorsdtos.communication.FileManagerToRoutePlanningCmd;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToFileManagerDto;
import s4n.codechallenge.entities.CardinalPointWithDirection;
import s4n.codechallenge.entities.DeliveryOrder;
import s4n.codechallenge.entities.RoutePlanningIndexes;

import java.util.Map;
import java.util.Set;

public interface RoutePlanning {

    void validateDrone(byte amountsOrders, int ordersAmount);

    void generateDroneRoute(Set<String> encodedOrders, RoutePlanningIndexes routePlanningIndexes);

    Behavior<RoutePlanningDtoCmd> listenParentCall(FileManagerToRoutePlanningCmd fileManagerToRoutePlanningCmd);

    Behavior<RoutePlanningDtoCmd> respondToParentActuator(RoutesPlanningToFileManagerDto routesPlanningToFileManagerDto);

    Behavior<RoutePlanningDtoCmd> listenChildCall(DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto);

    void callChild(Map<DeliveryOrder, Set<CardinalPointWithDirection>> cardinalPoints, Map<DeliveryOrder, CardinalPointWithDirection> deliveryPoints);
}
