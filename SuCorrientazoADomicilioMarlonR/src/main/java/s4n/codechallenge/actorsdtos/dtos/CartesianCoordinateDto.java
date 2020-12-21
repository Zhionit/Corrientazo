package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CartesianCoordinate;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class CartesianCoordinateDto {
    private int xAxe;
    private int yAxe;

    public CartesianCoordinateDto() {
        xAxe = 0;
        yAxe = 0;
    }

    public static CartesianCoordinateDto toDto(CartesianCoordinate cartesianCoordinate) {
        return CartesianCoordinateDto.builder()
                .xAxe(cartesianCoordinate.getXAxe())
                .yAxe(cartesianCoordinate.getYAxe())
                .build();
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
