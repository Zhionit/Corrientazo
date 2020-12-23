package s4n.codechallenge.services.impl;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
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
import s4n.codechallenge.actorsdtos.commands.DeliveryOrderCmd;
import s4n.codechallenge.actorsdtos.commands.DroneCmd;
import s4n.codechallenge.actorsdtos.commands.RouteCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningContainerDto;
import s4n.codechallenge.actorsdtos.communication.FileManagerToRoutePlanningCmd;
import s4n.codechallenge.actorsdtos.communication.RoutePlanningToFileManagerDtoFailException;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToDroneManagerCmd;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToFileManagerDto;
import s4n.codechallenge.actorsdtos.dtos.RoutesDto;
import s4n.codechallenge.entities.CardinalPoint;
import s4n.codechallenge.entities.CardinalPointWithDirection;
import s4n.codechallenge.entities.CircularValueAndCoordinate;
import s4n.codechallenge.entities.DeliveryOrder;
import s4n.codechallenge.entities.RoutePlanningIndexes;
import s4n.codechallenge.actorsdtos.commands.CardinalPointWithDirectionCmd;
import s4n.codechallenge.enums.CartesianDirection;
import s4n.codechallenge.enums.CartesianMapperToCardinal;
import s4n.codechallenge.enums.DeliveryOrderStatus;
import s4n.codechallenge.services.RoutePlanning;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RoutePlanningImpl extends AbstractBehavior<RoutePlanningDtoCmd> implements RoutePlanning {

    private static final byte MAX_DRONES_ALLOWED = 20;
    private static final byte MAX_AMOUNT_ORDERS_ALLOWED = 3;
    public static final int MAX_CUADRAS_LENGTH = 10;

    public static final int MAX_CUADRAS_LENGTH_NEGATIVE = -10;

    public static final String IN_COME_ROUTE = "input";
    public static final String OUT_COME_ROUTE = "output";
    public static final String FILE_ENDING = ".txt";
    public static final String OUTPUT_FILE_NAME = "out";

    private byte droneId;
    private CardinalPoint actualCardinalPoint;
    private CartesianDirection actualCartesianDirection;
    private RoutesDto routesDto;

    private CircularValueAndCoordinate actualCircularListOfCoordinates;
    private Map<DeliveryOrder, Set<CardinalPointWithDirection>> deliveryOrdersMovements;
    private Map<DeliveryOrder, CardinalPointWithDirection> endOfEachOrder;

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

        routePlanningDtoCmdReceiveBuilder.onMessage(FileManagerToRoutePlanningCmd.class, this::listenParentCall);
        routePlanningDtoCmdReceiveBuilder.onMessage(DroneManagerToRoutePlanningContainerDto.class, this::listenChildCall);
        routePlanningDtoCmdReceiveBuilder.onSignal(PostStop.class, postStop -> onPostStop());

        return routePlanningDtoCmdReceiveBuilder.build();
    }

    private Behavior<RoutePlanningDtoCmd> onPostStop() {
        getContext().getSystem().log().info("Worker {} stopped", getContext().getSelf().toString());
        return this;
    }

    @Override
    public Behavior<RoutePlanningDtoCmd> listenParentCall(FileManagerToRoutePlanningCmd fileManagerToRoutePlanningCmd) {

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
    public void generateDroneRoute(Set<String> encodedOrders, RoutePlanningIndexes routePlanningIndexes) {

        routePlanningIndexes.incrementOrderIndex();
        Set<DeliveryEncodedOrder> deliveryEncodedOrders = new LinkedHashSet<>();

        encodedOrders.forEach(encodedOrder -> {

            DeliveryOrder deliveryOrder = DeliveryOrder.builder()
                    .id(routePlanningIndexes.getActualOrderIndex())
                    .deliveryOrderStatus(DeliveryOrderStatus.UNDELIVERED)
                    .build();

            DeliveryEncodedOrder deliveryEncodedOrder = DeliveryEncodedOrder.builder()
                    .deliveryOrder(deliveryOrder)
                    .encodedOrder(encodedOrder)
                    .build();

            deliveryEncodedOrders.add(deliveryEncodedOrder);

            this.deliveryOrdersMovements.put(deliveryOrder, new LinkedHashSet<>());
        });

        stepsToGenerateDroneRoute(deliveryEncodedOrders, routePlanningIndexes);
    }

    private void stepsToGenerateDroneRoute(Set<DeliveryEncodedOrder> deliveryEncodedOrders, RoutePlanningIndexes routePlanningIndexes) {

        routePlanningIndexes.resetCharMovementIndex();
        deliveryEncodedOrders.forEach(deliveryEncodedOrder -> {
            for (int i = 0; i < deliveryEncodedOrder.getEncodedOrder().length(); i++) {

                char movementChar = readMovement(deliveryEncodedOrder.getEncodedOrder(), routePlanningIndexes);
                interpretMovement(movementChar);
                applyMovement();
                addMovementToDeliveryOrder(deliveryEncodedOrder.getDeliveryOrder());

                if (deliveryEncodedOrder.encodedOrder.length() == routePlanningIndexes.getActualMovementCharIndex()) {
                    setLastNextMovement(routePlanningIndexes, deliveryEncodedOrder.getDeliveryOrder());
                }
            }
        });

        callChild(this.deliveryOrdersMovements, this.endOfEachOrder);
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
            default:
                String error = "El archivo contiene instrucciones inválidas";
                RoutePlanningToFileManagerDtoFailException routePlanningToFileManagerDtoFailException =
                        new RoutePlanningToFileManagerDtoFailException();
                routePlanningToFileManagerDtoFailException.setErrorMessage(error);

                respondToParentActuatorErrorMessage(routePlanningToFileManagerDtoFailException);
                break;
        }
    }

    private CartesianDirection findCoordinatesDirection(CircularValueAndCoordinate circularListOfCoordinates) {
        return Arrays.stream(CartesianDirection.values())
                .filter(cartesianDirection -> cartesianDirection.getValue()
                        .equals(circularListOfCoordinates.getSame().getCartesianDirection().getValue())
                ).findFirst().get();
    }

    private CircularValueAndCoordinate findActualCoordinate(CircularValueAndCoordinate circularListOfCoordinates) {

        if (this.actualCartesianDirection.getValue().equals(circularListOfCoordinates.getSame().getCartesianDirection().getValue())) {
            return circularListOfCoordinates;
        }

        circularListOfCoordinates = circularListOfCoordinates.getNext();
        return findActualCoordinate(circularListOfCoordinates);
    }

    private void applyMovement() {

        this.actualCardinalPoint.incrementValues(
                this.actualCircularListOfCoordinates.getSame().getCardinalPoint().getXAxe(),
                this.actualCircularListOfCoordinates.getSame().getCardinalPoint().getYAxe()
        );

        validateCuadrasLength(this.actualCardinalPoint);

        this.actualCartesianDirection = this.actualCircularListOfCoordinates.getSame().getCartesianDirection();
    }

    private void validateCuadrasLength(CardinalPoint actualCardinalPoint) {
        if (actualCardinalPoint.getXAxe() > MAX_CUADRAS_LENGTH || actualCardinalPoint.getXAxe() < MAX_CUADRAS_LENGTH_NEGATIVE )
            buildErrorResponseToFileManager("El límite de cuadras a sido excedido", this.droneId);

        if (actualCardinalPoint.getYAxe() > MAX_CUADRAS_LENGTH || actualCardinalPoint.getYAxe() < MAX_CUADRAS_LENGTH_NEGATIVE )
            buildErrorResponseToFileManager("El límite de cuadras a sido excedido", this.droneId);

    }

    private void addMovementToDeliveryOrder(DeliveryOrder deliveryOrder) {

        CardinalPointWithDirection cardinalPointWithDirection = CardinalPointWithDirection.builder()
                .cartesianDirection(this.actualCartesianDirection)
                .cardinalPoint(
                        new CardinalPoint(this.actualCardinalPoint.getXAxe(), this.actualCardinalPoint.getYAxe())
                )
                .build();

        this.deliveryOrdersMovements.get(deliveryOrder).add(cardinalPointWithDirection);
    }

    private void setLastNextMovement(RoutePlanningIndexes routePlanningIndexes, DeliveryOrder deliveryOrder) {

        setLastMovementAsOrderDestination(deliveryOrder);
        routePlanningIndexes.resetCharMovementIndex();
    }

    private void setLastMovementAsOrderDestination(DeliveryOrder deliveryOrder) {
        CardinalPointWithDirection cardinalPointWithDirection = CardinalPointWithDirection.builder()
                .cartesianDirection(this.actualCartesianDirection)
                .cardinalPoint(
                        new CardinalPoint(this.actualCardinalPoint.getXAxe(), this.actualCardinalPoint.getYAxe())
                )
                .build();

        this.endOfEachOrder.put(deliveryOrder, cardinalPointWithDirection);
    }

    private void buildCircularListOfCoordinates() {

        CircularValueAndCoordinate circularValueAndCoordinateX = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateY = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateNx = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateNy = CircularValueAndCoordinate.builder().build();

        circularValueAndCoordinateX.setBefore(circularValueAndCoordinateY);
        circularValueAndCoordinateX.setSame(
                CardinalPointWithDirection.builder()
                        .cartesianDirection(CartesianDirection.X)
                        .cardinalPoint(new CardinalPoint(1, 0))
                        .build()
        );
        circularValueAndCoordinateX.setNext(circularValueAndCoordinateNy);

        circularValueAndCoordinateY.setBefore(circularValueAndCoordinateNx);
        circularValueAndCoordinateY.setSame(
                CardinalPointWithDirection.builder()
                        .cartesianDirection(CartesianDirection.Y)
                        .cardinalPoint(new CardinalPoint(0, 1))
                        .build()
        );
        circularValueAndCoordinateY.setNext(circularValueAndCoordinateX);

        circularValueAndCoordinateNx.setBefore(circularValueAndCoordinateNy);
        circularValueAndCoordinateNx.setSame(
                CardinalPointWithDirection.builder()
                        .cartesianDirection(CartesianDirection.NX)
                        .cardinalPoint(new CardinalPoint(-1, 0))
                        .build()
        );
        circularValueAndCoordinateNx.setNext(circularValueAndCoordinateY);

        circularValueAndCoordinateNy.setBefore(circularValueAndCoordinateX);
        circularValueAndCoordinateNy.setSame(
                CardinalPointWithDirection.builder()
                        .cartesianDirection(CartesianDirection.NY)
                        .cardinalPoint(new CardinalPoint(0, -1))
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
    public void callChild(Map<DeliveryOrder, Set<CardinalPointWithDirection>> cardinalPointsOfEachDelivery,
                          Map<DeliveryOrder, CardinalPointWithDirection> deliveryPoints) {

        int routeId = 1;
        DroneCmd droneCmd = DroneCmd.builder().droneId(this.droneId).build();

        Set<CardinalPointWithDirectionCmd> cardinalPointsCmds = new LinkedHashSet<>();
        cardinalPointsOfEachDelivery.forEach((deliveryOrder, cardinalPointWithDirections) -> {
            cardinalPointsCmds.addAll(CardinalPointWithDirectionCmd.toCmds(cardinalPointWithDirections));
        });

        Set<DeliveryOrderCmd> deliveryOrdersCmds = new LinkedHashSet<>();
        deliveryPoints.forEach((deliveryOrder, cardinalPointWithDirection) -> {
            deliveryOrdersCmds.add(DeliveryOrderCmd.builder()
                    .id(deliveryOrder.getId())
                    .cardinalPointWithDirectionCmd(CardinalPointWithDirectionCmd.toCmd(cardinalPointWithDirection))
                    .build()
            );
        });

        RouteCmd routeCmd = RouteCmd.builder()
                .routeId(routeId)
                .drone(droneCmd)
                .cardinalPointCmds(cardinalPointsCmds)
                .deliveryOrdersCmds(deliveryOrdersCmds)
                .build();

        RoutesPlanningToDroneManagerCmd routesPlanningToDroneManagerCmd = RoutesPlanningToDroneManagerCmd.builder()
                .routeCmd(routeCmd)
                .replyTo(this.getContext().getSelf())
                .build();

        childDroneManagerDtoCmdActorRef.tell(routesPlanningToDroneManagerCmd);
    }

    @Override
    public Behavior<RoutePlanningDtoCmd> respondToParentActuator(RoutesPlanningToFileManagerDto routesPlanningToFileManagerDto) {

        this.fatherRoutePlanningDtoCmdActorRef.tell(routesPlanningToFileManagerDto);

        String deliveryOrderReport = routesPlanningToFileManagerDto.getDeliveryOrderReport();

        if (Objects.isNull(deliveryOrderReport)) {
            deliveryOrderReport = routesPlanningToFileManagerDto.getErrorMessage();
        }

        String fileContent = buildFileContent(deliveryOrderReport);
        byte[] dataBytes = fileContent.getBytes();

        Path path = Paths.get(buildOutputNameFile(routesPlanningToFileManagerDto.getDroneId()));

        writeFiles(path, dataBytes);

        onPostStop();
        return this;
    }

    private void writeFiles(Path path, byte[] dataBytes) {
        try {
            Files.write(path, dataBytes);
        } catch (IOException e) {
            getContext().getLog().error("Error writing file");
        }
    }

    private String buildOutputNameFile(int droneId) {
        return buildDirectoryRoute(OUT_COME_ROUTE + File.separator + OUTPUT_FILE_NAME + droneId + FILE_ENDING);
    }

    private String buildDirectoryRoute(String inComeRoute) {
        return new StringBuilder()
                .append(".")
                .append(File.separator)
                .append("src")
                .append(File.separator)
                .append("main")
                .append(File.separator)
                .append("resources")
                .append(File.separator)
                .append(inComeRoute)
                .append(File.separator)
                .toString();
    }

    private String buildFileContent(String deliveryOrderReport) {

        String headerString = "== Reporte de entregas ==";
        StringBuilder stringBuilder = new StringBuilder(headerString);
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append(deliveryOrderReport);

        return stringBuilder.toString();
    }
    @Override
    public Behavior<RoutePlanningDtoCmd> listenChildCall(DroneManagerToRoutePlanningContainerDto droneManagerToRoutePlanningContainerDto) {

        RoutesPlanningToFileManagerDto routesPlanningToFileManagerDtoCmd = RoutesPlanningToFileManagerDto.builder().build();
        droneManagerToRoutePlanningContainerDto.getDroneManagerToRoutePlanningDtos().forEach(droneManagerToRoutePlanningDto -> {
            routesPlanningToFileManagerDtoCmd.setDroneId(droneManagerToRoutePlanningDto.getDroneDto().getDroneId());
            routesPlanningToFileManagerDtoCmd.setDeliveryOrderReport(buildDeliveryOrderStringReport(droneManagerToRoutePlanningDto.getRoutesDto()));
        });

        //all of this has been done, because some times the Actuator parent dies and can perform the call
        respondToParentActuator(routesPlanningToFileManagerDtoCmd);

        return this;
    }

    private void cleanCache() {

        this.deliveryOrdersMovements = new HashMap<>();
        this.endOfEachOrder = new HashMap<>();
        this.droneId = 0;
        this.actualCardinalPoint = new CardinalPoint();
        this.actualCircularListOfCoordinates = CircularValueAndCoordinate.builder().build();
        this.routesDto = RoutesDto.builder().build();
    }

    private String buildDeliveryOrderStringReport(RoutesDto routesDto) {
        StringBuilder stringBuilder = new StringBuilder();
        this.endOfEachOrder.forEach((deliveryOrder, cardinalPointWithDirection) -> {
            stringBuilder.append(cardinalPointWithDirection.getCardinalPoint().toString()).append(" dirección ")
                    .append(CartesianMapperToCardinal.valueOf(cardinalPointWithDirection.getCartesianDirection().name()).toString());
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
    private static class DeliveryEncodedOrder {
        private DeliveryOrder deliveryOrder;
        private String encodedOrder;
    }
}
