package s4n.codechallenge.entities;

import lombok.*;
import s4n.codechallenge.dtos.CartesianCoordinateDto;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class CartesianCoordinate {
    private byte xAxe;
    private byte yAxe;

    public CartesianCoordinate() {
        xAxe = 0;
        yAxe = 0;
    }

    public static CartesianCoordinate toModel(CartesianCoordinateDto cartesianCoordinateDto) {
        return CartesianCoordinate.builder()
                .xAxe(cartesianCoordinateDto.getXAxe())
                .yAxe(cartesianCoordinateDto.getYAxe())
                .build();
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
