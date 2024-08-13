package pa.centric.client.modules.impl.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TextFormatting;
import pa.centric.client.helper.conduction;
import pa.centric.client.modules.Function;
import pa.centric.client.modules.ModuleAnnotation;
import pa.centric.client.modules.Type;
import pa.centric.events.Event;
import pa.centric.events.impl.game.EventMouseTick;
import pa.centric.util.ClientUtils;


@ModuleAnnotation(name = "MiddleClickFriend", category = Type.Util)
public class MiddleClickFriendFunction extends Function {

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMouseTick e) {
            handleMouseTickEvent(e);
        }
    }

    /**
     * Обрабатывает событие нажатия кнопки мыши.
     *
     * @param event событие нажатия кнопки мыши
     */
    private void handleMouseTickEvent(EventMouseTick event) {
        if (event.getButton() == 2 && mc.pointedEntity instanceof LivingEntity) {
            String entityName = mc.pointedEntity.getName().getString();

            if (conduction.FRIEND_MANAGER.isFriend(entityName)) {
                conduction.FRIEND_MANAGER.removeFriend(entityName);
                displayRemoveFriendMessage(entityName);
            } else {
                conduction.FRIEND_MANAGER.addFriend(entityName);
                displayAddFriendMessage(entityName);
            }
        }
    }

    /**
     * Отображает сообщение о удалении друга.
     *
     * @param friendName имя друга
     */
    private void displayRemoveFriendMessage(String friendName) {
        ClientUtils.sendMessage(TextFormatting.RED + "Удалил " + TextFormatting.RESET + friendName + " из друзей!");
    }

    /**
     * Отображает сообщение о добавлении друга.
     *
     * @param friendName имя друга
     */
    private void displayAddFriendMessage(String friendName) {
        ClientUtils.sendMessage(TextFormatting.GREEN + "Добавил " + TextFormatting.RESET + friendName + " в друзья!");
    }
}
