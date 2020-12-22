package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPoint;

@Generated
@Getter
@Setter
@Builder
public class CartesianCoordinateCmd {
    private int xAxe;
    private int yAxe;

    public static CartesianCoordinateCmd toCmd(CardinalPoint cardinalPoint) {
        return CartesianCoordinateCmd.builder()
                .xAxe(cardinalPoint.getXAxe())
                .yAxe(cardinalPoint.getYAxe())
                .build();
    }

    public static CardinalPoint toModel(CartesianCoordinateCmd cartesianCoordinateCmd) {
        return CardinalPoint.builder()
                .xAxe(cartesianCoordinateCmd.getXAxe())
                .yAxe(cartesianCoordinateCmd.getYAxe())
                .build();
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
