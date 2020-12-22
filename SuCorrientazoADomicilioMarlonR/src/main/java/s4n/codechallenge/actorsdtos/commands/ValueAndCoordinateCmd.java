package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPoint;

import java.util.Set;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
public class ValueAndCoordinateCmd {
    private String direction;
    private int x;
    private int y;

    public static CardinalPointWithDirectionCmd toModel(ValueAndCoordinateCmd valueAndCoordinateCmd) {
        CardinalPoint cardinalPoint = CardinalPoint.builder()
                .xAxe(valueAndCoordinateCmd.getX())
                .yAxe(valueAndCoordinateCmd.getY())
                .build();

        return CardinalPointWithDirectionCmd.builder()
                .cartesianDirection(valueAndCoordinateCmd.getDirection())
                .cardinalPointCmd(cardinalPoint)
                .build();
    }

    public static ValueAndCoordinateCmd toCmd(CardinalPointWithDirectionCmd cardinalPointWithDirectionCmd) {
        return ValueAndCoordinateCmd.builder()
                .direction(cardinalPointWithDirectionCmd.getCartesianDirection())
                .x(cardinalPointWithDirectionCmd.getCardinalPointCmd().getXAxe())
                .y(cardinalPointWithDirectionCmd.getCardinalPointCmd().getYAxe())
                .build();
    }

    public static Set<ValueAndCoordinateCmd> toCmds(Set<CardinalPointWithDirectionCmd> cartesianCoordinatesOfRoute) {
        return cartesianCoordinatesOfRoute.stream()
                .map(ValueAndCoordinateCmd::toCmd)
                .collect(Collectors.toSet());
    }
}
