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
import s4n.codechallenge.actorsdtos.commands.CardinalPointCmd;
import s4n.codechallenge.actorsdtos.commands.CartesianCoordinateCmd;
import s4n.codechallenge.actorsdtos.commands.DeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveCmd.MoveDroneCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorSyncCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningContainerDto;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningDto;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToDroneManagerCmd;
import s4n.codechallenge.actorsdtos.commands.NewDeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.dtos.CardinalPointDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerMoveDroneDto;
import s4n.codechallenge.actorsdtos.dtos.DeliveryOrderDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.actorsdtos.dtos.DroneDto;
import s4n.codechallenge.actorsdtos.dtos.FinalOrderInformationDto;
import s4n.codechallenge.actorsdtos.dtos.OrderDto;
import s4n.codechallenge.actorsdtos.dtos.RoutesDto;
import s4n.codechallenge.entities.CardinalPoint;
import s4n.codechallenge.entities.CardinalPointWithDirection;
import s4n.codechallenge.entities.DeliveryOrder;
import s4n.codechallenge.entities.Drone;
import s4n.codechallenge.entities.Route;
import s4n.codechallenge.entities.RouteCoordinates;
import s4n.codechallenge.actorsdtos.commands.CardinalPointWithDirectionCmd;
import s4n.codechallenge.enums.CardinalDirection;
import s4n.codechallenge.enums.DeliveryOrderStatus;
import s4n.codechallenge.enums.DroneStatus;
import s4n.codechallenge.enums.RouteStatus;
import s4n.codechallenge.services.DroneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
public class DroneManagerImpl extends AbstractBehavior<DroneManagerDtoCmd> implements DroneManager {
    private Route route;
    private Drone drone;
    private DeliveryOrder actualDeliveryOrder;
    private Set<DeliveryOrder> deliveryOrders;
    private Set<FinalOrderInformationDto> finalOrderStatuses;

    private RoutesPlanningToDroneManagerCmd routesPlanningToDroneManagerCmd;
    private ActorRef<RoutePlanningDtoCmd> routePlanningActorRefReplyTo;
    private final ActorRef<DroneActuatorDtoCmd> droneActuatorActorRef;

    public DroneManagerImpl(ActorContext<DroneManagerDtoCmd> context) {
        super(context);
        droneActuatorActorRef = context.spawn(DroneActuatorImpl.create(), "droneActuator");
    }

    public static Behavior<DroneManagerDtoCmd> create() {
        return Behaviors.setup(DroneManagerImpl::new);
    }

    @Override
    public Receive<DroneManagerDtoCmd> createReceive() {
        ReceiveBuilder<DroneManagerDtoCmd> builder = newReceiveBuilder();

        builder.onMessage(RoutesPlanningToDroneManagerCmd.class, this::listenParentCall);
        builder.onMessage(DroneActuatorToDroneManagerSyncDroneDto.class, this::syncListenChildCall);
        builder.onMessage(DroneActuatorToDroneManagerMoveDroneDto.class, this::moveListenChildCall);

        return builder.build();
    }

    @Override
    public Behavior<DroneManagerDtoCmd> respondToParentActuator(DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto) {
        return null;
    }

    @Override
    public Behavior<DroneManagerDtoCmd> listenParentCall(RoutesPlanningToDroneManagerCmd routesPlanningToDroneManagerCmd) {

        buildDrone(routesPlanningToDroneManagerCmd.getRouteCmd().getDrone().getDroneId());
        this.routesPlanningToDroneManagerCmd = routesPlanningToDroneManagerCmd;
        this.routePlanningActorRefReplyTo = this.routesPlanningToDroneManagerCmd.getReplyTo();

        buildDeliveryOrders(this.routesPlanningToDroneManagerCmd.getRouteCmd().getDeliveryOrdersCmds());
        buildRoute(routesPlanningToDroneManagerCmd.getRouteCmd().getRouteId(),
                this.routesPlanningToDroneManagerCmd.getRouteCmd().getCardinalPointCmds());

        getContext().getLog().info("Sync drone to process routes {}");

        DroneManagerToDroneActuatorSyncCmd droneActuator =
                new DroneManagerToDroneActuatorSyncCmd(DroneCmd.toCmd(this.drone), getContext().getSelf());

        droneActuatorActorRef.tell(droneActuator);

        return this;
    }

