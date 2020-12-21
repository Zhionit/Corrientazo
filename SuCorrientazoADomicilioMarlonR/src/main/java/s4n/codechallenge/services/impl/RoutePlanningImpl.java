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
import java.util.Arrays;
import java.util.List;

public class RoutePlanningImpl extends AbstractBehavior<RoutePlanningDtoCmd> implements RoutePlanning {

    private static final byte MAX_DRONES_ALLOWED = 20;
    private static final byte MAX_AMOUNT_ORDERS_ALLOWED = 3;
    //TODO - Crear los drones
    private String fileName;
    private List<String> encodedOrders;
    private byte droneId;
    private CartesianCoordinate actualCartesianCoordinate;
    private CoordinatesDirection actualCoordinatesDirection;

    private RoutesPlanningToDroneManagerCmd routesPlanningToDroneManagerCmd;
    private ActorRef<RoutePlanningDtoCmd> routePlanningDtoCmdActorRef;
    private final ActorRef<DroneManagerDtoCmd> droneManagerDtoCmdActorRef;
    private List<CircularValueAndCoordinate> circularListOfCoordinates;


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

        generateDroneRoute(fileManagerToRoutePlanningCmd.getEncodedOrders());

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
    public void generateDroneRoute(List<String> encodedOrders) {

        readMovement();
        interpretMovement();
        applyMovement();
        saveCartesianCoordinate();
        hasNextMovement();

    }

    private void readMovement() {

    }

    private void interpretMovement() {

    }

    private void applyMovement() {

    }

    private void saveCartesianCoordinate() {

    }

    private void hasNextMovement() {
        if (true)
            setLastMovementAsOrderDestination();
        else
            readMovement();

    }

    private void setLastMovementAsOrderDestination() {
    }

    private void buildCircularListOfCoordinates() {

        CircularValueAndCoordinate circularValueAndCoordinateX = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateY = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateNx = CircularValueAndCoordinate.builder().build();
        CircularValueAndCoordinate circularValueAndCoordinateNy = CircularValueAndCoordinate.builder().build();

        circularValueAndCoordinateX.setBefore(circularValueAndCoordinateY);
        circularValueAndCoordinateX.setSame(new ValueAndCoordinate("x", 1, 0));
        circularValueAndCoordinateX.setAfter(circularValueAndCoordinateNy);

        circularValueAndCoordinateY.setBefore(circularValueAndCoordinateNx);
        circularValueAndCoordinateY.setSame(new ValueAndCoordinate("y", 0, 1));
        circularValueAndCoordinateY.setAfter(circularValueAndCoordinateX);

        circularValueAndCoordinateNx.setBefore(circularValueAndCoordinateNy);
        circularValueAndCoordinateNx.setSame(new ValueAndCoordinate("-x", -1, 0));
        circularValueAndCoordinateNx.setAfter(circularValueAndCoordinateY);

        circularValueAndCoordinateNy.setBefore(circularValueAndCoordinateX);
        circularValueAndCoordinateNy.setSame(new ValueAndCoordinate("-y", 0, -1));
        circularValueAndCoordinateNy.setAfter(circularValueAndCoordinateNx);

        this.circularListOfCoordinates = Arrays.asList(circularValueAndCoordinateX, circularValueAndCoordinateY,
                circularValueAndCoordinateNx, circularValueAndCoordinateNy);
    }

    private void buildInitialValues() {
        this.actualCoordinatesDirection = CoordinatesDirection.Y;
        this.actualCartesianCoordinate = new CartesianCoordinate(0,0);
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
