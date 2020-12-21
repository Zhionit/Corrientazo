package s4n.codechallenge.entities;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
@Builder
public class CircularValueAndCoordinate {
    private CircularValueAndCoordinate before;
    private ValueAndCoordinate same;
    private CircularValueAndCoordinate next;
}
