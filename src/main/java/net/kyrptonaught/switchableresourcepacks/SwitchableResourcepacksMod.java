package net.kyrptonaught.switchableresourcepacks;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


public class SwitchableResourcepacksMod implements ModInitializer {
    public static final String MOD_ID = "switchableresourcepacks";
    public static ConfigManager.SingleConfigManager configManager = new ConfigManager.SingleConfigManager(MOD_ID, new ResourcePackConfig());

    public static HashMap<String, ResourcePackConfig.RPOption> rpOptionHashMap = new HashMap<>();
    public static CustomCriterion STARTED, FINISHED, FAILED;

    @Override
    public void onInitialize() {
        ArgumentTypes.register(MOD_ID + ":packs", PackListArgumentType.class, new PackListArgumentType.StringArgumentSerializer());
        CommandRegistrationCallback.EVENT.register(SwitchableResourcepacksMod::register);
        configManager.load();
        getConfig().packs.forEach(rpOption -> {
            rpOptionHashMap.put(rpOption.packname, rpOption);
        });

        if (getConfig().packs.size() == 0) {
            ResourcePackConfig.RPOption option = new ResourcePackConfig.RPOption();
            option.packname = "example_pack";
            option.url = "https://example.com/resourcepack.zip";
            option.hash = "examplehash";
            getConfig().packs.add(option);
            configManager.save();
            System.out.println("[" + MOD_ID + "]: Generated example resourcepack config");
        }
        STARTED = CriterionRegistry.register(new CustomCriterion("started"));
        FINISHED = CriterionRegistry.register(new CustomCriterion("finished"));
        FAILED = CriterionRegistry.register(new CustomCriterion("failed"));
    }

    public static ResourcePackConfig getConfig() {
        return ((ResourcePackConfig) configManager.getConfig());
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean b) {
        dispatcher.register(CommandManager.literal("loadresource")
                .requires((source) -> source.hasPermissionLevel(0))
                .then(CommandManager.argument("packname", PackListArgumentType.word())
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .executes(commandContext -> execute(commandContext, EntityArgumentType.getPlayers(commandContext, "player"))))
                        .executes(commandContext -> execute(commandContext, Collections.singleton(commandContext.getSource().getPlayer())))));
    }

    public static int execute(CommandContext<ServerCommandSource> commandContext, Collection<ServerPlayerEntity> players) {
        String packname = PackListArgumentType.getString(commandContext, "packname");
        ResourcePackConfig.RPOption rpOption = rpOptionHashMap.get(packname);
        if (rpOption == null) {
            commandContext.getSource().sendFeedback(new LiteralText("Packname: ").append(packname).append(" was not found"), false);
            return 1;
        }
        Text feedBack = new LiteralText("Enabled pack: ").append(rpOption.packname);
        players.forEach(player -> {
            if (getConfig().autoRevoke) {
                STARTED.revoke(player);
                FINISHED.revoke(player);
                FAILED.revoke(player);
            }
            player.sendResourcePackUrl(rpOption.url, rpOption.hash, rpOption.required, rpOption.hasPrompt ? new LiteralText(rpOption.message) : null);
            player.sendSystemMessage(feedBack, Util.NIL_UUID);
        });
        return 1;
    }
}