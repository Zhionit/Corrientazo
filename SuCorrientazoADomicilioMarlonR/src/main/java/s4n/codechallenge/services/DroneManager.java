package s4n.codechallenge.services;

import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.entities.Drone;

public interface DroneManager extends MainActuator {
    void droneSync(DroneActuatorToDroneManagerSyncDroneDto droneManagerDtoCmd);

    void deliverOrders();

    void moveDroneInDroneActuator(Drone drone);
}
