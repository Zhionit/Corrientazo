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
import s4n.codechallenge.actorsdtos.commands.CartesianCoordinateCmd;
import s4n.codechallenge.actorsdtos.commands.DroneInformationCmd;
import s4n.codechallenge.actorsdtos.commands.DroneManagerCmd;
import s4n.codechallenge.actorsdtos.commands.MoveDroneCmd;
import s4n.codechallenge.actorsdtos.commands.NewActualDeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.dtos.CartesianCoordinateDto;
import s4n.codechallenge.actorsdtos.dtos.MoveDroneDto;
import s4n.codechallenge.actorsdtos.dtos.DeliveryOrderDto;
import s4n.codechallenge.actorsdtos.dtos.SyncDroneDto;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.DeliveryOrder;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.entities.Route;
import s4n.codechallenge.entities.RouteCoordinates;
import s4n.codechallenge.enums.DroneStatus;
import s4n.codechallenge.enums.DeliveryOrderStatus;
import s4n.codechallenge.services.DroneManager;

import java.util.List;
import java.util.Optional;

@Generated
@Getter
@Setter
public class DroneManagerImpl extends AbstractBehavior<DroneManagerDtoCmd> implements DroneManager {
    private List<Route> routes;
    private ActorRef<DroneActuatorDtoCmd> droneActuatorActorRef;
    private DroneInformationCmd droneInformationCmd;

    public DroneManagerImpl(ActorContext<DroneManagerDtoCmd> context) {
        super(context);
        this.droneInformationCmd = new DroneInformationCmd();
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
    public void moveDrone() {

        DroneInformationCmd nextDroneInformationDto = DroneInformationCmd.builder()
                .cartesianCoordinateCmd(CartesianCoordinateCmd.toCmd(nextCoordinates))
                .actualNewActualDeliveryOrderCmd(NewActualDeliveryOrderCmd.toCmd(actualOrder))
                .cardinalDirection(.getCardinalDirection())
                                    .build();

        MoveDroneCmd moveDroneCmd = MoveDroneCmd.builder()
                .droneId(syncDroneDto.getId())
                .droneInformationDto(nextDroneInformationDto)
                .shouldDeliverOrder(Boolean.TRUE)
                .build();

        droneActuatorActorRef.tell();
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

            DeliveryOrderDto actualDeliveryOrderDto = syncDroneDto.getDroneInformation().getActualDeliveryOrderDto();
            if (actualDeliveryOrderDto.getDeliveryOrderStatus().equals(DeliveryOrderStatus.UNDELIVERED)) {

                Optional<DeliveryOrder> deliveryOrderOptional = findOrderInRouteById(actualDeliveryOrderDto.getId(), route);
                deliveryOrderOptional.ifPresent(actualDeliveryOrder -> {
                    CartesianCoordinate deliverCoordinate = actualDeliveryOrder.getCartesianCoordinateOfDestination();
                    Optional<RouteCoordinates> actualCoordinateInRout =
                            findActualCoordinateInRoute(syncDroneDto.getDroneInformation().getCartesianCoordinateDto(), route.getRouteCoordinates());

                    actualCoordinateInRout.ifPresent(routeCoordinates -> {
                        CartesianCoordinate nextCoordinates = routeCoordinates.getNext();
                        if (nextCoordinates.equals(deliverCoordinate)) {
                            DroneInformation newActualDroneInformation = DroneInformation.builder()
                                    .droneStatus(DroneStatus.BUSY)
                                    .cardinalDirection(droneInformationCmd.getCardinalDirection())
                                    .deliveryOrder(droneInformationCmd.getActualNewActualDeliveryOrderCmd())
                                    .build();

                            moveDrone();
                        }
                    });
                });

            }

            DeliveryOrder newActualDeliveryOrder = getNextOrder(actualDeliveryOrderDto, route.getDeliveryOrders());
        });
    }

    private Optional<RouteCoordinates> findActualCoordinateInRoute(CartesianCoordinateDto actualDroneCoordinateDto,
                                                                   List<RouteCoordinates> routeCoordinates) {
        return routeCoordinates.stream().filter(routeCoordinate -> routeCoordinate.getActual()
                .equals(actualDroneCoordinateDto))
                .findFirst();
    }

    private Optional<DeliveryOrder> findOrderInRouteById(byte orderId, Route route) {
        return route.getDeliveryOrders()
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
