package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.RouteCoordinates;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
public class RouteCoordinatesCmd {
    private final CartesianCoordinateCmd beforeCmd;
    private final CartesianCoordinateCmd actualCmd;
    private final CartesianCoordinateCmd nextCmd;

    public static RouteCoordinates toModel(RouteCoordinatesCmd routeCoordinatesCmd) {
        return RouteCoordinates.builder()
                .beforeOptional(Optional.ofNullable(CartesianCoordinateCmd.toModel(routeCoordinatesCmd.getBeforeCmd())))
                .actualOptional(Optional.ofNullable(CartesianCoordinateCmd.toModel(routeCoordinatesCmd.getActualCmd())))
                .nextOptional(Optional.ofNullable(CartesianCoordinateCmd.toModel(routeCoordinatesCmd.getNextCmd())))
                .build();
    }

    public static List<RouteCoordinates> toModels(List<RouteCoordinatesCmd> routeCoordinatesCmds) {
        return routeCoordinatesCmds.stream()
                .map(RouteCoordinatesCmd::toModel)
                .collect(Collectors.toList());
    }
}
