package s4n.codechallenge.services.impl;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.FilesManagerDtoCmd;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;
import s4n.codechallenge.actorsdtos.commands.ValueAndCoordinateCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningContainerDto;
import s4n.codechallenge.actorsdtos.communication.FileManagerToRoutePlanningCmd;
import s4n.codechallenge.actorsdtos.communication.RoutePlanningToFileManagerDtoFailException;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToDroneManagerCmd;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToFileManagerDto;
import s4n.codechallenge.actorsdtos.dtos.RoutesDto;
import s4n.codechallenge.entities.CardinalPoint;
import s4n.codechallenge.entities.CircularValueAndCoordinate;
import s4n.codechallenge.entities.DeliveryOrder;
import s4n.codechallenge.entities.RoutePlanningIndexes;
import s4n.codechallenge.entities.ValueAndCoordinate;
import s4n.codechallenge.enums.CartesianDirection;
import s4n.codechallenge.services.RoutePlanning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoutePlanningImpl extends AbstractBehavior<RoutePlanningDtoCmd> implements RoutePlanning {

    private static final byte MAX_DRONES_ALLOWED = 20;
    private static final byte MAX_AMOUNT_ORDERS_ALLOWED = 3;
    public static final int MAX_CUADRAS_LENGTH = 10;
    public static final int MAX_CUADRAS_LENGTH_NEGATIVE = -10;

    private byte droneId;
    private CardinalPoint actualCardinalPoint;
    private CartesianDirection actualCartesianDirection;
    private CircularValueAndCoordinate actualCircularListOfCoordinates;

    private Map<DeliveryOrder, Set<ValueAndCoordinate>> deliveryOrdersMovements;
    private Map<DeliveryOrder, ValueAndCoordinate> endOfEachOrder;

    private ActorRef<FilesManagerDtoCmd> fatherRoutePlanningDtoCmdActorRef;
    private final ActorRef<DroneManagerDtoCmd> childDroneManagerDtoCmdActorRef;

    public RoutePlanningImpl(ActorContext<RoutePlanningDtoCmd> context) {
        super(context);
        buildCircularListOfCoordinates();
        buildInitialValues();
        childDroneManagerDtoCmdActorRef = context.spawn(DroneManagerImpl.create(), "DroneManager");
    }


    public static Behavior<RoutePlanningDtoCmd> create() {
        return Behaviors.setup(RoutePlanningImpl::new);
    }

    @Override
    public Receive<RoutePlanningDtoCmd> createReceive() {
        ReceiveBuilder<RoutePlanningDtoCmd> routePlanningDtoCmdReceiveBuilder = newReceiveBuilder();

        routePlanningDtoCmdReceiveBuilder.onMessage(FileManagerToRoutePlanningCmd.class, this::extractDrone);
        routePlanningDtoCmdReceiveBuilder.onMessage(DroneManagerToRoutePlanningContainerDto.class, this::listenChildCall);

        return routePlanningDtoCmdReceiveBuilder.build();
    }

    @Override
    public Behavior<RoutePlanningDtoCmd> extractDrone(FileManagerToRoutePlanningCmd fileManagerToRoutePlanningCmd) {

        this.fatherRoutePlanningDtoCmdActorRef = fileManagerToRoutePlanningCmd.getReplyTo();

        int digitAmountOfDroneIdNumber = 2;
        String cleanedFileName = fileManagerToRoutePlanningCmd.getFileName()
                .substring(fileManagerToRoutePlanningCmd.getFileName().length() - digitAmountOfDroneIdNumber);

        this.droneId = Byte.parseByte(cleanedFileName);
        validateDrone(this.droneId, fileManagerToRoutePlanningCmd.getEncodedOrders().size());

        RoutePlanningIndexes routePlanningIndexes = new RoutePlanningIndexes();
        generateDroneRoute(fileManagerToRoutePlanningCmd.getEncodedOrders(), routePlanningIndexes);

        return this;
    }

    @Override
    public void validateDrone(byte amountsOrders, int ordersAmount) {

        if (droneId > MAX_DRONES_ALLOWED)
            buildErrorResponseToFileManager("The drone is not available", droneId);

        if (ordersAmount > MAX_AMOUNT_ORDERS_ALLOWED)
            buildErrorResponseToFileManager("The drone has an incorrect amount of orders", droneId);

    }

    private void buildErrorResponseToFileManager(String errorMessage, byte droneId) {
        RoutePlanningToFileManagerDtoFailException routePlanningToFileManagerDtoFailException = new RoutePlanningToFileManagerDtoFailException();
        routePlanningToFileManagerDtoFailException.setErrorMessage(errorMessage);
        routePlanningToFileManagerDtoFailException.setDroneId(droneId);

        respondToParentActuatorErrorMessage(routePlanningToFileManagerDtoFailException);
    }

    @Override
    public void generateDroneRoute(List<String> encodedOrders, RoutePlanningIndexes routePlanningIndexes) {

        List<DeliveryEncodedOrder> deliveryEncodedOrders = new ArrayList<>();

        encodedOrders.forEach(encodedOrder -> {
            DeliveryEncodedOrder deliveryEncodedOrder = DeliveryEncodedOrder.builder()
                    .deliveryOrder(DeliveryOrder.builder().build())
                    .encodedOrder(encodedOrder)
                    .build();

            deliveryEncodedOrders.add(deliveryEncodedOrder);
        });

        stepsToGenerateDroneRoute(deliveryEncodedOrders, routePlanningIndexes);
    }

    private void stepsToGenerateDroneRoute(List<DeliveryEncodedOrder> deliveryEncodedOrders, RoutePlanningIndexes routePlanningIndexes) {

        deliveryEncodedOrders.forEach(deliveryEncodedOrder -> {

            char movementChar = readMovement(deliveryEncodedOrder.getEncodedOrder(), routePlanningIndexes);
            interpretMovement(movementChar);
            applyMovement();
            addMovementToDeliveryOrder(deliveryEncodedOrder.getDeliveryOrder());

            if (deliveryEncodedOrder.encodedOrder.length() == routePlanningIndexes.getActualMovementCharIndex()) {
                setLastNextMovement(routePlanningIndexes, deliveryEncodedOrder.getDeliveryOrder());
            }
        });

        sendInformationToDroneManager();
    }

    private char readMovement(String encodedOrder, RoutePlanningIndexes routePlanningIndexes) {

        char movementChar = encodedOrder.charAt(routePlanningIndexes.getActualMovementCharIndex());
        routePlanningIndexes.incrementCharMovementIndex();

        return movementChar;
    }

    private void interpretMovement(char movementChar) {

        switch (movementChar) {
            case 'D':
                this.actualCircularListOfCoordinates = findActualCoordinate(this.actualCircularListOfCoordinates).getNext();
                this.actualCartesianDirection = findCoordinatesDirection(this.actualCircularListOfCoordinates);
                break;
            case 'I':
                this.actualCircularListOfCoordinates = findActualCoordinate(this.actualCircularListOfCoordinates).getBefore();
                this.actualCartesianDirection = findCoordinatesDirection(this.actualCircularListOfCoordinates);
                break;
            case 'A':
                this.actualCartesianDirection = findCoordinatesDirection(this.actualCircularListOfCoordinates);
                break;
        }
    }

    private CartesianDirection findCoordinatesDirection(CircularValueAndCoordinate circularListOfCoordinates) {
        return Arrays.stream(CartesianDirection.values())
                .filter(cartesianDirection -> cartesianDirection.getValue()
                        .equals(circularListOfCoordinates.getSame().getName())
                ).findFirst().get();
    }

    private CircularValueAndCoordinate findActualCoordinate(CircularValueAndCoordinate circularListOfCoordinatesP) {

        CircularValueAndCoordinate circularListOfCoordinates = circularListOfCoordinatesP;

        if (this.actualCartesianDirection.getValue().equals(circularListOfCoordinates.getSame().getName())) {
            return circularListOfCoordinates;
        }

        circularListOfCoordinates = circularListOfCoordinates.getNext();
        findActualCoordinate(circularListOfCoordinates);

        return null;
    }

    private void applyMovement() {

        this.actualCardinalPoint.incrementValues(
                this.actualCircularListOfCoordinates.getSame().getCardinalPoint().getXAxe(),
                this.actualCircularListOfCoordinates.getSame().getCardinalPoint().getYAxe()
        );

        validateCuadrasLength(this.actualCardinalPoint);
    }

    private void validateCuadrasLength(CardinalPoint actualCardinalPoint) {
        if (actualCardinalPoint.getXAxe() > MAX_CUADRAS_LENGTH || actualCardinalPoint.getXAxe() < MAX_CUADRAS_LENGTH_NEGATIVE )
            buildErrorResponseToFileManager("El límite de cuadras a sido excedido", this.droneId);

        if (actualCardinalPoint.getYAxe() > MAX_CUADRAS_LENGTH || actualCardinalPoint.getYAxe() < MAX_CUADRAS_LENGTH_NEGATIVE )
            buildErrorResponseToFileManager("El límite de cuadras a sido excedido", this.droneId);

    }

    private void addMovementToDeliveryOrder(DeliveryOrder deliveryOrder) {

        ValueAndCoordinate valueAndCoordinate = ValueAndCoordinate.builder()
                .name(this.actualCartesianDirection.name())
                .cardinalPoint(
                        new CardinalPoint(this.actualCardinalPoint.getXAxe(), this.actualCardinalPoint.getYAxe())
                )
                .build();

        this.deliveryOrdersMovements.get(deliveryOrder).add(valueAndCoordinate);
    }

    private void setLastNextMovement(RoutePlanningIndexes routePlanningIndexes, DeliveryOrder deliveryOrder) {

        setLastMovementAsOrderDestination(deliveryOrder);
        routePlanningIndexes.resetCharMovementIndex();
    }

    private void setLastMovementAsOrderDestination(DeliveryOrder deliveryOrder) {
        ValueAndCoordinate valueAndCoordinate = ValueAndCoordinate.builder()
                .name(this.actualCartesianDirection.name())
                .cardinalPoint(
                        new CardinalPoint(this.actualCardinalPoint.getXAxe(), this.actualCardinalPoint.getYAxe())
                )
                .build();

        this.endOfEachOrder.put(deliveryOrder, valueAndCoordinate);
    }

    private void buildCircularListOfCoordinates() {

        CircularValueAndCoordinate circularValueAndCoordinateX = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateY = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateNx = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateNy = CircularValueAndCoordinate.builder().build();

        circularValueAndCoordinateX.setBefore(circularValueAndCoordinateY);
        circularValueAndCoordinateX.setSame(
                ValueAndCoordinate.builder()
                        .name("x")
                        .cardinalPoint(new CardinalPoint(1, 0))
                        .build()
        );
        circularValueAndCoordinateX.setNext(circularValueAndCoordinateNy);

        circularValueAndCoordinateY.setBefore(circularValueAndCoordinateNx);
        circularValueAndCoordinateY.setSame(
                ValueAndCoordinate.builder()
                        .name("y")
                        .cardinalPoint(new CardinalPoint(0, 1))
                        .build()
        );
        circularValueAndCoordinateY.setNext(circularValueAndCoordinateX);

        circularValueAndCoordinateNx.setBefore(circularValueAndCoordinateNy);
        circularValueAndCoordinateNx.setSame(
                ValueAndCoordinate.builder()
                        .name("-x")
                        .cardinalPoint(new CardinalPoint(-1, 0))
                        .build()
        );
        circularValueAndCoordinateNx.setNext(circularValueAndCoordinateY);

        circularValueAndCoordinateNy.setBefore(circularValueAndCoordinateX);
        circularValueAndCoordinateNy.setSame(
                ValueAndCoordinate.builder()
                        .name("-y").cardinalPoint(new CardinalPoint(0, -1))
                        .build()
        );
        circularValueAndCoordinateNy.setNext(circularValueAndCoordinateNx);

        this.actualCircularListOfCoordinates = circularValueAndCoordinateY;
    }

    private void buildInitialValues() {
        this.actualCartesianDirection = CartesianDirection.Y;
        this.actualCardinalPoint = new CardinalPoint(0,0);
        this.endOfEachOrder = new HashMap<>();
        this.deliveryOrdersMovements = new HashMap<>();
    }

    @Override
    public void sendInformationToDroneManager() {
        byte routeId = 1;
        DroneCmd droneCmd = DroneCmd.builder().droneId(this.droneId).build();

        Set<DeliveryOrderCmd> linearListDeliveryOrdersCmds = buildLinearListDeliveryOrdersCmds(this.);

        RoutesPlanningToDroneManagerCmd.DeliveryDetailsCmd deliveryDetail = RoutesPlanningToDroneManagerCmd.DeliveryDetailsCmd.builder()

                .build();

        RoutesPlanningToDroneManagerCmd message = RoutesPlanningToDroneManagerCmd.builder()
                .droneCmd(droneCmd)
                .routeId(routeId)
                .deliveryDetailsCmd(deliveryDetail)
                .routesCoordinatesCmd(ValueAndCoordinateCmd.toCmds(this.deliveryOrdersMovements)) //Set
                .replyTo(this.getContext().getSelf())
                .build();

        childDroneManagerDtoCmdActorRef.tell(message);
    }

    private DeliveryOrderCmdDDDelete buildLinearListDeliveryOrdersCmds(Integer deliverId) {

        int incrementValue = 1;
        if (deliverId > deliveryOrdersMovements.size()) {
            ValueAndCoordinate valueAndCoordinate = deliveryOrdersMovements.get(deliverId);

            return DeliveryOrderCmdDDDelete.builder()
                    .id(deliverId.byteValue())
                    .valueAndCoordinateToBeDeliveredCmd(ValueAndCoordinateCmd.toCmd(valueAndCoordinate))
                    .build();
        }

        return null;
    }

    @Override
    public Behavior<RoutePlanningDtoCmd> respondToParentActuator(RoutesPlanningToFileManagerDto routesPlanningToFileManagerDto) {
        this.fatherRoutePlanningDtoCmdActorRef.tell(routesPlanningToFileManagerDto);

        return null;
    }

    @Override
    public Behavior<RoutePlanningDtoCmd> listenChildCall(DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto) {

        RoutesPlanningToFileManagerDto routesPlanningToFileManagerDtoCmd = RoutesPlanningToFileManagerDto.builder().build();
        droneManagerToRoutePlanningContainerDto.getDroneManagerToRoutePlanningDtos().forEach(droneManagerToRoutePlanningDto -> {
            routesPlanningToFileManagerDtoCmd.setDroneId(droneManagerToRoutePlanningDto.getDroneDto().getDroneId());
            routesPlanningToFileManagerDtoCmd.setDeliveryOrderReport(buildDeliveryOrderStringReport(droneManagerToRoutePlanningDto.getRoutesDto()));
        });

        respondToParentActuator(routesPlanningToFileManagerDtoCmd);

        return this;
    }

    private String buildDeliveryOrderStringReport(RoutesDto routesDto) {
        StringBuilder stringBuilder = new StringBuilder();
        this.endOfEachOrder.forEach(valueAndCoordinate -> {
            stringBuilder.append(valueAndCoordinate.getCartesianCoordinate().toString()).append(" dirección ")
                    .append(valueAndCoordinate.getName());
        });

        return stringBuilder.toString();
    }

    private void respondToParentActuatorErrorMessage(RoutePlanningToFileManagerDtoFailException routePlanningToFileManagerDtoFailException) {
        respondToParentActuator(routePlanningToFileManagerDtoFailException);
    }

    @Generated
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    private class DeliveryEncodedOrder {
        private DeliveryOrder deliveryOrder;
        private String encodedOrder;

    }
}
