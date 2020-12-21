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
    private int xAxe;
    private int yAxe;

    public CartesianCoordinate() {
        xAxe = 0;
        yAxe = 0;
    }

    public void incrementValues(int x, int y) {
        xAxe += x;
        yAxe += y;
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
