package s4n.codechallenge.actorsdtos.commands;

import akka.actor.typed.ActorRef;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.dtos.OrderAndCoordinateDto;
import s4n.codechallenge.entities.Drone;
import s4n.codechallenge.services.RoutePlanning;

import java.util.List;

public class DroneManagerCmd implements DroneManagerDtoCmd {
    private List<Drone> drones;
    private OrderAndCoordinateDto orderAndCoordinateDto;
    private ActorRef<RoutePlanning> replyTo;
}