    @Override
    public void syncCallChild(Drone drone) {
    }

    @Override
    public void moveCallChild(Drone drone, Boolean shouldDeliverOrder, CardinalPointWithDirection cardinalPointWithDirection) {

        MoveDroneCmd moveDroneCmd = MoveDroneCmd.builder()
                .droneCmd(DroneCmd.toCmd(drone))
                .cardinalPointWithDirectionCmd(CardinalPointWithDirectionCmd.toCmd(cardinalPointWithDirection))
                .shouldDeliverOrder(shouldDeliverOrder)
                .build();
        DroneManagerToDroneActuatorMoveCmd droneManagerToDroneActuatorMoveCmd = DroneManagerToDroneActuatorMoveCmd.builder()
                .moveDroneCmd(moveDroneCmd)
                .replyTo(getContext().getSelf())
                .build();

        droneActuatorActorRef.tell(droneManagerToDroneActuatorMoveCmd);
    }

    @Override
    public Behavior<DroneManagerDtoCmd> syncListenChildCall(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto) {

        coordinateDroneMovements(droneActuatorToDroneManagerSyncDroneDto.getDroneDto());
        moveCallChild(this.drone, droneInformation.getShouldDeliverOrder(), cardinalPointWithDirection);

        return this;
    }

    @Override
    public Behavior<DroneManagerDtoCmd> moveListenChildCall(DroneActuatorToDroneManagerMoveDroneDto droneActuatorToDroneManagerMoveDroneDto) {

        moveCallChild(this.drone, droneInformation.getShouldDeliverOrder(), cardinalPointWithDirection);
        return this;
    }

    private void buildDeliveryOrders(Set<DeliveryOrderCmd> deliveryOrdersCmds) {
        this.deliveryOrders = DeliveryOrderCmd.toModels(deliveryOrdersCmds);
    }

    private void buildDrone(int droneId) {
        this.drone = Drone.builder()
                .id(droneId)
                .direction(CardinalDirection.NORTE)
                .position(new CardinalPoint())
                .status(DroneStatus.AVAILABLE)
                .build();
    }

    private void buildRoute(int routeId, Set<CardinalPointWithDirectionCmd> routesCoordinatesCmds) {
        this.route = Route.builder()
                .routeId(routeId)
                .drone(this.drone)
                .deliveryOrders(this.deliveryOrders)
                .cardinalPoints(routesCoordinatesCmds.stream()
                        .map(cardinalPointWithDirectionCmd -> CardinalPointCmd.toModel(cardinalPointWithDirectionCmd.getCardinalPointCmd()))
                        .collect(Collectors.toSet()))
                .routeStatus(RouteStatus.WAITING)
                .build();
    }

    private void coordinateDroneMovements(DroneDto droneDto) {
        if (Objects.nonNull(this.route)) {

            Optional<DeliveryOrder> deliveryOrderOptional =
                    findOrderById(droneDto.getDroneDto().getActualDeliveryOrderDto().getId());

            deliveryOrderOptional.ifPresent(actualDeliveryOrder -> {
                DeliveryOrderDto actualDeliveryOrderDto = droneDto.getDroneDto().getActualDeliveryOrderDto();

                if (actualDeliveryOrderDto.getDeliveryOrderStatus().equals(DeliveryOrderStatus.UNDELIVERED)) {
                    moveInUndeliveredOrder(droneDto, this.route, actualDeliveryOrder);
                }

                moveToTheNextDeliveryOrder(droneDto, actualDeliveryOrderDto);

                this.droneInformationCmd.setNewDeliveryOrderCmd(null);
            });
        } else {
            DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto = buildDroneManagerToRoutePlanning();
            this.routePlanningActorRefReplyTo.tell(droneManagerToRoutePlanningContainerDto);
        }
    }

