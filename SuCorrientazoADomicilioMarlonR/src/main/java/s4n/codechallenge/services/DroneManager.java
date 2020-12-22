package s4n.codechallenge.services;

import akka.actor.typed.Behavior;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerMoveDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorSyncCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningContainerDto;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToDroneManagerCmd;
import s4n.codechallenge.entities.Drone;

public interface DroneManager {

    void moveDroneInDroneActuator(Drone drone);

    Behavior<DroneManagerDtoCmd> respondToParentActuator(DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto);

    Behavior<DroneManagerDtoCmd> listenParentCall(RoutesPlanningToDroneManagerCmd routesPlanningToDroneManagerCmd);

    Behavior<DroneManagerDtoCmd> syncListenChildCall(DroneActuatorToDroneManagerSyncDroneDto droneManagerDtoCmd);

    Behavior<DroneManagerDtoCmd> moveListenChildCall(DroneActuatorToDroneManagerMoveDroneDto droneActuatorToDroneManagerMoveDroneDto);

    Behavior<DroneManagerDtoCmd> syncCallChild(DroneManagerToDroneActuatorSyncCmd droneManagerToDroneActuatorSyncCmd);

    Behavior<DroneManagerDtoCmd> moveCallChild(DroneManagerToDroneActuatorMoveCmd droneManagerToDroneActuatorMoveCmd);
}
