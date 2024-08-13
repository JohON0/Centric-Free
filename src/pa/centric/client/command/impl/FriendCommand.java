package pa.centric.client.command.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import pa.centric.client.helper.conduction;
import pa.centric.client.command.Command;
import pa.centric.client.command.CommandInfo;
import pa.centric.client.friend.Friend;
import pa.centric.client.friend.FriendManager;


@CommandInfo(name = "friend", description = "��������� �������� ������� � ������ ������")
public class FriendCommand extends Command {

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            switch (args[1]) {
                case "add" -> {
                    final String friendName = args[2];
                    addFriend(friendName);
                }
                case "remove" -> {
                    final String friendName = args[2];
                    removeFriend(friendName);
                }
                case "list" -> friendList();
                case "clear" -> clearFriendList();
            }
        } else {
            error();
        }
    }

    /**
     * ������� ���������� ������ � ������
     *
     * @param friendName ��� ������
     */
    private void addFriend(final String friendName) {
        final FriendManager friendManager = conduction.FRIEND_MANAGER;

        if (friendName.contains(Minecraft.getInstance().player.getName().getString())) {
            sendMessage("� ��������� �� �� ������ �������� ������ ���� � ������ :(");
            return;
        }
        if (friendManager.getFriends().stream().map(Friend::getName).toList().contains(friendName)) {
            sendMessage(friendName + " ��� ���� � ������ ������");
            return;
        }

        sendMessage(friendName + " ������� �������� � ������ ������!");
        friendManager.addFriend(friendName);
    }

    /**
     * ������� �������� ������ �� ������ ������
     *
     * @param friendName ��� ������
     */
    private void removeFriend(final String friendName) {
        final FriendManager friendManager = conduction.FRIEND_MANAGER;

        if (friendManager.isFriend(friendName)) {
            friendManager.removeFriend(friendName);
            sendMessage(friendName + " ��� ������ �� ������ ������");
            return;
        }
        sendMessage(friendName + " ���� � ������ ������");
    }

    /**
     * ������� ������ ����� ���� ������
     */
    private void friendList() {
        final FriendManager friendManager = conduction.FRIEND_MANAGER;

        if (friendManager.getFriends().isEmpty()) {
            sendMessage("������ ������ ����");
            return;
        }

        sendMessage("������ ������:");
        for (Friend friend : friendManager.getFriends()) {
            sendMessage(TextFormatting.GRAY + friend.getName());
        }
    }

    /**
     * ������� ������ ������
     */
    private void clearFriendList() {
        final FriendManager friendManager = conduction.FRIEND_MANAGER;

        if (friendManager.getFriends().isEmpty()) {
            sendMessage("������ ������ ����");
            return;
        }

        friendManager.clearFriend();
        sendMessage("������ ������ ������� ������");
    }

    @Override
    public void error() {
        sendMessage(TextFormatting.GRAY + "������ � �������������" + TextFormatting.WHITE + ":");
        sendMessage(TextFormatting.WHITE + "." + "friend add " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "name" + TextFormatting.GRAY + ">");
        sendMessage(TextFormatting.WHITE + "." + "friend remove " + TextFormatting.GRAY + "<"
                + TextFormatting.RED + "name" + TextFormatting.GRAY + ">");
        sendMessage(TextFormatting.WHITE + "." + "friend list" + TextFormatting.GRAY + " - ���������� ������ ���� ��������");
        sendMessage(TextFormatting.WHITE + "." + "friend clear" + TextFormatting.GRAY + " - ������� ���� ��������");
    }
}
