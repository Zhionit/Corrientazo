package s4n.codechallenge.actorsdtos.dtos;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.DeliveryOrder;
import s4n.codechallenge.enums.DeliveryOrderStatus;

import java.util.List;
import java.util.stream.Collectors;

@Generated
@Setter
@Getter
@Builder
public class DeliveryOrderDto {
    private byte id;
    private DeliveryOrderStatus deliveryOrderStatus;
    private CartesianCoordinateDto cartesianCoordinateDtoOfDestinationDto;

    public DeliveryOrderDto(CartesianCoordinateDto cartesianCoordinateDtoOfDestinationDto) {
        this.deliveryOrderStatus = DeliveryOrderStatus.UNDELIVERED;
        this.cartesianCoordinateDtoOfDestinationDto = cartesianCoordinateDtoOfDestinationDto;
    }

    public static DeliveryOrderDto toDto(DeliveryOrder deliveryOrder) {
        return DeliveryOrderDto.builder()
                .id(deliveryOrder.getId())
                .deliveryOrderStatus(deliveryOrder.getDeliveryOrderStatus())
                .cartesianCoordinateDtoOfDestinationDto(CartesianCoordinateDto.toDto(deliveryOrder.getCartesianCoordinateOfDestination()))
                .build();
    }

    public static List<DeliveryOrderDto> toDtos(List<DeliveryOrder> deliveryOrders) {
        return deliveryOrders.stream()
                .map(DeliveryOrderDto::toDto)
                .collect(Collectors.toList());
    }

}
