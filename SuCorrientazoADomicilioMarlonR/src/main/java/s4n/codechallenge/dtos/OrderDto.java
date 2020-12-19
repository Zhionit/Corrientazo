package s4n.codechallenge.dtos;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.OrderStatus;

@Generated
@Setter
@Getter
public class OrderDto {
    private OrderStatus orderStatus;
    private CartesianCoordinateDto cartesianCoordinateDtoOfDestination;

    public OrderDto(CartesianCoordinateDto cartesianCoordinateDtoOfDestination) {
        this.orderStatus = OrderStatus.UNDELIVERED;
        this.cartesianCoordinateDtoOfDestination = cartesianCoordinateDtoOfDestination;
    }
}
