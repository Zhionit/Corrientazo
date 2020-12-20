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
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.commands.CartesianCoordinateCmd;
import s4n.codechallenge.actorsdtos.commands.DeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveDroneCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorSyncCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;
import s4n.codechallenge.actorsdtos.commands.DroneInformationCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningContainerDto;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningDto;
import s4n.codechallenge.actorsdtos.communication.PlanningRoutesToDroneManagerCmd;
import s4n.codechallenge.actorsdtos.commands.NewDeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.commands.RouteCoordinatesCmd;
import s4n.codechallenge.actorsdtos.dtos.CartesianCoordinateDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerMoveDroneDto;
import s4n.codechallenge.actorsdtos.dtos.DeliveryOrderDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.actorsdtos.dtos.DroneDto;
import s4n.codechallenge.actorsdtos.dtos.FinalOrderInformation;
import s4n.codechallenge.actorsdtos.dtos.OrderDto;
import s4n.codechallenge.actorsdtos.dtos.RoutesDto;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.DeliveryOrder;
import s4n.codechallenge.entities.Drone;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.entities.Route;
import s4n.codechallenge.entities.RouteCoordinates;
import s4n.codechallenge.enums.CardinalDirection;
import s4n.codechallenge.enums.DeliveryOrderStatus;
import s4n.codechallenge.enums.DroneStatus;
import s4n.codechallenge.enums.RouteStatus;
import s4n.codechallenge.services.DroneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Generated
@Getter
@Setter
public class DroneManagerImpl extends AbstractBehavior<DroneManagerDtoCmd> implements DroneManager {
    private Route route;
    private Drone drone;
    private DroneInformation droneInformation;
    private List<DeliveryOrder> deliveryOrders;
    private DeliveryOrder actualDeliveryOrder;
    private DroneInformationCmd droneInformationCmd;
    private List<FinalOrderInformation> finalOrderStatuses;

    private PlanningRoutesToDroneManagerCmd planningRoutesToDroneManagerCmd;
    private ActorRef<RoutePlanningDtoCmd> routePlanningActorRefReplyTo;
    private final ActorRef<DroneActuatorDtoCmd> droneActuatorActorRef;

    public DroneManagerImpl(ActorContext<DroneManagerDtoCmd> context) {
        super(context);
        this.droneInformation = new DroneInformation();
        droneActuatorActorRef = context.spawn(DroneActuatorImpl.create(), "droneActuator");
    }

    public static Behavior<DroneManagerDtoCmd> create() {
        return Behaviors.setup(DroneManagerImpl::new);
    }

    @Override
    public void droneSync(DroneActuatorToDroneManagerSyncDroneDto droneManagerDtoCmd) {

    }

    @Override
    public void deliverOrders() {

    }

    @Override
    public void  moveDroneInDroneActuator(DroneInformation newActualDroneInformation) {

        DroneInformationCmd nextDroneInformationDto = DroneInformationCmd.builder()
                .droneId(newActualDroneInformation.getDroneId())
                .cartesianCoordinateCmd(CartesianCoordinateCmd.toCmd(newActualDroneInformation.getCartesianCoordinate()))
                .newDeliveryOrderCmd(NewDeliveryOrderCmd.toCmd(newActualDroneInformation.getDeliveryOrder()))
                .cardinalDirection(newActualDroneInformation.getCardinalDirection())
                .build();

        DroneManagerToDroneActuatorMoveDroneCmd droneManagerToDroneActuatorMoveDroneCmd = DroneManagerToDroneActuatorMoveDroneCmd.builder()
                .droneId(newActualDroneInformation.getDroneId())
                .droneInformationCmd(nextDroneInformationDto)
                .shouldDeliverOrder(newActualDroneInformation.getShouldDeliverOrder())
                .build();

        droneActuatorActorRef.tell(droneManagerToDroneActuatorMoveDroneCmd);
    }

    @Override
    public Receive<DroneManagerDtoCmd> createReceive() {
        ReceiveBuilder<DroneManagerDtoCmd> builder = newReceiveBuilder();

        builder.onMessage(PlanningRoutesToDroneManagerCmd.class, this::processDroneRoutes);
        builder.onMessage(DroneActuatorToDroneManagerSyncDroneDto.class, this::droneSyncBehavior);
        builder.onMessage(DroneActuatorToDroneManagerMoveDroneDto.class, this::moveDroneBehavior);

        return builder.build();
    }

    private Behavior<DroneManagerDtoCmd> processDroneRoutes(PlanningRoutesToDroneManagerCmd planningRoutesToDroneManagerCmd) {
        this.planningRoutesToDroneManagerCmd = planningRoutesToDroneManagerCmd;
        this.routePlanningActorRefReplyTo = this.planningRoutesToDroneManagerCmd.getReplyTo();

        buildDeliveryOrders(this.planningRoutesToDroneManagerCmd.getDeliveryOrdersCmds());
        buildDroneInformation(this.planningRoutesToDroneManagerCmd.getDroneCmd());
        buildDrone(this.planningRoutesToDroneManagerCmd.getDroneCmd());
        buildRoute(planningRoutesToDroneManagerCmd.getRouteId(), this.planningRoutesToDroneManagerCmd.getRoutesCoordinates());
        buildDroneInformationCmd();

        getContext().getLog().info("Sync drone to process routes {}");
        DroneManagerToDroneActuatorSyncCmd droneActuator = new DroneManagerToDroneActuatorSyncCmd(this.getDrone().getId(), this.deliveryOrders, getContext().getSelf());
        droneActuatorActorRef.tell(droneActuator);
        return this;
    }

