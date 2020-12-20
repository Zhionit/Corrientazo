package s4n.codechallenge.actorsdtos.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.DeliveryOrder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
public class NewDeliveryOrderCmd {

    private byte id;
    private CartesianCoordinateCmd cartesianCoordinateOfDestinationCmd;

    public static Optional<NewDeliveryOrderCmd> toCmd(Optional<DeliveryOrder> deliveryOrderOptional) {
        if (deliveryOrderOptional.isPresent()) {
            return Optional.of(NewDeliveryOrderCmd.builder()
                .id(deliveryOrderOptional.get().getId())
                .cartesianCoordinateOfDestinationCmd(CartesianCoordinateCmd.toCmd(deliveryOrderOptional.get().getCartesianCoordinateOfDestination()))
                .build());
        }
        return Optional.empty();
    }

    public static List<Optional<NewDeliveryOrderCmd>> toCmds(List<Optional<DeliveryOrder>> deliveryOrders) {
        return deliveryOrders.stream()
                .map(NewDeliveryOrderCmd::toCmd)
                .collect(Collectors.toList());
    }
}
