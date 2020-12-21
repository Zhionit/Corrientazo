package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CircularValueAndCoordinate;
import s4n.codechallenge.entities.RouteCoordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
public class RouteCoordinatesCmd {
    private final ValueAndCoordinateCmd beforeCmd;
    private final ValueAndCoordinateCmd actualCmd;
    private final ValueAndCoordinateCmd nextCmd;

    public static RouteCoordinates toModel(RouteCoordinatesCmd routeCoordinatesCmd) {

        return RouteCoordinates.builder()
                .beforeOptional(Optional.ofNullable(ValueAndCoordinateCmd.toModel(routeCoordinatesCmd.getBeforeCmd())))
                .actualOptional(Optional.ofNullable(ValueAndCoordinateCmd.toModel(routeCoordinatesCmd.getActualCmd())))
                .nextOptional(Optional.ofNullable(ValueAndCoordinateCmd.toModel(routeCoordinatesCmd.getNextCmd())))
                .build();
    }

    public static List<RouteCoordinates> toModels(List<RouteCoordinatesCmd> routeCoordinatesCmds) {

        return routeCoordinatesCmds.stream()
                .map(RouteCoordinatesCmd::toModel)
                .collect(Collectors.toList());
    }

    private static RouteCoordinatesCmd toCmd(CircularValueAndCoordinate actualCircularListOfCoordinates) {

        RouteCoordinatesCmd routeCoordinatesCmd = buildRouteCoordinator(actualCircularListOfCoordinates, null);
        return routeCoordinatesCmd;
    }

    public static List<RouteCoordinatesCmd> toCmds(CircularValueAndCoordinate actualCircularListOfCoordinates) {

        List<RouteCoordinatesCmd> routeCoordinatesCmds = new ArrayList<>();
        routeCoordinatesCmds.add(toCmd(actualCircularListOfCoordinates));
        return routeCoordinatesCmds;
    }

    private static RouteCoordinatesCmd buildRouteCoordinator(CircularValueAndCoordinate actualCircularListOfCoordinates,
                                                             CircularValueAndCoordinate beforeCircularValueAndCoordinate) {

        RouteCoordinatesCmdBuilder routeCoordinatesCmdBuilder = RouteCoordinatesCmd.builder()
                .beforeCmd(ValueAndCoordinateCmd.toCmd(beforeCircularValueAndCoordinate.getSame()))
                .actualCmd(ValueAndCoordinateCmd.toCmd(actualCircularListOfCoordinates.getSame()));

        if (Objects.nonNull(actualCircularListOfCoordinates.getNext())) {
            buildRouteCoordinator(actualCircularListOfCoordinates.getNext(), actualCircularListOfCoordinates);
        }

        routeCoordinatesCmdBuilder.nextCmd(null);

        return routeCoordinatesCmdBuilder.build();
    }
}