    private void buildDroneInformationCmd() {
        CartesianCoordinateCmd cartesianCoordinateOfDestinationCmd = CartesianCoordinateCmd.toCmd(
                this.actualDeliveryOrder.getCartesianCoordinateOfDestination());
        Optional<NewDeliveryOrderCmd> newDeliveryOrderCmd = Optional.of(NewDeliveryOrderCmd.builder()
                .id(this.actualDeliveryOrder.getId())
                .cartesianCoordinateOfDestinationCmd(cartesianCoordinateOfDestinationCmd)
                .build());

        CartesianCoordinateCmd cartesianCoordinateCmd = CartesianCoordinateCmd.toCmd(
                this.drone.getDroneInformation().getCartesianCoordinate());
        this.droneInformationCmd = DroneInformationCmd.builder()
                .droneId(this.drone.getId())
                .newDeliveryOrderCmd(newDeliveryOrderCmd)
                .cardinalDirection(this.droneInformation.getCardinalDirection())
                .cartesianCoordinateCmd(cartesianCoordinateCmd)
                .build();
    }

    private void buildDeliveryOrders(List<DeliveryOrderCmd> deliveryOrdersCmds) {
        this.deliveryOrders = DeliveryOrderCmd.toModels(deliveryOrdersCmds);
    }

    private void buildDroneInformation(DroneCmd droneCmd) {
        this.droneInformation = DroneInformation.builder()
                .droneId(droneCmd.getDroneId())
                .droneStatus(DroneStatus.AVAILABLE)
                .cartesianCoordinate(new CartesianCoordinate())
                .cardinalDirection(CardinalDirection.NORTE)
                .build();
    }

    private void buildDrone(DroneCmd droneCmd) {
        this.drone = Drone.builder()
                .id(droneCmd.getDroneId())
                .droneInformation(this.droneInformation)
                .build();
    }

    private void buildRoute(byte routeId, List<RouteCoordinatesCmd> routesCoordinates) {
        this.route = Route.builder()
                .routeId(routeId)
                .drone(this.drone)
                .deliveryOrders(this.deliveryOrders)
                .routeCoordinates(RouteCoordinatesCmd.toModels(routesCoordinates))
                .routeStatus(RouteStatus.WAITING)
                .build();
    }

    private Behavior<DroneManagerDtoCmd> droneSyncBehavior(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto) {
        coordinateDroneMovements(droneActuatorToDroneManagerSyncDroneDto);
        moveDroneInDroneActuator(this.droneInformation);

        return this;
    }

    private Behavior<DroneManagerDtoCmd> moveDroneBehavior(DroneActuatorToDroneManagerMoveDroneDto droneActuatorToDroneManagerMoveDroneDto) {
        moveDroneInDroneActuator(this.droneInformation);

        return this;
    }

    private void coordinateDroneMovements(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto) {
        if (Objects.nonNull(this.route)) {

            Optional<DeliveryOrder> deliveryOrderOptional =
                    findOrderById(droneActuatorToDroneManagerSyncDroneDto.getDroneInformation().getActualDeliveryOrderDto().getId());

            deliveryOrderOptional.ifPresent(actualDeliveryOrder -> {
                DeliveryOrderDto actualDeliveryOrderDto = droneActuatorToDroneManagerSyncDroneDto.getDroneInformation().getActualDeliveryOrderDto();

                if (actualDeliveryOrderDto.getDeliveryOrderStatus().equals(DeliveryOrderStatus.UNDELIVERED)) {
                    moveInUndeliveredOrder(droneActuatorToDroneManagerSyncDroneDto, this.route, actualDeliveryOrder);
                }

                moveToTheNextDeliveryOrder(droneActuatorToDroneManagerSyncDroneDto, actualDeliveryOrderDto);

                this.droneInformationCmd.setNewDeliveryOrderCmd(null);
            });
        } else {
            DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto = buildDroneManagerToRoutePlanning();
            this.routePlanningActorRefReplyTo.tell(droneManagerToRoutePlanningContainerDto);
        }
    }

