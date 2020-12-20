package s4n.codechallenge.actorsdtos.dtos;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.Order;
import s4n.codechallenge.enums.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

@Generated
@Setter
@Getter
@Builder
public class OrderDto {
    private byte id;
    private OrderStatus orderStatus;
    private CartesianCoordinateDto cartesianCoordinateDtoOfDestinationDto;

    public OrderDto(CartesianCoordinateDto cartesianCoordinateDtoOfDestinationDto) {
        this.orderStatus = OrderStatus.UNDELIVERED;
        this.cartesianCoordinateDtoOfDestinationDto = cartesianCoordinateDtoOfDestinationDto;
    }

    public static OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .cartesianCoordinateDtoOfDestinationDto(CartesianCoordinateDto.toDto(order.getCartesianCoordinateOfDestination()))
                .build();
    }

    public static List<OrderDto> toDtos(List<Order> orders) {
        return orders.stream()
                .map(OrderDto::toDto)
                .collect(Collectors.toList());
    }

}
