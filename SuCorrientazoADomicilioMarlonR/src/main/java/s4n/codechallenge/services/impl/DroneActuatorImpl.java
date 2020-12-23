package s4n.codechallenge.services.impl;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.DroneManagerDtoCmd;
import s4n.codechallenge.actorsdtos.commands.CardinalPointCmd;
import s4n.codechallenge.actorsdtos.commands.CardinalPointWithDirectionCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorSyncCmd;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerMoveDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto.SyncDroneDto;
import s4n.codechallenge.actorsdtos.dtos.CardinalPointDto;
import s4n.codechallenge.actorsdtos.dtos.CardinalPointWithDirectionDto;
import s4n.codechallenge.actorsdtos.dtos.DroneDto;
import s4n.codechallenge.entities.CardinalPoint;
import s4n.codechallenge.entities.Drone;
import s4n.codechallenge.enums.CartesianDirection;
import s4n.codechallenge.enums.DroneStatus;
import s4n.codechallenge.services.DroneActuator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DroneActuatorImpl extends AbstractBehavior<DroneActuatorDtoCmd> implements DroneActuator {

    private List<Drone> drones;
    private int AMOUNT_OF_DRONES = 20;

    private ActorRef<DroneManagerDtoCmd> parentReplyTo;

    public DroneActuatorImpl(ActorContext<DroneActuatorDtoCmd> context) {
        super(context);
    }

    public static Behavior<DroneActuatorDtoCmd> create() {
        return Behaviors.setup(DroneActuatorImpl::new);
    }

    @Override
    public Receive<DroneActuatorDtoCmd> createReceive() {
        createDrones(AMOUNT_OF_DRONES);
        ReceiveBuilder<DroneActuatorDtoCmd> builder = newReceiveBuilder();

        builder.onMessage(DroneManagerToDroneActuatorSyncCmd.class, this::syncDrone);
        builder.onMessage(DroneManagerToDroneActuatorMoveCmd.class, this::moveDroneInCartesianCoordinate);

        return builder.build();
    }

    private void createDrones(int amountOfDrones) {
        this.drones = new ArrayList<>();
        for (int i = 0; i < amountOfDrones; i++) {
            Drone drone = Drone.builder()
                    .id(i)
                    .status(DroneStatus.AVAILABLE)
                    .direction(CartesianDirection.Y)
                    .position(new CardinalPoint())
                    .build();

            this.drones.add(drone);
        }
    }

    private Behavior<DroneActuatorDtoCmd> syncDrone(DroneManagerToDroneActuatorSyncCmd droneManagerToDroneActuatorSyncCmd) {
        int noOrderId = 0;
        CardinalPointWithDirectionDto defaultCardinalPointWithDirection = CardinalPointWithDirectionDto.builder()
                .cardinalPointDto(new CardinalPointDto())
                .cartesianDirection(CartesianDirection.Y)
                .build();

        getContext().getLog().info("Syncing {} drone status", droneManagerToDroneActuatorSyncCmd.getDroneCmd());
        this.parentReplyTo = droneManagerToDroneActuatorSyncCmd.getReplyTo();

        Optional<Drone> droneOptional = findDrone(droneManagerToDroneActuatorSyncCmd.getDroneCmd().getDroneId());

        droneOptional.ifPresent(drone -> {

            DroneDto droneDto = new DroneDto(droneManagerToDroneActuatorSyncCmd.getDroneCmd().getDroneId());
            SyncDroneDto syncDrone = SyncDroneDto.builder()
                    .orderId(noOrderId)
                    .droneDto(droneDto)
                    .cardinalPointWithDirectionDto(defaultCardinalPointWithDirection)
                    .build();

            DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto =
                    DroneActuatorToDroneManagerSyncDroneDto.builder()
                            .syncDroneDto(syncDrone)
                            .build();

            syncRespondToParentActuator(droneActuatorToDroneManagerSyncDroneDto);
        });

        return this;
    }

    private Optional<Drone> findDrone(int droneId) {
        return this.drones.stream()
                .filter(syncDroneDto -> syncDroneDto.getId() == droneId)
                .findFirst();
    }

    private Behavior<DroneActuatorDtoCmd> moveDroneInCartesianCoordinate(DroneManagerToDroneActuatorMoveCmd droneManagerToDroneActuatorMoveCmd) {
        getContext()
                .getLog()
                .info("Moving {} drone", droneManagerToDroneActuatorMoveCmd.getMoveDroneCmd().getDroneCmd().getDroneId());
        //TODO - Add cache if enough time, else simulate db connection with fake data

        Optional<Drone> droneOptional = findDrone(droneManagerToDroneActuatorMoveCmd.getMoveDroneCmd().getDroneCmd().getDroneId());
        droneOptional.ifPresent(drone -> {

            drone.setStatus(DroneStatus.BUSY);
            CardinalPointWithDirectionCmd cardinalPointWithDirectionCmd = droneManagerToDroneActuatorMoveCmd
                    .getMoveDroneCmd()
                    .getCardinalPointWithDirectionCmd();

            drone.setDirection(cardinalPointWithDirectionCmd.getCartesianDirection());
            CardinalPoint cardinalPoint = CardinalPointCmd.toModel(cardinalPointWithDirectionCmd.getCardinalPointCmd());
            drone.setPosition(cardinalPoint);

            CardinalPointWithDirectionDto cardinalPointWithDirectionDto = CardinalPointWithDirectionDto.builder()
                    .cartesianDirection(cardinalPointWithDirectionCmd.getCartesianDirection())
                    .cardinalPointDto(CardinalPointDto.toDto(cardinalPoint))
                    .build();

            SyncDroneDto syncDroneDto = SyncDroneDto.builder()
                    .orderId(droneManagerToDroneActuatorMoveCmd.getMoveDroneCmd().getOrderCmd().getId())
                    .droneDto(new DroneDto(drone.getId()))
                    .cardinalPointWithDirectionDto(cardinalPointWithDirectionDto)
                    .build();
            DroneActuatorToDroneManagerMoveDroneDto moveDroneDto = new DroneActuatorToDroneManagerMoveDroneDto(syncDroneDto);

            droneManagerToDroneActuatorMoveCmd.getReplyTo().tell(moveDroneDto);
        });

        return this;
    }

    @Override
    public void syncRespondToParentActuator(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto) {
        replyToParentActuator(droneActuatorToDroneManagerSyncDroneDto);
    }

    private void replyToParentActuator(DroneActuatorToDroneManagerSyncDroneDto droneActuatorToDroneManagerSyncDroneDto) {
        this.parentReplyTo.tell(droneActuatorToDroneManagerSyncDroneDto);
    }

    @Override
    public Behavior<DroneManagerDtoCmd> moveRespondToParentActuator(DroneActuatorToDroneManagerMoveDroneDto droneActuatorToDroneManagerMoveDroneDto) {
        return null;
    }

    @Override
    public Behavior<DroneActuatorDtoCmd> syncListenParentActuator(DroneManagerToDroneActuatorSyncCmd droneManagerToDroneActuatorSyncCmd) {
        return null;
    }

    @Override
    public Behavior<DroneActuatorDtoCmd> moveListenParentActuator(DroneManagerToDroneActuatorMoveCmd droneManagerToDroneActuatorMoveCmd) {
        return null;
    }
}
