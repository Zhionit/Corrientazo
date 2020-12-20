package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.DeliveryOrder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
public class DeliveryOrderCmd {
    private byte id;
    private CartesianCoordinateCmd cartesianCoordinateOfDestinationCmd;
    private DeliveryOrderCmd nextDeliveryOrderCmd;

    public static DeliveryOrder toModel(DeliveryOrderCmd deliveryOrderCmd) {
        return DeliveryOrder.builder()
                .id(deliveryOrderCmd.getId())
                .cartesianCoordinateOfDestination(CartesianCoordinateCmd.toModel(deliveryOrderCmd.cartesianCoordinateOfDestinationCmd))
                .nextDeliveryOrder(Objects.isNull(deliveryOrderCmd.getNextDeliveryOrderCmd()) ? null : DeliveryOrderCmd.toModel(deliveryOrderCmd.getNextDeliveryOrderCmd()))
                .build();
    }

    public static List<DeliveryOrder> toModels(List<DeliveryOrderCmd> deliveryOrderCmds) {
        return deliveryOrderCmds.stream()
                .map(DeliveryOrderCmd::toModel)
                .collect(Collectors.toList());
    }
}
