package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.CardinalDirection;
import s4n.codechallenge.enums.DroneStatus;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Drone {
    private int id;
    private DroneStatus status;
    private CardinalPoint position;
    private CardinalDirection direction;

    public Drone() {
        this.status = DroneStatus.AVAILABLE;
        this.position = new CardinalPoint();
        this.direction = CardinalDirection.NORTE;
    }
}
