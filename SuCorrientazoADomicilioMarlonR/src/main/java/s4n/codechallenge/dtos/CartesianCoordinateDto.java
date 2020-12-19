package s4n.codechallenge.dtos;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
@Setter
@Getter
@AllArgsConstructor
public class CartesianCoordinateDto {
    private byte xAxe;
    private byte yAxe;

    public CartesianCoordinateDto() {
        xAxe = 0;
        yAxe = 0;
    }

    @Override
    public String toString() {
        return "(" + xAxe + ", " + yAxe +')';
    }
}
