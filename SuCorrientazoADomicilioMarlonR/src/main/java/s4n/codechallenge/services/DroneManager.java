package s4n.codechallenge.services;

import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.entities.DroneInformation;

public interface DroneManager {
    void droneSync(DroneActuatorToDroneManagerSyncDroneDto droneManagerDtoCmd);

    void deliverOrders();

    void moveDroneInDroneActuator(DroneInformation newActualDroneInformation);
}
