package s4n.codechallenge.services.impl;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.RoutePlanningDtoCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToRoutePlanningDto;
import s4n.codechallenge.actorsdtos.communication.FileManagerToRoutePlanningCmd;
import s4n.codechallenge.actorsdtos.communication.RoutePlanningToFileManagerDtoFailException;
import s4n.codechallenge.actorsdtos.communication.RoutesPlanningToDroneManagerCmd;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.CircularValueAndCoordinate;
import s4n.codechallenge.entities.ValueAndCoordinate;
import s4n.codechallenge.enums.CoordinatesDirection;
import s4n.codechallenge.services.RoutePlanning;

import java.util.ArrayList;
import java.util.List;

public class RoutePlanningImpl extends AbstractBehavior<RoutePlanningDtoCmd> implements RoutePlanning {

    private static final byte MAX_DRONES_ALLOWED = 20;
    private static final byte MAX_AMOUNT_ORDERS_ALLOWED = 3;
    public static final int MAX_CUADRAS_LENGTH = 10;
    public static final int MAX_CUADRAS_LENGTH_NEGATIVE = -10;

    //TODO - Crear los drones
    private String fileName;
    private List<String> encodedOrders;
    private byte droneId;
    private CartesianCoordinate actualCartesianCoordinate;
    private CoordinatesDirection actualCoordinatesDirection;
    private CircularValueAndCoordinate actualCircularListOfCoordinates;
    private int limiteDeCuadras;
    private List<ValueAndCoordinate> cartesianCoordinatesOfRoute;
    private List<ValueAndCoordinate> everyCartesianCoordinateEndOfRoute;

    private RoutesPlanningToDroneManagerCmd routesPlanningToDroneManagerCmd;
    private ActorRef<RoutePlanningDtoCmd> routePlanningDtoCmdActorRef;
    private final ActorRef<DroneManagerDtoCmd> droneManagerDtoCmdActorRef;


    public RoutePlanningImpl(ActorContext<RoutePlanningDtoCmd> context) {
        super(context);
        buildCircularListOfCoordinates();
        buildInitialValues();
        droneManagerDtoCmdActorRef = context.spawn(DroneManagerImpl.create(), "DroneManager");
    }


    public static Behavior<RoutePlanningDtoCmd> create() {
        return Behaviors.setup(RoutePlanningImpl::new);
    }

    @Override
    public Receive<RoutePlanningDtoCmd> createReceive() {
        ReceiveBuilder<RoutePlanningDtoCmd> routePlanningDtoCmdReceiveBuilder = newReceiveBuilder();

        routePlanningDtoCmdReceiveBuilder.onMessage(FileManagerToRoutePlanningCmd.class, this::extractDrone);

        return routePlanningDtoCmdReceiveBuilder.build();
    }

    @Override
    public Behavior<RoutePlanningDtoCmd> extractDrone(FileManagerToRoutePlanningCmd fileManagerToRoutePlanningCmd) {

        int digitAmountOfDroneIdNumber = 2;
        String cleanedFileName = fileManagerToRoutePlanningCmd.getFileName()
                .substring(fileManagerToRoutePlanningCmd.getFileName().length() - digitAmountOfDroneIdNumber);

        this.droneId = Byte.parseByte(cleanedFileName);
        validateDrone(this.droneId, fileManagerToRoutePlanningCmd.getEncodedOrders().size());

        generateDroneRoute(fileManagerToRoutePlanningCmd.getEncodedOrders(), 0, 0);

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
        RoutePlanningToFileManagerDtoFailException routePlanningToFileManagerDtoFailException = RoutePlanningToFileManagerDtoFailException.builder()
                .errorMessage(errorMessage)
                .droneId(droneId)
                .build();

        respondToParentActuatorErrorMessage(routePlanningToFileManagerDtoFailException);
    }

    @Override
    public void generateDroneRoute(List<String> encodedOrders, int actualEncodedOrderStarter, int actualMovementCharStarter) {

        int actualEncodedOrder = actualEncodedOrderStarter;
        int actualMovementChar = actualMovementCharStarter;

        char movementChar = readMovement(encodedOrders, actualEncodedOrder, actualMovementChar);
        interpretMovement(movementChar);
        applyMovement();
        saveCartesianCoordinate();
        hasNextMovement(encodedOrders, actualMovementChar, actualEncodedOrder);
    }

    private char readMovement(List<String> encodedOrders, int actualEncodedOrder, int actualMovementChar) {

        String encodedOrder = encodedOrders.get(actualEncodedOrder);
        char movementChar = encodedOrder.charAt(actualMovementChar);
        actualEncodedOrder ++;

        return movementChar;
    }

    private void interpretMovement(char movementChar) {

        switch (movementChar) {
            case 'D':
                this.actualCircularListOfCoordinates = findActualCoordinate(this.actualCircularListOfCoordinates).getNext();
                this.actualCoordinatesDirection = findCoordinatesDirection(this.actualCircularListOfCoordinates);
            case 'I':
                this.actualCircularListOfCoordinates = findActualCoordinate(this.actualCircularListOfCoordinates).getBefore();
                this.actualCoordinatesDirection = findCoordinatesDirection(this.actualCircularListOfCoordinates);
            case 'A':
                this.actualCoordinatesDirection = findCoordinatesDirection(this.actualCircularListOfCoordinates);
        }
    }

