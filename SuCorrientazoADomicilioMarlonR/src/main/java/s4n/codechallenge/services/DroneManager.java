package s4n.codechallenge.services;

import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.MoveDroneDto;
import s4n.codechallenge.actorsdtos.dtos.SyncDroneDto;

public interface DroneManager {
    void droneSync(SyncDroneDto droneManagerDtoCmd);

    void deliverOrders();

    DroneManagerDtoCmd moveDrone(MoveDroneDto droneManagerDtoCmd);
}
