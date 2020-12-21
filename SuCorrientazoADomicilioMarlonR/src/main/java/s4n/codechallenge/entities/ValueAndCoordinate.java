package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ValueAndCoordinate {
    private String name;
    private CartesianCoordinate cartesianCoordinate;
}
