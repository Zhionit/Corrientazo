package s4n.codechallenge.services.impl;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.commands.DroneManagerCmd;
import s4n.codechallenge.actorsdtos.dtos.DroneManagerDto;
import s4n.codechallenge.actorsdtos.dtos.OrderAndCoordinateDto;
import s4n.codechallenge.entities.Drone;
import s4n.codechallenge.services.DroneManager;
import s4n.codechallenge.services.RoutePlanning;

import java.util.List;

@Generated
@Getter
@Setter
public class DroneManagerImpl extends AbstractBehavior<DroneManagerDtoCmd> implements DroneManager {

    private List<Drone> drones;
    private OrderAndCoordinateDto orderAndCoordinateDto;
    private ActorRef<RoutePlanning> replyT  o;

    public DroneManagerImpl(ActorContext<DroneManagerDtoCmd> context) {
        super(context);
    }

    public static Behavior<DroneManagerDtoCmd> create() {
        return Behaviors.setup(DroneManagerImpl::new);
    }

    @Override
    public void droneSync(DroneManagerDtoCmd droneManagerDtoCmd) {
        getContext().getLog().info("Sync drone {}");
    }

    @Override
    public void deliverOrders() {

    }

    @Override
    public void moveDrone() {

    }

    @Override
    public Receive<DroneManagerDtoCmd> createReceive() {
        ReceiveBuilder<DroneManagerDtoCmd> builder = newReceiveBuilder();

        builder.onMessage(DroneManagerCmd.class, this::callDroneActorActuator);
        builder.onMessage(DroneManagerDto.class, this::callRoutePlanningActuator)

        return builder.build();
    }

    private Behavior<DroneManagerDtoCmd> callRoutePlanningActuator(DroneManagerDto droneManagerDto) {
        return null;
    }

    private Behavior<DroneManagerDtoCmd> callDroneActorActuator(DroneManagerCmd droneManagerDtoCmd) {
        droneSync(droneManagerDtoCmd);

        return null;
    }
}
