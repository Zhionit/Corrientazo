package s4n.codechallenge.enums;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
@AllArgsConstructor
public enum RouteStatus {
    WAITING("Waiting"), IN_PROGRESS("In Progress"), FINISHED("Finished");

    String value;

    @Override
    public String toString() {
            return getValue();
        }
}
