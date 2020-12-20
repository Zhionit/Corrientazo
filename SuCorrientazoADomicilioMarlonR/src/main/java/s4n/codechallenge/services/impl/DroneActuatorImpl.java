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
import s4n.codechallenge.actorsdtos.commands.DroneCmd;
import s4n.codechallenge.actorsdtos.commands.DroneInformationCmd;
import s4n.codechallenge.actorsdtos.commands.MoveDroneCmd;
import s4n.codechallenge.actorsdtos.dtos.DroneDto;
import s4n.codechallenge.actorsdtos.dtos.DroneInformationDto;
import s4n.codechallenge.entities.Drone;
import s4n.codechallenge.entities.DroneInformation;
import s4n.codechallenge.enums.DroneStatus;
import s4n.codechallenge.services.DroneActuator;

@Generated
@Builder
public class DroneActuatorImpl extends AbstractBehavior<DroneActuatorDtoCmd> implements DroneActuator {

    private DroneDto droneDto;
    private DroneCmd droneCmd;

    public DroneActuatorImpl(ActorContext<DroneActuatorDtoCmd> context) {
        super(context);
    }

    public static Behavior<DroneActuatorDtoCmd> create() {
        return Behaviors.setup(DroneActuatorImpl::new);
    }

    @Override
    public Receive<DroneActuatorDtoCmd> createReceive() {
        ReceiveBuilder<DroneActuatorDtoCmd> builder = newReceiveBuilder();

        builder.onMessage(DroneCmd.class, this::updateStatus);
        builder.onMessage(MoveDroneCmd.class, this::moveDrone);

        return builder.build();
    }

    private Behavior<DroneActuatorDtoCmd> updateStatus(DroneCmd droneCmd) {

            getContext().getLog().info("Updating {} drone status", droneCmd.getId());
            droneCmd.getReplyTo().tell(droneSyncDto);

            moveDrone(droneCmd);

        return this;
    }

    private Behavior<DroneActuatorDtoCmd> moveDrone(MoveDroneCmd moveDroneCmd) {
        getContext().getLog().info("Moving {} drone", moveDroneCmd.getId());
        //TODO - Add cache if enough time, else simulate db connection with fake data

        DroneInformation droneInformation = DroneInformationCmd.toModel(moveDroneCmd.getDroneInformationDto());
        droneInformation.setDroneStatus(DroneStatus.BUSY);

        Drone drone = new Drone();
        drone.setId(drone.getId());
        drone.setDroneInformation(droneInformation);

        this.droneDto = new DroneDto(drone.getId(), DroneInformationDto.toDto(drone.getDroneInformation()));

        return this;
    }
}
