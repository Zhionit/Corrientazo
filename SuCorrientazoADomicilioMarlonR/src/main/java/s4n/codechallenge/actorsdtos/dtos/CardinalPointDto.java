package s4n.codechallenge.actorsdtos.dtos;

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
public class CardinalPointDto {
    private int xAxe;
    private int yAxe;

    public CardinalPointDto() {
        xAxe = 0;
        yAxe = 0;
    }

    public static CardinalPointDto toDto(CardinalPoint cardinalPoint) {
        return CardinalPointDto.builder()
                .xAxe(cardinalPoint.getXAxe())
                .yAxe(cardinalPoint.getYAxe())
                .build();
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
