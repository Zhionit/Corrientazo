package s4n.codechallenge.entities;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.commands.CardinalPointWithDirectionCmd;

import java.util.Optional;

@Generated
@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class RouteCoordinates {
    private final Optional<CardinalPointWithDirectionCmd> beforeOptional;
    private final Optional<CardinalPointWithDirectionCmd> actualOptional;
    private final Optional<CardinalPointWithDirectionCmd> nextOptional;
}
