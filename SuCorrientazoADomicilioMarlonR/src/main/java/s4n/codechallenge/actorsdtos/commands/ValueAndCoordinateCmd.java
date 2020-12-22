package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPoint;
import s4n.codechallenge.entities.ValueAndCoordinate;

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

    public static ValueAndCoordinate toModel(ValueAndCoordinateCmd valueAndCoordinateCmd) {
        CardinalPoint cardinalPoint = CardinalPoint.builder()
                .xAxe(valueAndCoordinateCmd.getX())
                .yAxe(valueAndCoordinateCmd.getY())
                .build();

        return ValueAndCoordinate.builder()
                .name(valueAndCoordinateCmd.getDirection())
                .cardinalPoint(cardinalPoint)
                .build();
    }

    public static ValueAndCoordinateCmd toCmd(ValueAndCoordinate valueAndCoordinate) {
        return ValueAndCoordinateCmd.builder()
                .direction(valueAndCoordinate.getName())
                .x(valueAndCoordinate.getCardinalPoint().getXAxe())
                .y(valueAndCoordinate.getCardinalPoint().getYAxe())
                .build();
    }

    public static Set<ValueAndCoordinateCmd> toCmds(Set<ValueAndCoordinate> cartesianCoordinatesOfRoute) {
        return cartesianCoordinatesOfRoute.stream()
                .map(ValueAndCoordinateCmd::toCmd)
                .collect(Collectors.toSet());
    }
}
