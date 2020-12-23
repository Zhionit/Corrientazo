package s4n.codechallenge.actorsdtos.dtos;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class DeliveryOrderDto {
    private int id;
    private DeliveryOrderStatus deliveryOrderStatus;
    private CardinalPointDto cardinalPointDtoOfDestinationDto;

    public DeliveryOrderDto(CardinalPointDto cardinalPointDtoOfDestinationDto) {
        this.id = 0;
        this.deliveryOrderStatus = DeliveryOrderStatus.UNDELIVERED;
        this.cardinalPointDtoOfDestinationDto = cardinalPointDtoOfDestinationDto;
    }

    public static DeliveryOrderDto toDto(DeliveryOrder deliveryOrder) {
        return DeliveryOrderDto.builder()
                .id(deliveryOrder.getId())
                .deliveryOrderStatus(deliveryOrder.getDeliveryOrderStatus())
                .cardinalPointDtoOfDestinationDto(CardinalPointDto.toDto(deliveryOrder.getCardinalPoint()))
                .build();
    }

    public static List<DeliveryOrderDto> toDtos(List<DeliveryOrder> deliveryOrders) {
        return deliveryOrders.stream()
                .map(DeliveryOrderDto::toDto)
                .collect(Collectors.toList());
    }

}
