package s4n.codechallenge.services.impl;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import lombok.Builder;
import lombok.Generated;
import s4n.codechallenge.actorsdtos.DroneActuatorDtoCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorMoveDroneCmd;
import s4n.codechallenge.actorsdtos.communication.DroneManagerToDroneActuatorSyncCmd;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerMoveDroneDto;
import s4n.codechallenge.actorsdtos.communication.DroneActuatorToDroneManagerSyncDroneDto;
import s4n.codechallenge.actorsdtos.dtos.DroneInformationDto;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.Drone;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.enums.DroneStatus;
import s4n.codechallenge.services.DroneActuator;

import java.util.List;
import java.util.Optional;

@Generated
@Builder
public class DroneActuatorImpl extends AbstractBehavior<DroneActuatorDtoCmd> implements DroneActuator {

    private List<Drone> drones;

    public DroneActuatorImpl(ActorContext<DroneActuatorDtoCmd> context) {
        super(context);
    }

    public static Behavior<DroneActuatorDtoCmd> create() {
        return Behaviors.setup(DroneActuatorImpl::new);
    }

    @Override
    public Receive<DroneActuatorDtoCmd> createReceive() {
        ReceiveBuilder<DroneActuatorDtoCmd> builder = newReceiveBuilder();

        builder.onMessage(DroneManagerToDroneActuatorSyncCmd.class, this::syncDrone);
        builder.onMessage(DroneManagerToDroneActuatorMoveDroneCmd.class, this::moveDroneInCartesianCoordinate);

        return builder.build();
    }

    private Behavior<DroneActuatorDtoCmd> syncDrone(DroneManagerToDroneActuatorSyncCmd droneManagerToDroneActuatorSyncCmd) {
        getContext().getLog().info("Syncing {} drone status", droneManagerToDroneActuatorSyncCmd.getDroneId());
        Optional<Drone> droneOptional = findDrone(droneManagerToDroneActuatorSyncCmd.getDroneId());

        droneOptional.ifPresent(drone -> droneManagerToDroneActuatorSyncCmd.getReplyTo().tell(DroneActuatorToDroneManagerSyncDroneDto.toDto(drone)));
        return this;
    }

    private Optional<Drone> findDrone(byte id) {
        return this.drones.stream()
                .filter(syncDroneDto -> syncDroneDto.getId() == id)
                .findFirst();
    }

    private Behavior<DroneActuatorDtoCmd> moveDroneInCartesianCoordinate(DroneManagerToDroneActuatorMoveDroneCmd droneManagerToDroneActuatorMoveDroneCmd) {
        getContext().getLog().info("Moving {} drone", droneManagerToDroneActuatorMoveDroneCmd.getDroneId());
        //TODO - Add cache if enough time, else simulate db connection with fake data

        Optional<Drone> droneOptional = findDrone(droneManagerToDroneActuatorMoveDroneCmd.getDroneId());
        droneOptional.ifPresent(drone -> {

            CartesianCoordinate cartesianCoordinate = CartesianCoordinate.builder()
                    .xAxe(droneManagerToDroneActuatorMoveDroneCmd.getDroneInformationCmd().getCartesianCoordinateCmd().getXAxe())
                    .yAxe(droneManagerToDroneActuatorMoveDroneCmd.getDroneInformationCmd().getCartesianCoordinateCmd().getYAxe())
                    .build();

            DroneInformation droneInformation = DroneInformation.builder()
                    .cardinalDirection(droneManagerToDroneActuatorMoveDroneCmd.getDroneInformationCmd().getCardinalDirection())
                    .droneStatus(DroneStatus.BUSY)
                    .cartesianCoordinate(cartesianCoordinate)
                    .build();
            droneInformation.setDroneStatus(DroneStatus.BUSY);

            drone.setDroneInformation(droneInformation);

            DroneInformationDto droneInformationDto = DroneInformationDto.toDto(droneInformation);
            DroneActuatorToDroneManagerMoveDroneDto moveDroneDto = new DroneActuatorToDroneManagerMoveDroneDto(drone.getId(), droneInformationDto);

            droneManagerToDroneActuatorMoveDroneCmd.getReplyTo().tell(moveDroneDto);
        });

        return this;
    }
}
