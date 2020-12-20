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
import s4n.codechallenge.actorsdtos.commands.DroneActuatorCmd;
import s4n.codechallenge.actorsdtos.commands.DroneInformationCmd;
import s4n.codechallenge.actorsdtos.commands.MoveDroneCmd;
import s4n.codechallenge.actorsdtos.dtos.MoveDroneDto;
import s4n.codechallenge.actorsdtos.dtos.SyncDroneDto;
import s4n.codechallenge.actorsdtos.dtos.DroneInformationDto;
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

        builder.onMessage(DroneActuatorCmd.class, this::syncDrone);
        builder.onMessage(MoveDroneCmd.class, this::moveDrone);

        return builder.build();
    }

    private Behavior<DroneActuatorDtoCmd> syncDrone(DroneActuatorCmd droneActuatorCmd) {
        getContext().getLog().info("Syncing {} drone status", droneActuatorCmd.getId());
        Optional<Drone> droneOptional = findDrone(droneActuatorCmd.getId());

        droneOptional.ifPresent(drone -> droneActuatorCmd.getReplyTo().tell(SyncDroneDto.toDto(drone)));
        return this;
    }

    private Optional<Drone> findDrone(byte id) {
        return this.drones.stream()
                .filter(syncDroneDto -> syncDroneDto.getId() == id)
                .findFirst();
    }

    private Behavior<DroneActuatorDtoCmd> moveDrone(MoveDroneCmd moveDroneCmd) {
        getContext().getLog().info("Moving {} drone", moveDroneCmd.getDroneId());
        //TODO - Add cache if enough time, else simulate db connection with fake data

        Optional<Drone> droneOptional = findDrone(moveDroneCmd.getDroneId());
        droneOptional.ifPresent(drone -> {
            DroneInformation droneInformation = DroneInformationCmd.toModel(moveDroneCmd.getDroneInformationDto());
            droneInformation.setDroneStatus(DroneStatus.BUSY);

            drone.setDroneInformation(droneInformation);

            DroneInformationDto droneInformationDto = DroneInformationDto.toDto(droneInformation);
            MoveDroneDto moveDroneDto = new MoveDroneDto(drone.getId(), droneInformationDto);

            moveDroneCmd.getReplyTo().tell(moveDroneDto);
        });

        return this;
    }
}
