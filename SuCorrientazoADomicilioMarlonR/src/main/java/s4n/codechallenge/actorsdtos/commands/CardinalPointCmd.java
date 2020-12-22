package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPoint;

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

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
