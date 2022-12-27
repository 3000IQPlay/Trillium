package dev._3000IQPlay.trillium.util;

import com.google.common.collect.Maps;
import dev._3000IQPlay.trillium.command.Command;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static dev._3000IQPlay.trillium.util.Util.mc;

public class TrilliumUtils {

    private static final Map<String, String> uuidNameCache = Maps.newConcurrentMap();

    public static String uuidtoname(String uuid) {
        uuid = uuid.replace("-", "");
        if (uuidNameCache.containsKey(uuid)) {
            return uuidNameCache.get(uuid);
        }

        final String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        try {
            final String nameJson = IOUtils.toString(new URL(url));
            if (nameJson != null && nameJson.length() > 0) {
                final JSONArray jsonArray = (JSONArray) JSONValue.parseWithException(nameJson);
                if (jsonArray != null) {
                    final JSONObject latestName = (JSONObject) jsonArray.get(jsonArray.size() - 1);
                    if (latestName != null) {
                        uuidNameCache.put(uuid,latestName.get("name").toString());
                        return latestName.get("name").toString();
                    }
                }
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void saveUserAvatar(String s, String nickname){
        try {
            URL url = new URL(s);
            URLConnection openConnection = url.openConnection();
            boolean check = true;

            try {

                openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                openConnection.connect();

                if (openConnection.getContentLength() > 8000000) {
                    System.out.println(" file size is too big.");
                    check = false;
                }
            } catch (Exception e) {
                System.out.println("Couldn't create a connection to the link, please recheck the link.");
                check = false;
                e.printStackTrace();
            }
            if (check) {
                BufferedImage img = null;
                try {
                    InputStream in = new BufferedInputStream(openConnection.getInputStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = 0;
                    while (-1 != (n = in.read(buf))) {
                        out.write(buf, 0, n);
                    }
                    out.close();
                    in.close();
                    byte[] response = out.toByteArray();
                    img = ImageIO.read(new ByteArrayInputStream(response));
                } catch (Exception e) {
                    System.out.println(" couldn't read an image from this link.");
                    e.printStackTrace();
                }
                try {
                    ImageIO.write(img, "png", new File("Trillium/friendsAvatars/" + nickname + ".png"));
                } catch (IOException e) {
                    System.out.println("Couldn't create/send the output image.");
                    e.printStackTrace();
                }
            }
        } catch (Exception e){

        }
    }



    public static String solvename(String notsolved){
        AtomicReference<String> mb = new AtomicReference<>("err");
        Objects.requireNonNull(mc.getConnection()).getPlayerInfoMap().forEach(player -> {
            if(notsolved.contains(player.getGameProfile().getName())){
                mb.set(player.getGameProfile().getName());
            }
        });
        return mb.get();
    }

    public static void savePlayerSkin(String s, String nickname){
        try {
            URL url = new URL(s);
            URLConnection openConnection = url.openConnection();
            boolean check = true;

            try {

                openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                openConnection.connect();

                if (openConnection.getContentLength() > 8000000) {
                    System.out.println(" file size is too big.");
                    check = false;
                }
            } catch (Exception e) {
                System.out.println("Couldn't create a connection to the link, please recheck the link.");
                check = false;
                e.printStackTrace();
            }
            if (check) {
                BufferedImage img = null;
                try {
                    InputStream in = new BufferedInputStream(openConnection.getInputStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int n = 0;
                    while (-1 != (n = in.read(buf))) {
                        out.write(buf, 0, n);
                    }
                    out.close();
                    in.close();
                    byte[] response = out.toByteArray();
                    img = ImageIO.read(new ByteArrayInputStream(response));
                } catch (Exception e) {
                    System.out.println(" couldn't read an image from this link.");
                    e.printStackTrace();
                }
                try {
                    ImageIO.write(img, "png", new File("Trillium/skins/" + nickname + ".png"));
                } catch (IOException e) {
                    System.out.println("Couldn't create/send the output image.");
                    e.printStackTrace();
                }
            }
        } catch (Exception e){

        }
    }
}
