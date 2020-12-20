package s4n.codechallenge.actorsdtos.commands;

import s4n.codechallenge.actorsdtos.dtos.OrderDto;
import s4n.codechallenge.entities.CartesianCoordinate;
import s4n.codechallenge.entities.Order;

import java.util.List;
import java.util.stream.Collectors;

public class OrderCmd {

    public static Order toModel(OrderDto orderDto) {
        return Order.builder()
                .id(orderDto.getId())
                .cartesianCoordinateOfDestination(CartesianCoordinate.toModel(orderDto.getCartesianCoordinateDtoOfDestinationDto()))
                .orderStatus(orderDto.getOrderStatus())
                .build();
    }

    public static List<Order> toModels(List<OrderDto> ordersDtos) {
        return ordersDtos.stream()
                .map(OrderCmd::toModel)
                .collect(Collectors.toList());
    }

}
