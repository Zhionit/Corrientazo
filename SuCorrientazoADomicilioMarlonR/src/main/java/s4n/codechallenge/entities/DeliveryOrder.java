package s4n.codechallenge.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import s4n.codechallenge.enums.DeliveryOrderStatus;

@Generated
@Setter
@Getter
@Builder
@AllArgsConstructor
public class DeliveryOrder {
    private int id;
    private CardinalPoint cardinalPoint;
    private DeliveryOrderStatus deliveryOrderStatus;

    public DeliveryOrder(CardinalPoint cardinalPoint) {
        this.deliveryOrderStatus = DeliveryOrderStatus.UNDELIVERED;
        this.cardinalPoint = cardinalPoint;
    }
}
