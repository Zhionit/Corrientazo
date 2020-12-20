package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CartesianCoordinate;

@Generated
@Getter
@Setter
@Builder
public class CartesianCoordinateCmd {
    private byte xAxe;
    private byte yAxe;

    public CartesianCoordinateCmd() {
        xAxe = 0;
        yAxe = 0;
    }

    public static CartesianCoordinateCmd toCmd(CartesianCoordinate cartesianCoordinate) {
        return CartesianCoordinateCmd.builder()
                .xAxe(cartesianCoordinate.getXAxe())
                .yAxe(cartesianCoordinate.getYAxe())
                .build();
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
