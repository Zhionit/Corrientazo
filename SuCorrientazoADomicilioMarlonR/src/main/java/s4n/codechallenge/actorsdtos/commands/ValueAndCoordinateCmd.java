package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.ValueAndCoordinate;

@Generated
@Getter
@Setter
@Builder
public class ValueAndCoordinateCmd {
    private String name;
    private int x;
    private int y;

    public static ValueAndCoordinate toModel(ValueAndCoordinateCmd valueAndCoordinateCmd) {
        CartesianCoordinate cartesianCoordinate = CartesianCoordinate.builder()
                .xAxe(valueAndCoordinateCmd.getX())
                .yAxe(valueAndCoordinateCmd.getY())
                .build();

        return ValueAndCoordinate.builder()
                .name(valueAndCoordinateCmd.getName())
                .cartesianCoordinate(cartesianCoordinate)
                .build();
    }

    public static ValueAndCoordinateCmd toCmd(ValueAndCoordinate valueAndCoordinate) {
        return ValueAndCoordinateCmd.builder()
                .name(valueAndCoordinate.getName())
                .x(valueAndCoordinate.getCartesianCoordinate().getXAxe())
                .y(valueAndCoordinate.getCartesianCoordinate().getYAxe())
                .build();
    }
}
