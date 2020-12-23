package s4n.codechallenge.enums;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Generated
@Getter
@AllArgsConstructor
public enum CartesianMapperToCardinal {

    Y(CardinalDirection.NORTE), X(CardinalDirection.ORIENTE), NY(CardinalDirection.SUR), NX(CardinalDirection.OCCIDENTE);

    private CardinalDirection value;

    @Override
    public String toString() {
        return value.toString();
    }
}
