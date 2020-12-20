package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import s4n.codechallenge.entities.DeliveryOrder;

import java.util.List;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
@NoArgsConstructor
public class NewActualDeliveryOrderCmd {

    private byte id;
    private CartesianCoordinateCmd cartesianCoordinateOfDestinationCmd;

    public static NewActualDeliveryOrderCmd toCmd(DeliveryOrder deliveryOrder) {
        return NewActualDeliveryOrderCmd.builder()
                .id(deliveryOrder.getId())
                .cartesianCoordinateOfDestinationCmd(CartesianCoordinateCmd.toCmd(deliveryOrder.getCartesianCoordinateOfDestination()))
                .build();
    }

    public static List<NewActualDeliveryOrderCmd> toCmds(List<DeliveryOrder> deliveryOrders) {
        return deliveryOrders.stream()
                .map(NewActualDeliveryOrderCmd::toCmd)
                .collect(Collectors.toList());
    }

}
