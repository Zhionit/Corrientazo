package s4n.codechallenge.entities;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
public class Drone extends AbstractBehavior<DroneInformation> {

    private byte id;
    private DroneInformation droneInformation;

    public Drone(ActorContext<DroneInformation> context) {
        super(context);
    }

    public static Behavior<DroneInformation> create() {
        return Behaviors.setup(Drone::new);
    }

    @Override
    public Receive<DroneInformation> createReceive() {
        return newReceiveBuilder().onMessage(DroneInformation.class, this::updateStatus).build();
    }

    private Behavior<DroneInformation> updateStatus(DroneInformation droneInformation) {
        getContext().getLog().info("Updating {} drone status", getId());
        setDroneInformation(droneInformation);

        return this;
    }
}
