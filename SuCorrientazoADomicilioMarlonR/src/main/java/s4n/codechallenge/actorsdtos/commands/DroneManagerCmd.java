package s4n.codechallenge.actorsdtos.commands;

import akka.actor.typed.ActorRef;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.DeliveryOrderAndCoordinateDto;
import s4n.codechallenge.entities.Drone;
import s4n.codechallenge.services.RoutePlanning;

import java.util.List;

public class DroneManagerCmd implements DroneManagerDtoCmd {
    private List<Drone> drones;
    private DeliveryOrderAndCoordinateDto deliveryOrderAndCoordinateDto;
    private ActorRef<RoutePlanning> replyTo;
}
