package pa.centric.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.play.client.CPlayerPacket;
import pa.centric.events.Event;


@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventTeleport extends Event {

    private CPlayerPacket response;

    public double posX, posY, posZ;
    public float yaw, pitch;

}
