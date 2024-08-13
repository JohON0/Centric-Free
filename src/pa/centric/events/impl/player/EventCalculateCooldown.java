package pa.centric.events.impl.player;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.item.Item;
import pa.centric.events.Event;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventCalculateCooldown extends Event {

    public Item itemStack;
    public float cooldown;

    public EventCalculateCooldown(Item item) {
        this.itemStack = item;
    }
}

