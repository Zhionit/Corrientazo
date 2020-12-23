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
import s4n.codechallenge.actorsdtos.commands.DeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto.SyncDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveCmd.MoveDroneCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorSyncCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningContainerDto;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningDto;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToDroneManagerCmd;
import s4n.codechallenge.actorsdtos.dtos.CardinalPointDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerMoveDroneDto;
import s4n.codechallenge.actorsdtos.dtos.CardinalPointWithDirectionDto;
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
import s4n.codechallenge.actorsdtos.commands.CardinalPointWithDirectionCmd;
import s4n.codechallenge.entities.RouteCoordinates;
import s4n.codechallenge.enums.CartesianDirection;
import s4n.codechallenge.enums.DeliveryOrderStatus;
import s4n.codechallenge.enums.DroneStatus;
import s4n.codechallenge.enums.RouteStatus;
import s4n.codechallenge.services.DroneManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Generated
@Getter
@Setter
public class DroneManagerImpl extends AbstractBehavior<DroneManagerDtoCmd> implements DroneManager {
    private Route route;
    private Drone drone;
    private s4n.codechallenge.entities.DeliveryOrder actualDeliveryOrder;
    private Set<s4n.codechallenge.entities.DeliveryOrder> deliveryOrders;
    private Set<FinalOrderInformationDto> finalOrderStatuses;
    private RouteCoordinates routeCoordinates;

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
        this.finalOrderStatuses = new LinkedHashSet<>();
        ReceiveBuilder<DroneManagerDtoCmd> builder = newReceiveBuilder();

        builder.onMessage(RoutesPlanningToDroneManagerCmd.class, this::listenParentCall);
        builder.onMessage(DroneActuatorToDroneManagerSyncDroneDto.class, this::syncListenChildCall);
        builder.onMessage(DroneActuatorToDroneManagerMoveDroneDto.class, this::moveListenChildCall);

