package s4n.codechallenge.entities;

import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Generated
@Getter
@Setter
@NoArgsConstructor
public class RoutePlanningIndexes {
    int actualOrderIndex;
    int actualMovementCharIndex;

    public void incrementCharMovementIndex() {
        this.actualMovementCharIndex++;
    }

    public void incrementOrderIndex() {
        this.actualOrderIndex++;
    }

    public void resetCharMovementIndex() {
        this.actualMovementCharIndex = 0;
    }
}
