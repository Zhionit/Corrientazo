package s4n.codechallenge.actorsdtos.commands;

import com.sun.istack.internal.Nullable;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.entities.CardinalPoint;
import s4n.codechallenge.entities.DeliveryOrder;

import java.util.List;
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
                .xAxe(deliveryOrderCmd.cardinalPointWithDirectionCmd.getCardinalPoint().getXAxe())
                .yAxe(deliveryOrderCmd.cardinalPointWithDirectionCmd.getCardinalPoint().getYAxe())
                .build();

        return DeliveryOrder.builder()
                .id(deliveryOrderCmd.getId())
                .cardinalPoint(cardinalPointOfDestination)
                .build();
    }

    public static List<DeliveryOrder> toModels(Set<DeliveryOrderCmd> deliveryOrderCmd) {
        return deliveryOrderCmd.stream()
                .map(DeliveryOrderCmd::toModel)
                .collect(Collectors.toList());
    }
}