        return builder.build();
    }

    @Override
    public void respondToParentActuator() {
        DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto = buildDroneManagerToRoutePlanning();
        this.routePlanningActorRefReplyTo.tell(droneManagerToRoutePlanningContainerDto);
    }

    @Override
    public Behavior<DroneManagerDtoCmd> listenParentCall(RoutesPlanningToDroneManagerCmd routesPlanningToDroneManagerCmd) {

        buildDrone(routesPlanningToDroneManagerCmd.getRouteCmd().getDrone().getDroneId());
        this.routesPlanningToDroneManagerCmd = routesPlanningToDroneManagerCmd;
        this.routePlanningActorRefReplyTo = this.routesPlanningToDroneManagerCmd.getReplyTo();
        this.deliveryOrders = DeliveryOrderCmd.toModels(routesPlanningToDroneManagerCmd.getRouteCmd().getDeliveryOrdersCmds());

        buildDeliveryOrders(this.routesPlanningToDroneManagerCmd.getRouteCmd().getDeliveryOrdersCmds());
        buildRoute(routesPlanningToDroneManagerCmd.getRouteCmd().getRouteId(),
                this.routesPlanningToDroneManagerCmd.getRouteCmd().getCardinalPointCmds());
        buildRouteCoordinates(routesPlanningToDroneManagerCmd.getRouteCmd().getCardinalPointCmds());

        getContext().getLog().info("Process to order {} has been started", routesPlanningToDroneManagerCmd.getRouteCmd().getRouteId());

        syncCallChild(this.drone);

        return this;
    }

    private void buildRouteCoordinates(Set<CardinalPointWithDirectionCmd> cardinalPointCmds) {

        RouteCoordinates routeCoordinatesPrincipal = new RouteCoordinates();
        Boolean shouldSetCoordinateToParent = Boolean.TRUE;
        boolean shouldSetNextToParent = Boolean.FALSE;

        RouteCoordinates routeCoordinatesTemporal = new RouteCoordinates();

        for (Iterator<CardinalPointWithDirectionCmd> directionCmdIterator = cardinalPointCmds.iterator(); directionCmdIterator.hasNext(); ) {

            RouteCoordinates routeCoordinatesIntern = RouteCoordinates.builder()
                    .cardinalPointWithDirection(CardinalPointWithDirectionCmd.toModel(directionCmdIterator.next()))
                    .build();

            routeCoordinatesTemporal.setNextOptional(Optional.ofNullable(routeCoordinatesIntern));
            routeCoordinatesTemporal = routeCoordinatesIntern;

            if (shouldSetNextToParent) {
                routeCoordinatesPrincipal.setNextOptional(Optional.ofNullable(routeCoordinatesTemporal));
                shouldSetNextToParent = Boolean.FALSE;
            }

            if (shouldSetCoordinateToParent) {

                routeCoordinatesPrincipal.setCardinalPointWithDirection(routeCoordinatesIntern.getCardinalPointWithDirection());
                shouldSetCoordinateToParent = Boolean.FALSE;
                shouldSetNextToParent = Boolean.TRUE;
            }
        }

        this.routeCoordinates = routeCoordinatesPrincipal;
    }

    @Override
    public void syncCallChild(Drone drone) {
        this.route.setRouteStatus(RouteStatus.WAITING);
        DroneManagerToDroneActuatorSyncCmd droneActuator =
                new DroneManagerToDroneActuatorSyncCmd(DroneCmd.toCmd(this.drone), getContext().getSelf());

        droneActuatorActorRef.tell(droneActuator);
    }

    @Override
    public void moveCallChild(Drone drone, Boolean shouldDeliverOrder,
                              CardinalPointWithDirection cardinalPointWithDirection,
                              DeliveryOrder actualDeliveryOrder) {

        MoveDroneCmd moveDroneCmd = MoveDroneCmd.builder()
                .orderCmd(DeliveryOrderCmd.toCmd(actualDeliveryOrder))
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

        coordinateDroneMovements(droneActuatorToDroneManagerSyncDroneDto.getSyncDroneDto());
        return this;
    }

    @Override
    public Behavior<DroneManagerDtoCmd> moveListenChildCall(DroneActuatorToDroneManagerMoveDroneDto droneActuatorToDroneManagerMoveDroneDto) {

        updateDeliverOrder(droneActuatorToDroneManagerMoveDroneDto.getMoveDroneDto().getOrderId());
        coordinateDroneMovements(droneActuatorToDroneManagerMoveDroneDto.getMoveDroneDto());
        return this;
    }

    private void updateDeliverOrder(int orderId) {
        this.actualDeliveryOrder.setDeliveryOrderStatus(DeliveryOrderStatus.DELIVERED);
    }

    private void buildDeliveryOrders(Set<DeliveryOrderCmd> deliveryOrdersCmds) {
        this.deliveryOrders = DeliveryOrderCmd.toModels(deliveryOrdersCmds);
    }

    private void buildDrone(int droneId) {
        this.drone = Drone.builder()
                .id(droneId)
                .direction(CartesianDirection.Y)
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
                        .collect((Collectors.toCollection(LinkedHashSet::new))))
                .routeStatus(RouteStatus.WAITING)
                .build();
    }

    private void coordinateDroneMovements(SyncDroneDto syncDroneDto) {

        Supplier<Stream<DeliveryOrder>> streamSupplier = () -> this.deliveryOrders.stream();
        boolean anyRemainingOrder = streamSupplier.get()
                .anyMatch(deliveryOrder -> deliveryOrder.getDeliveryOrderStatus().equals(DeliveryOrderStatus.UNDELIVERED));
        if (anyRemainingOrder) {

            int orderIdInDrone = syncDroneDto.getOrderId() <= 0 ? 1 : syncDroneDto.getOrderId();
            Optional<DeliveryOrder> deliveryOrderOptional = streamSupplier.get()
                    .filter(deliveryOrder -> deliveryOrder.getId() == orderIdInDrone)
                    .filter(deliveryOrder -> DeliveryOrderStatus.UNDELIVERED.equals(deliveryOrder.getDeliveryOrderStatus()))
                    .findFirst();

            if (deliveryOrderOptional.isPresent()) {
                this.actualDeliveryOrder = deliveryOrderOptional.get();
                moveInOrder(syncDroneDto, deliveryOrderOptional.get());
            } else {

                moveToNextDeliveryOrder(syncDroneDto, this.actualDeliveryOrder);
            }
        }
        respondToParentActuator();
    }

    private DroneManagerToRoutePlanningContainerDto buildDroneManagerToRoutePlanning() {

        List<DroneManagerToRoutePlanningDto> droneManagerToRoutePlanningDtos = new ArrayList<>();

        this.finalOrderStatuses.forEach(finalOrderInformationDto -> {

            OrderDto orderDto = OrderDto.builder()
                    .orderId(finalOrderInformationDto.getOrderId())
                    .finalCardinalPointDto(CardinalPointDto.toDto(finalOrderInformationDto.getCardinalPoint()))
                    .finalCartesianDirection(finalOrderInformationDto.getCardinalDirection())
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

    private void moveToNextDeliveryOrder(SyncDroneDto syncDroneDto, s4n.codechallenge.entities.DeliveryOrder actualDeliveryOrder) {

        Optional<DeliveryOrder> nextOrder = getNextOrderOptional(actualDeliveryOrder);

        nextOrder.ifPresent(nextDeliveryOrder -> {

            this.actualDeliveryOrder = nextDeliveryOrder;
            moveInOrder(syncDroneDto, this.actualDeliveryOrder);
        });
    }

    private void moveInOrder(SyncDroneDto syncDroneDto, DeliveryOrder actualDeliveryOrder) {

        CardinalPoint cardinalPoint = actualDeliveryOrder.getCardinalPoint();
        CardinalPointWithDirectionDto cardinalPointWithDirectionDto = CardinalPointWithDirectionDto.builder()
                .cardinalPointDto(CardinalPointDto.toDto(cardinalPoint))
                .build();;
        RouteCoordinates routeCoordinates = findActualRouteCoordinate(cardinalPointWithDirectionDto, this.routeCoordinates);

        if (Objects.isNull(routeCoordinates)) {

            moveToNextDeliveryOrder(syncDroneDto, this.actualDeliveryOrder);
        }

        Boolean shouldDeliverOrder = Boolean.FALSE;

        Drone drone = Drone.builder()
                .id(syncDroneDto.getDroneDto().getDroneId())
                .status(DroneStatus.BUSY)
                .position(routeCoordinates.getCardinalPointWithDirection().getCardinalPoint())
                .direction(routeCoordinates.getCardinalPointWithDirection().getCartesianDirection())
                .build();

        if (routeCoordinates.getCardinalPointWithDirection().getCardinalPoint().equals(actualDeliveryOrder.getCardinalPoint())) {
            addFinalOrderStatus(drone, actualDeliveryOrder);
            shouldDeliverOrder = Boolean.TRUE;
        }

        moveCallChild(drone, shouldDeliverOrder, routeCoordinates.getCardinalPointWithDirection(), this.actualDeliveryOrder);

    }

    private RouteCoordinates findActualRouteCoordinate(CardinalPointWithDirectionDto cardinalPointWithDirectionCmd,
                                                       RouteCoordinates routeCoordinates) {

        if (Objects.isNull(routeCoordinates)) {
            return null;
        } else if (routeCoordinates.getCardinalPointWithDirection().getCardinalPoint().equals(cardinalPointWithDirectionCmd.getCardinalPointDto())) {
            return routeCoordinates;
        }
        return findActualRouteCoordinate(cardinalPointWithDirectionCmd, routeCoordinates.getNextOptional().get());
    }

    private void addFinalOrderStatus(Drone drone, s4n.codechallenge.entities.DeliveryOrder actualDeliveryOrder) {

        FinalOrderInformationDto order = FinalOrderInformationDto.builder()
                .orderId(actualDeliveryOrder.getId())
                .droneId(drone.getId())
                .cardinalDirection(drone.getDirection())
                .cardinalPoint(drone.getPosition())
                .build();

        this.finalOrderStatuses.add(order);
    }

    private Optional<DeliveryOrder> getNextOrderOptional(DeliveryOrder actualDeliveryOrder) {
        return new ArrayList<>(this.deliveryOrders).stream()
                .filter(actualDeliveryOrderDtoToFilter -> actualDeliveryOrderDtoToFilter.getId() == (actualDeliveryOrder.getId() + 1))
                .findFirst();
    }
}