    private DroneManagerToRoutePlanningContainerDto buildDroneManagerToRoutePlanning() {

        List<DroneManagerToRoutePlanningDto> droneManagerToRoutePlanningDtos = new ArrayList<>();

        this.finalOrderStatuses.forEach(finalOrderInformationDto -> {
            OrderDto orderDto = OrderDto.builder()
                    .orderId(finalOrderInformationDto.getOrderId())
                    .finalCardinalPointDto(CardinalPointDto.toDto(finalOrderInformationDto.getCardinalPoint()))
                    .finalCardinalDirectionDto(finalOrderInformationDto.getCardinalDirection())
                    .build();

            RoutesDto routsDto = RoutesDto.builder()
                    .id(this.route.getRouteId())
                    .orderDto(orderDto)
                    .build();

            DroneDto droneDto = new DroneDto(finalOrderInformationDto.getDroneId());
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
                    .cartesianCoordinateOfDestinationCmd(CartesianCoordinateCmd.toCmd(nextDeliveryOrder.getCardinalPoint()))
                    .build();
            this.droneInformationCmd.setNewDeliveryOrderCmd(Optional.of(nextActualOrder));
            moveInUndeliveredOrder(droneActuatorToDroneManagerSyncDroneDto, this.route, this.actualDeliveryOrder);
        });
    }

    private void moveInUndeliveredOrder(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto,
                                        Route actualRoute, DeliveryOrder actualDeliveryOrder) {

        CardinalPoint deliveryCoordinate = actualDeliveryOrder.getCardinalPoint();
        Optional<RouteCoordinates> actualCoordinateInRout = findActualCoordinateInRoute(
                droneActuatorToDroneManagerSyncDroneDto.getDroneDto().getCardinalPointDto(),
                        actualRoute.getCardinalPoints()
        );

        actualCoordinateInRout.ifPresent(routeCoordinates -> {

            Optional<CardinalPointWithDirectionCmd> directionAndCoordinatesOptional = routeCoordinates.getNextOptional();

            directionAndCoordinatesOptional.ifPresent(nextDirectionAndCoordinates -> {

                DroneInformation newActualDroneInformation =
                        buildNewDroneInformationToActualOrder(droneActuatorToDroneManagerSyncDroneDto.getId(),
                                Optional.of(actualDeliveryOrder), nextDirectionAndCoordinates.getCartesianDirection(), Boolean.FALSE);

                if (nextDirectionAndCoordinates.getCardinalPointCmd().equals(deliveryCoordinate)) {
                    newActualDroneInformation =
                            buildNewDroneInformationToActualOrder(droneActuatorToDroneManagerSyncDroneDto.getId(),
                                    Optional.of(actualDeliveryOrder), nextDirectionAndCoordinates.getCartesianDirection(), Boolean.TRUE);

                    addFinalOrderStatus(newActualDroneInformation);
                }

                Drone drone = Drone.builder()
                        .droneInformation(newActualDroneInformation)
                        .build();
                moveCallChild(drone, droneInformation.getShouldDeliverOrder(), cardinalPointWithDirection);
            });
        });
    }

    private void addFinalOrderStatus(DroneInformation newActualDroneInformation) {
        FinalOrderInformationDto order = FinalOrderInformationDto.builder()
                .orderId(newActualDroneInformation.getDeliveryOrder().get().getId())
                .droneId(newActualDroneInformation.getDroneId())
                .cardinalDirection(newActualDroneInformation.getCardinalDirection())
                .cardinalPoint(newActualDroneInformation.getCartesianCoordinate())
                .build();

        finalOrderStatuses.add(order);
    }

    private Optional<DeliveryOrder> getNextOrderOptional(DeliveryOrderDto actualDeliveryOrderDto) {

        Optional<DeliveryOrder> orderInRouteById = findOrderById(actualDeliveryOrderDto.getId());
        return Optional.ofNullable(orderInRouteById.get().getNextDeliveryOrder());
    }

    private DroneInformation buildNewDroneInformationToActualOrder(byte droneId,
                                                                   Optional<DeliveryOrder> deliveryOrderOptional,
                                                                   String cardinalDirection, Boolean shouldDeliverOrder) {

        DroneInformation newActualDroneInformation = DroneInformation.builder()
                .droneId(droneId)
                //TODO Maybe need some "update"
                .cardinalDirection(droneInformationCmd.getCardinalDirection())
                .deliveryOrder(deliveryOrderOptional)
                .shouldDeliverOrder(shouldDeliverOrder)
                .build();

        return newActualDroneInformation;
    }

    private Optional<RouteCoordinates> findActualCoordinateInRoute(CardinalPointDto actualDroneCoordinateDto,
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