    private DroneManagerToRoutePlanningContainerDto buildDroneManagerToRoutePlanning() {

        List<DroneManagerToRoutePlanningDto> droneManagerToRoutePlanningDtos = new ArrayList<>();

        this.finalOrderStatuses.forEach(finalOrderInformation -> {
            OrderDto orderDto = OrderDto.builder()
                    .orderId(finalOrderInformation.getOrderId())
                    .finalCartesianCoordinateDto(CartesianCoordinateDto.toDto(finalOrderInformation.getCartesianCoordinate()))
                    .finalCardinalDirectionDto(finalOrderInformation.getCardinalDirection())
                    .build();

            RoutesDto routsDto = RoutesDto.builder()
                    .id(this.route.getRouteId())
                    .orderDto(orderDto)
                    .build();

            DroneDto droneDto = new DroneDto(finalOrderInformation.getDroneId());
            DroneManagerToRoutePlanningDto droneManagerToRoutePlanningDto = DroneManagerToRoutePlanningDto.builder()
                    .droneDto(droneDto)
                    .routesDto(routsDto)
                    .build();

            droneManagerToRoutePlanningDtos.add(droneManagerToRoutePlanningDto);
        });

        DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto =
                new DroneManagerToRoutePlanningContainerDto(droneManagerToRoutePlanningDtos);

        return droneManagerToRoutePlanningContainerDto;
    }

    private void moveToTheNextDeliveryOrder(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto, DeliveryOrderDto actualDeliveryOrderDto) {
        Optional<DeliveryOrder> nextOrder = getNextOrderOptional(actualDeliveryOrderDto);
        nextOrder.ifPresent(nextDeliveryOrder -> {
            NewDeliveryOrderCmd nextActualOrder = NewDeliveryOrderCmd.builder()
                    .id(nextDeliveryOrder.getId())
                    .cartesianCoordinateOfDestinationCmd(CartesianCoordinateCmd.toCmd(nextDeliveryOrder.getCartesianCoordinateOfDestination()))
                    .build();
            this.droneInformationCmd.setNewDeliveryOrderCmd(Optional.of(nextActualOrder));
            moveInUndeliveredOrder(droneActuatorToDroneManagerSyncDroneDto, this.route, this.actualDeliveryOrder);
        });
    }

    private void moveInUndeliveredOrder(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto, Route actualRoute, DeliveryOrder actualDeliveryOrder) {

        CartesianCoordinate deliveryCoordinate = actualDeliveryOrder.getCartesianCoordinateOfDestination();
        Optional<RouteCoordinates> actualCoordinateInRout =
                findActualCoordinateInRoute(droneActuatorToDroneManagerSyncDroneDto.getDroneInformation().getCartesianCoordinateDto(), actualRoute.getRouteCoordinates());

        actualCoordinateInRout.ifPresent(routeCoordinates -> {

            Optional<CartesianCoordinate> nextCoordinatesOptional = routeCoordinates.getNextOptional();

            nextCoordinatesOptional.ifPresent(nextCoordinates -> {

                DroneInformation newActualDroneInformation =
                        buildNewDroneInformationToActualOrder(droneActuatorToDroneManagerSyncDroneDto.getId(),
                                Optional.of(actualDeliveryOrder), Boolean.FALSE);

                if (nextCoordinates.equals(deliveryCoordinate)) {
                    newActualDroneInformation =
                            buildNewDroneInformationToActualOrder(droneActuatorToDroneManagerSyncDroneDto.getId(),
                                    Optional.of(actualDeliveryOrder), Boolean.TRUE);

                    addFinalOrderStatus(newActualDroneInformation);
                }

                moveDroneInDroneActuator(newActualDroneInformation);
            });
        });
    }

    private void addFinalOrderStatus(DroneInformation newActualDroneInformation) {
        FinalOrderInformation order = FinalOrderInformation.builder()
                .orderId(newActualDroneInformation.getDeliveryOrder().get().getId())
                .droneId(newActualDroneInformation.getDroneId())
                .cardinalDirection(newActualDroneInformation.getCardinalDirection())
                .cartesianCoordinate(newActualDroneInformation.getCartesianCoordinate())
                .build();

        finalOrderStatuses.add(order);
    }

    private Optional<DeliveryOrder> getNextOrderOptional(DeliveryOrderDto actualDeliveryOrderDto) {

        Optional<DeliveryOrder> orderInRouteById = findOrderById(actualDeliveryOrderDto.getId());
        return Optional.ofNullable(orderInRouteById.get().getNextDeliveryOrder());
    }

    private DroneInformation buildNewDroneInformationToActualOrder(byte droneId,
                                                                   Optional<DeliveryOrder> deliveryOrderOptional,
                                                                   Boolean shouldDeliverOrder) {

        DroneInformation newActualDroneInformation = DroneInformation.builder()
                .droneId(droneId)
                .cardinalDirection(droneInformationCmd.getCardinalDirection())
                .deliveryOrder(deliveryOrderOptional)
                .shouldDeliverOrder(shouldDeliverOrder)
                .build();

        return newActualDroneInformation;
    }

    private Optional<RouteCoordinates> findActualCoordinateInRoute(CartesianCoordinateDto actualDroneCoordinateDto,
                                                                   List<RouteCoordinates> routeCoordinates) {
        return routeCoordinates.stream().filter(routeCoordinate -> routeCoordinate.getActualOptional().get()
                .equals(actualDroneCoordinateDto))
                .findFirst();
    }

    private Optional<DeliveryOrder> findOrderById(byte orderId) {
        return this.deliveryOrders.stream()
                .filter(order -> order.getId() == orderId)
                .findFirst();
    }
}
