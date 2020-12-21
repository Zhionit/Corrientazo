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
    private int xAxe;
    private int yAxe;

    public static CartesianCoordinateCmd toCmd(CartesianCoordinate cartesianCoordinate) {
        return CartesianCoordinateCmd.builder()
                .xAxe(cartesianCoordinate.getXAxe())
                .yAxe(cartesianCoordinate.getYAxe())
                .build();
    }

    public static CartesianCoordinate toModel(CartesianCoordinateCmd cartesianCoordinateCmd) {
        return CartesianCoordinate.builder()
                .xAxe(cartesianCoordinateCmd.getXAxe())
                .yAxe(cartesianCoordinateCmd.getYAxe())
                .build();
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
