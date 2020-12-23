package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPoint;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class CardinalPointCmd {
    private int xAxe;
    private int yAxe;

    public CardinalPointCmd() {
        xAxe = 0;
        yAxe = 0;
    }

    public static CardinalPointCmd toCmd(CardinalPoint cardinalPoint) {
        return CardinalPointCmd.builder()
                .xAxe(cardinalPoint.getXAxe())
                .yAxe(cardinalPoint.getYAxe())
                .build();
    }

    public static Set<CardinalPointCmd> toCmds(Set<CardinalPoint> cardinalPoints) {
        return cardinalPoints.stream()
                .map(CardinalPointCmd::toCmd)
                .collect((Collectors.toCollection(LinkedHashSet::new)));
    }

    public static CardinalPoint toModel(CardinalPointCmd cardinalPointCmd) {
        return CardinalPoint.builder()
                .xAxe(cardinalPointCmd.getXAxe())
                .yAxe(cardinalPointCmd.getYAxe())
                .build();
    }

    public static Set<CardinalPoint> toModels(Set<CardinalPointCmd> cardinalPointCmds) {
        return cardinalPointCmds.stream()
                .map(CardinalPointCmd::toModel)
                .collect((Collectors.toCollection(LinkedHashSet::new)));
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
