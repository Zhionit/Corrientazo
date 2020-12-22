package s4n.codechallenge.services;

import akka.actor.typed.Behavior;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerMoveDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningContainerDto;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToDroneManagerCmd;
import s4n.codechallenge.entities.CardinalPointWithDirection;
import s4n.codechallenge.entities.Drone;

public interface DroneManager {


    Behavior<DroneManagerDtoCmd> respondToParentActuator(DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto);

    Behavior<DroneManagerDtoCmd> listenParentCall(RoutesPlanningToDroneManagerCmd routesPlanningToDroneManagerCmd);

    Behavior<DroneManagerDtoCmd> syncListenChildCall(DroneActuatorToDroneManagerSyncDroneDto droneManagerDtoCmd);

    Behavior<DroneManagerDtoCmd> moveListenChildCall(DroneActuatorToDroneManagerMoveDroneDto droneActuatorToDroneManagerMoveDroneDto);

    void syncCallChild(Drone drone);

    void moveCallChild(Drone drone, Boolean shouldDeliverOrder, CardinalPointWithDirection cardinalPointWithDirection);
}
