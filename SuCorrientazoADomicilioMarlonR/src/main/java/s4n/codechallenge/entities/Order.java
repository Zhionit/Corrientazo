package s4n.codechallenge.entities;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.dtos.OrderDto;
import s4n.codechallenge.enums.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

@Generated
@Setter
@Getter
@Builder
public class Order {
    private OrderStatus orderStatus;
    private CartesianCoordinate cartesianCoordinateOfDestination;

    public Order(CartesianCoordinate cartesianCoordinateOfDestination) {
        this.orderStatus = OrderStatus.UNDELIVERED;
        this.cartesianCoordinateOfDestination = cartesianCoordinateOfDestination;
    }

    public static Order toModel(OrderDto orderDto) {
        return Order.builder()
                .cartesianCoordinateOfDestination(CartesianCoordinate.toModel(orderDto.getCartesianCoordinateDtoOfDestination()))
                .orderStatus(orderDto.getOrderStatus())
                .build();
    }

    public static List<Order> toModels(List<OrderDto> ordersDtos) {
        return ordersDtos.stream()
                .map(Order::toModel)
                .collect(Collectors.toList());
    }
}
