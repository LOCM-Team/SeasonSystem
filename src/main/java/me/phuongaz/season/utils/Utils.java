package me.phuongaz.season.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import me.phuongaz.season.Loader;
import me.phuongaz.season.api.SeasonAPI;
import me.phuongaz.season.season.Season;

public class Utils{

    public static List<String> getCurrentShop(){
        return Loader.getInstance().getConfig().getStringList("season.shop");
    }

    public static String getDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String date = sdf.format(new Date());
        return date;
    }

    public static int getItemCount(Player player, Item item){
        int count = 0;
        for(Item it : player.getInventory().getContents().values()){
            if(it.equalsExact(item)){
                count += it.getCount();
            }
        }
        return count;
    }

    public static void reloadShop(){
        Config config = Loader.getInstance().getConfig();
        Random rd = new Random();
        int price = rd.nextInt(config.getInt("oscillation"));
        Season season = SeasonAPI.getNowSeason();
        List<String> setupshop = config.getStringList("shop." + season.getSeason().toLowerCase());
        List<String> seasonshop = new ArrayList<>();
        System.out.println(setupshop);
        System.out.println(season.getSeason());
        for(String s : setupshop){
            String[] list = s.split(":");
            int price1 = 0;
            if(season.isFirstly()){
                price1 = rd.nextInt(Integer.parseInt(list[2]) + price);
            }
            if(season.isBetween()){
                price1 = rd.nextInt(Integer.parseInt(list[3]) + price);
            }
            if(season.isLast()){
                price1 = rd.nextInt(Integer.parseInt(list[4]) + price);
            }
            String item = list[0];
            item += ":" + list[1];
            item += ":" + price1;
            seasonshop.add(item);
            System.out.println(item);
        }
        System.out.println(seasonshop);
        config.set("season.shop", seasonshop);
        config.save();
        config.reload();
        Loader.getInstance().reloadConfig();
    }

    public static void runSeason(){
        new NukkitRunnable(){
            @Override
            public void run(){
                String day = getDay();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd");
                LocalDateTime now = LocalDateTime.now();
                String date = dtf.format(now);
                if(day.equals("Monday") || day.equals("Wednesday") || day.equals("Saturday")){
                    if(day.equals("Monday") && !date.equals(Loader.getInstance().getConfig().getString("season.date"))){
                        String season = SeasonAPI.getNowSeason().getNextSeason().getName();
                        Loader.getInstance().getConfig().set("season.name", season.toLowerCase());
                        Loader.getInstance().getConfig().set("season.date", date);
                        Loader.getInstance().getConfig().save();
                        Loader.getInstance().reloadConfig();
                        Loader.getInstance().loadSeason();
                        String msg2 = "&l&eNEW &f???? chuy???n sang m??a &6" + season;
                        Server.getInstance().broadcastMessage(TextFormat.colorize(msg2));
                    }                    
                    reloadShop();
                    String msg = "&l&eNEW &fC???a h??ng m??a v??? ???? ???????c l??m m???i!";
                    Server.getInstance().broadcastMessage(TextFormat.colorize(msg));
                }
            }
        }.runTaskTimer(Loader.getInstance(), 0, 20 * 60 * 60 * 3);
    }
}