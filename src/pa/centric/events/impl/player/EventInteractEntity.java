package pa.centric.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import pa.centric.events.Event;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventInteractEntity extends Event {
    private Entity entity;

}
