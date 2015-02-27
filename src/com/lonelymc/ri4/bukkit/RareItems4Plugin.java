package com.lonelymc.ri4.bukkit;

import com.lonelymc.ri4.api.IRareItems4API;
import com.lonelymc.ri4.bukkit.commands.*;
import com.lonelymc.ri4.bukkit.listeners.PlayerListener;
import com.lonelymc.ri4.bukkit.rareitems.RareItemsManager;
import com.lonelymc.ri4.bukkit.rareitems.RareItemsYMLPersistence;
import com.lonelymc.ri4.bukkit.rareitems.properties.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class RareItems4Plugin extends JavaPlugin{
    private BasicCommandExecutor commandExecutor;
    private IRareItems4API api;

    @Override
    public void onEnable(){
        this.getDataFolder().mkdirs();

        RareItemsYMLPersistence persistence = new RareItemsYMLPersistence(this,this.getDataFolder());

        File riCustomizationsFile = new File(this.getDataFolder(),"rareItemCustomizations.yml");

        if(!riCustomizationsFile.exists()){
            this.copy(this.getResource("rareItemCustomizations.yml"),riCustomizationsFile);
        }

        this.api = new RareItemsManager(this,persistence);

        File stringsFile = new File(this.getDataFolder(),"strings.yml");

        if(!stringsFile.exists()){
            this.copy(this.getResource("strings.yml"),stringsFile);
        }

        this.api.loadStrings(stringsFile);

// Register properties
        this.api.addItemProperty(new Backstab());
        this.api.addItemProperty(new Blinding());
        this.api.addItemProperty(new Burst());
        this.api.addItemProperty(new BurstShield());
        this.api.addItemProperty(new CallLightning());
        this.api.addItemProperty(new CatsFeet());
        this.api.addItemProperty(new Confuse());
        this.api.addItemProperty(new CraftItem());
        this.api.addItemProperty(new Disarm());
        this.api.addItemProperty(new Durability());
        this.api.addItemProperty(new Fertilize());
        this.api.addItemProperty(new GreaterBurst());
        this.api.addItemProperty(new GrowTree());
        this.api.addItemProperty(new HalfBakedIdea());
        this.api.addItemProperty(new Hardy());
        this.api.addItemProperty(new Haste());
        this.api.addItemProperty(new Invisibility());
        this.api.addItemProperty(new MagicBag());
        this.api.addItemProperty(new MeltObsidian());
        this.api.addItemProperty(new MermaidsGift());
        this.api.addItemProperty(new Multishot());
        this.api.addItemProperty(new PaintWool());
        this.api.addItemProperty(new Poison());
        this.api.addItemProperty(new Pull());
        this.api.addItemProperty(new Rage());
        this.api.addItemProperty(new Regeneration());
        this.api.addItemProperty(new RepairItem());
        this.api.addItemProperty(new Replenish());
        this.api.addItemProperty(new Slow());
        this.api.addItemProperty(new Smelt());
        this.api.addItemProperty(new Spore());
        this.api.addItemProperty(new SummonBoat());
        this.api.addItemProperty(new Strength());
        this.api.addItemProperty(new VampiricRegeneration());
        this.api.addItemProperty(new WaterBreathing());
        this.api.addItemProperty(new Weaken());

        this.api.addItemProperty(new SummonBat());
        this.api.addItemProperty(new SummonChicken());
        this.api.addItemProperty(new SummonMooshroom());
        this.api.addItemProperty(new SummonOcelot());
        this.api.addItemProperty(new SummonPig());
        this.api.addItemProperty(new SummonSheep());
        this.api.addItemProperty(new SummonSlime());

        this.api.addItemProperty(new BlackSmokeFX());
        this.api.addItemProperty(new FireworksFX());
        this.api.addItemProperty(new FlameFX());
        this.api.addItemProperty(new MageFX());
        this.api.addItemProperty(new NerdyFX());
        this.api.addItemProperty(new PortalFX());
        this.api.addItemProperty(new RainbowFuryFX());
        this.api.addItemProperty(new ToughLove());
        this.api.addItemProperty(new WitchFX());

// Register "/hat" to act as an equip/unequip listener
        this.getCommand("hat").setExecutor(new HatCommandExecutor(this.api));

        this.commandExecutor = new BasicCommandExecutor(this);

        this.getCommand("ri").setExecutor(commandExecutor);

// Register subcommands
        this.registerSubCommand(new CommandEssence(this.api));
        this.registerSubCommand(new CommandCraft(this.api));

// Register listeners
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this,this.api),this);
    }

    @Override
    public void onDisable(){
        this.api.save();
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerSubCommand(BasicCommand command){
        this.commandExecutor.registerSubCommand(command);
    }

    public IRareItems4API getAPI() {
        return api;
    }
}
