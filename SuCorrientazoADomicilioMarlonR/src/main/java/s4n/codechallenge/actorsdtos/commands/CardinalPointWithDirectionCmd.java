package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPointWithDirection;
import s4n.codechallenge.enums.CartesianDirection;

import java.util.Set;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CardinalPointWithDirectionCmd {
    private CartesianDirection cartesianDirection;
    private CardinalPointCmd cardinalPoint;

    public static CardinalPointWithDirectionCmd toCmd(CardinalPointWithDirection cardinalPointWithDirection) {
        return CardinalPointWithDirectionCmd.builder()
                .cardinalPoint(CardinalPointCmd.toCmd(cardinalPointWithDirection.getCardinalPoint()))
                .cartesianDirection(cardinalPointWithDirection.getCartesianDirection())
                .build();
    }

    public static Set<CardinalPointWithDirectionCmd> toCmds(Set<CardinalPointWithDirection> cardinalPointWithDirections) {
        return cardinalPointWithDirections.stream()
                .map(CardinalPointWithDirectionCmd::toCmd)
                .collect(Collectors.toSet());
    }
}
