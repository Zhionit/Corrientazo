package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class CartesianCoordinate {
    private byte xAxe;
    private byte yAxe;

    public CartesianCoordinate() {
        xAxe = 0;
        yAxe = 0;
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
