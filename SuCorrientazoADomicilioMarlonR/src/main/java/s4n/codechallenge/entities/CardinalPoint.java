package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.actorsdtos.dtos.CardinalPointDto;

import java.util.Objects;

@Generated
@Setter
@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class CardinalPoint {
    private int xAxe;
    private int yAxe;

    public CardinalPoint() {
        xAxe = 0;
        yAxe = 0;
    }

    public void incrementValues(int x, int y) {
        xAxe += x;
        yAxe += y;
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof CardinalPointDto) {
            return xAxe == ((CardinalPointDto) o).getXAxe() && yAxe == ((CardinalPointDto) o).getYAxe();
        }
        return xAxe == ((CardinalPoint) o).getXAxe() && yAxe == ((CardinalPoint) o).getYAxe();
    }

    @Override
    public int hashCode() {
        return Objects.hash(xAxe, yAxe);
    }
}
