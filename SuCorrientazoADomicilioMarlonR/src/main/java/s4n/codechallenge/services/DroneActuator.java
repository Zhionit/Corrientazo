package s4n.codechallenge.services;

import akka.actor.typed.Behavior;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerMoveDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorSyncCmd;

public interface DroneActuator {

    Behavior<DroneManagerDtoCmd> syncRespondToParentActuator(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto);

    Behavior<DroneManagerDtoCmd> moveRespondToParentActuator(DroneActuatorToDroneManagerMoveDroneDto droneActuatorToDroneManagerMoveDroneDto);

    Behavior<DroneActuatorDtoCmd> syncListenParentActuator(DroneManagerToDroneActuatorSyncCmd droneManagerToDroneActuatorSyncCmd);

    Behavior<DroneActuatorDtoCmd> moveListenParentActuator(DroneManagerToDroneActuatorMoveCmd droneManagerToDroneActuatorMoveCmd);
}
