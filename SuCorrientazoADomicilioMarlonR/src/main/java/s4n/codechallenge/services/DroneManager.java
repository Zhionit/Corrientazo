package s4n.codechallenge.services;

import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;

public interface DroneManager {
    void droneSync(DroneManagerDtoCmd droneManagerDtoCmd);

    void deliverOrders();

    void moveDrone();
}
