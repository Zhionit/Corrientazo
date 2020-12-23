package s4n.codechallenge.actorsdtos.commands;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPoint;
import s4n.codechallenge.entities.DeliveryOrder;
import s4n.codechallenge.enums.DeliveryOrderStatus;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Generated
@Getter
@Setter
@Builder
public class DeliveryOrderCmd {

    private final int id;
    private final CardinalPointWithDirectionCmd cardinalPointWithDirectionCmd;

    public static DeliveryOrder toModel(DeliveryOrderCmd deliveryOrderCmd) {
        CardinalPoint cardinalPointOfDestination = CardinalPoint.builder()
                .xAxe(deliveryOrderCmd.cardinalPointWithDirectionCmd.getCardinalPointCmd().getXAxe())
                .yAxe(deliveryOrderCmd.cardinalPointWithDirectionCmd.getCardinalPointCmd().getYAxe())
                .build();

        return DeliveryOrder.builder()
                .id(deliveryOrderCmd.getId())
                .cardinalPoint(cardinalPointOfDestination)
                .deliveryOrderStatus(DeliveryOrderStatus.UNDELIVERED)
                .build();
    }

    public static Set<DeliveryOrder> toModels(Set<DeliveryOrderCmd> deliveryOrderCmd) {
        return deliveryOrderCmd.stream()
                .map(DeliveryOrderCmd::toModel)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static DeliveryOrderCmd toCmd(DeliveryOrder actualDeliveryOrder) {

        CardinalPointWithDirectionCmd cardinalPointWithDirectionCmd = CardinalPointWithDirectionCmd.builder()
                .cardinalPointCmd(CardinalPointCmd.toCmd(actualDeliveryOrder.getCardinalPoint()))
                .build();

        return DeliveryOrderCmd.builder()
                .id(actualDeliveryOrder.getId())
                .cardinalPointWithDirectionCmd(cardinalPointWithDirectionCmd)
                .build();
    }
}
