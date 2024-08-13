package pa.centric.client.modules.impl.player;

import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.player.EventUpdate;


@ModuleAnnotation(name = "FastBreak", category = Type.Player)
public class FastBreakFunction extends Function {

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            // —брасываем задержку удара блока дл€ игрока
            mc.playerController.blockHitDelay = 0;

            // ѕровер€ем, превышает ли текущий урон блока значение 1.0F
            if (mc.playerController.curBlockDamageMP > 1.0F) {
                // ≈сли превышает, устанавливаем значение урона блока равным 1.0F
                mc.playerController.curBlockDamageMP = 1.0F;
            }
        }
    }
}
