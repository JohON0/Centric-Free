package pa.centric.events.impl.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pa.centric.events.Event;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventMouseTick extends Event {

    private int button;
}
