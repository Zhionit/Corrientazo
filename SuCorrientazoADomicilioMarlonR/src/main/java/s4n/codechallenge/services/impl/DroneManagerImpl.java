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
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.commands.DroneManagerCmd;
import s4n.codechallenge.actorsdtos.dtos.MoveDroneDto;
import s4n.codechallenge.actorsdtos.dtos.OrderDto;
import s4n.codechallenge.actorsdtos.dtos.SyncDroneDto;
import s4n.codechallenge.entities.Order;
import s4n.codechallenge.entities.Route;
import s4n.codechallenge.services.DroneManager;

import java.util.List;
import java.util.Optional;

@Generated
@Getter
@Setter
public class DroneManagerImpl extends AbstractBehavior<DroneManagerDtoCmd> implements DroneManager {
    private List<Route> routes;
    private ActorRef<DroneActuatorDtoCmd> droneActuatorActorRef;

    public DroneManagerImpl(ActorContext<DroneManagerDtoCmd> context) {
        super(context);
        droneActuatorActorRef = context.spawn(DroneActuatorImpl.create(), "droneActuator");
    }

    public static Behavior<DroneManagerDtoCmd> create() {
        return Behaviors.setup(DroneManagerImpl::new);
    }

    @Override
    public void droneSync(SyncDroneDto droneManagerDtoCmd) {

    }

    @Override
    public void deliverOrders() {

    }

    @Override
    public DroneManagerDtoCmd moveDrone(MoveDroneDto droneManagerDtoCmd) {
        return null;
    }

    @Override
    public Receive<DroneManagerDtoCmd> createReceive() {
        ReceiveBuilder<DroneManagerDtoCmd> builder = newReceiveBuilder();

        builder.onMessage(DroneManagerCmd.class, this::processDroneRoutes);
        builder.onMessage(SyncDroneDto.class, this::droneSyncBehavior);
        builder.onMessage(MoveDroneDto.class, this::moveDroneBehavior);

        return builder.build();
    }

    private Behavior<DroneManagerDtoCmd> processDroneRoutes(DroneManagerCmd droneManagerDtoCmd) {
        //TODO Set RouteCoordinates and related
        getContext().getLog().info("Sync drone to process routes {}");
        droneActuatorActorRef.tell(null);
        return null;
    }

    private Behavior<DroneManagerDtoCmd> droneSyncBehavior(SyncDroneDto syncDroneDto) {
        coordinateDroneMovements(syncDroneDto);
        moveDrone(null);
        return this;
    }

    private Behavior<DroneManagerDtoCmd> moveDroneBehavior(MoveDroneDto moveDroneDto) {
        moveDrone(moveDroneDto);
        //moveDroneDto.getDroneInformation().
        return null;
    }

    private void coordinateDroneMovements(SyncDroneDto syncDroneDto) {
        Optional<Route> routeOptional = findRouteCoordinatesByDroneId(syncDroneDto.getId());
        routeOptional.ifPresent(route -> {
            OrderDto actualOrderDto = syncDroneDto.getDroneInformation().getActualOrderDto();

            Optional<Order> orderOptional = findOrderInRouteById(actualOrderDto.getId(), route);
            orderOptional.ifPresent(order -> {
                
            });

        });
    }

    private Optional<Order> findOrderInRouteById(byte orderId, Route route) {
        return route.getOrders()
                .stream()
                .filter(order -> order.getId() == orderId)
                .findFirst();
    }

    private Optional<Route> findRouteCoordinatesByDroneId(byte droneId) {
        return this.routes.stream()
                .filter(route -> route.getDrone().getId() == droneId)
                .findFirst();
    }
}
