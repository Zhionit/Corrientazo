package s4n.codechallenge.services;

import s4n.codechallenge.actorsdtos.dtos.SyncDroneDto;

public interface DroneManager {
    void droneSync(SyncDroneDto droneManagerDtoCmd);

    void deliverOrders();

    void moveDrone();
}
