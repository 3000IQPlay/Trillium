package dev._3000IQPlay.trillium.manager;

import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.modules.Feature;
import dev._3000IQPlay.trillium.util.PlayerUtils;
import dev._3000IQPlay.trillium.util.TrilliumUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

public class FriendManager extends Feature {
    public List<Friend> friends = new ArrayList<Friend>();

    public FriendManager() {
        super("Friends");
    }

    public boolean isFriend(String name) {
        this.cleanFriends();
        return this.friends.stream().anyMatch(friend -> friend.username.equalsIgnoreCase(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return this.isFriend(player.getName());
    }

    public void addFriend(String name) {
        Friend friend = this.getFriendByName(name,"");
        if (friend != null) {
            this.friends.add(friend);
        }
        this.cleanFriends();
        try {
            TrilliumUtils.saveUserAvatar("https://minotar.net/helm/" + name + "/100.png",name);

        } catch (Exception e){

        }
    }

    public void removeFriend(String name) {
        this.cleanFriends();
        for (Friend friend : this.friends) {
            if (!friend.getUsername().equalsIgnoreCase(name)) continue;
            this.friends.remove(friend);
            break;
        }
    }

    public void onLoad() {
        this.friends = new ArrayList<Friend>();
        this.clearSettings();
    }


    public void saveFriends() {
        this.clearSettings();
        this.cleanFriends();
        for (Friend friend : this.friends) {
            this.register(new Setting<String>(friend.getUuid().toString(), friend.getUsername()));
        }
    }

    public void cleanFriends() {
        this.friends.stream().filter(Objects::nonNull).filter(friend -> friend.getUsername() != null);
    }

    public List<Friend> getFriends() {
        this.cleanFriends();
        return this.friends;
    }


    public Friend getFriendByName(String input, String description) {
        UUID uuid = PlayerUtils.getUUIDFromName(input);
        Friend friend;
        if (uuid != null) {
            if(Objects.equals(description, "")){
                friend = new Friend(input, uuid, "");
            } else {
                friend = new Friend(input, uuid, description);
            }
            return friend;
        }
        return null;
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
    }
	
	public boolean isEnemy(EntityPlayer player) {
        return false;
    }

    public static class Friend {
        private final String username;
        private final UUID uuid;
        private final String description;

        public Friend(String username, UUID uuid, String description) {
            this.username = username;
            this.uuid = uuid;
            this.description = description;
        }

        public String getUsername() {
            return this.username;
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }
}