    private CoordinatesDirection findCoordinatesDirection(CircularValueAndCoordinate circularListOfCoordinates) {
        return CoordinatesDirection.valueOf(circularListOfCoordinates.getSame().getName());
    }

    private CircularValueAndCoordinate findActualCoordinate(CircularValueAndCoordinate circularListOfCoordinatesP) {

        CircularValueAndCoordinate circularListOfCoordinates = circularListOfCoordinatesP;

        if (this.actualCoordinatesDirection.getValue().equals(circularListOfCoordinates.getSame().getName())) {
            return circularListOfCoordinates;
        }

        circularListOfCoordinates = circularListOfCoordinates.getNext();
        findActualCoordinate(circularListOfCoordinates);

        return null;
    }

    private void applyMovement() {
        this.actualCartesianCoordinate.incrementValues(
                this.actualCircularListOfCoordinates.getSame().getX(),
                this.actualCircularListOfCoordinates.getSame().getY()
        );

        validateCuadrasLength(this.actualCartesianCoordinate);
    }

    private void validateCuadrasLength(CartesianCoordinate actualCartesianCoordinate) {
        if (actualCartesianCoordinate.getXAxe() > MAX_CUADRAS_LENGTH || actualCartesianCoordinate.getXAxe() < MAX_CUADRAS_LENGTH_NEGATIVE )
            buildErrorResponseToFileManager("El límite de cuadras a sido excedido", this.droneId);

        if (actualCartesianCoordinate.getYAxe() > MAX_CUADRAS_LENGTH || actualCartesianCoordinate.getYAxe() < MAX_CUADRAS_LENGTH_NEGATIVE )
            buildErrorResponseToFileManager("El límite de cuadras a sido excedido", this.droneId);

    }

    private void saveCartesianCoordinate() {
        ValueAndCoordinate valueAndCoordinate = ValueAndCoordinate.builder()
                .name(this.actualCoordinatesDirection.name())
                .x(this.actualCartesianCoordinate.getXAxe())
                .y(this.actualCartesianCoordinate.getYAxe())
                .build();

        this.cartesianCoordinatesOfRoute.add(valueAndCoordinate);
    }

    private void hasNextMovement(List<String> encodedOrders, int actualMovementChar, int actualEncodedOrder) {
        if (true)
            setLastMovementAsOrderDestination();
        else
            readMovement(encodedOrders, actualEncodedOrder, actualMovementChar);

    }

    private void setLastMovementAsOrderDestination() {
        ValueAndCoordinate valueAndCoordinate = ValueAndCoordinate.builder()
                .name(this.actualCoordinatesDirection.name())
                .x(this.actualCartesianCoordinate.getXAxe())
                .y(this.actualCartesianCoordinate.getYAxe())
                .build();

        this.everyCartesianCoordinateEndOfRoute.add(valueAndCoordinate);
    }

    private void buildCircularListOfCoordinates() {

        CircularValueAndCoordinate circularValueAndCoordinateX = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateY = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateNx = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateNy = CircularValueAndCoordinate.builder().build();

        circularValueAndCoordinateX.setBefore(circularValueAndCoordinateY);
        circularValueAndCoordinateX.setSame(new ValueAndCoordinate("x", 1, 0));
        circularValueAndCoordinateX.setNext(circularValueAndCoordinateNy);

        circularValueAndCoordinateY.setBefore(circularValueAndCoordinateNx);
        circularValueAndCoordinateY.setSame(new ValueAndCoordinate("y", 0, 1));
        circularValueAndCoordinateY.setNext(circularValueAndCoordinateX);

        circularValueAndCoordinateNx.setBefore(circularValueAndCoordinateNy);
        circularValueAndCoordinateNx.setSame(new ValueAndCoordinate("-x", -1, 0));
        circularValueAndCoordinateNx.setNext(circularValueAndCoordinateY);

        circularValueAndCoordinateNy.setBefore(circularValueAndCoordinateX);
        circularValueAndCoordinateNy.setSame(new ValueAndCoordinate("-y", 0, -1));
        circularValueAndCoordinateNy.setNext(circularValueAndCoordinateNx);

        this.actualCircularListOfCoordinates = circularValueAndCoordinateY;
    }

    private void buildInitialValues() {
        this.actualCoordinatesDirection = CoordinatesDirection.Y;
        this.actualCartesianCoordinate = new CartesianCoordinate(0,0);
        this.limiteDeCuadras = MAX_CUADRAS_LENGTH_NEGATIVE;
        this.everyCartesianCoordinateEndOfRoute = new ArrayList<>();
        this.cartesianCoordinatesOfRoute = new ArrayList<>();
    }

    @Override
    public void sendInformationToDroneManager() {

    }

    @Override
    public Behavior<RoutePlanningDtoCmd> respondToParentActuator(DroneManagerToRoutePlanningDto droneManagerToRoutePlanningDto) {
        return null;
    }

    private void respondToParentActuatorErrorMessage(RoutePlanningToFileManagerDtoFailException routePlanningToFileManagerDtoFailException) {

    }
}
