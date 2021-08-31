package net.kyrptonaught.switchableresourcepacks;


import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.kyrptonaught.kyrptconfig.config.ConfigManager;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;


public class SwitchableResourcepacksMod implements DedicatedServerModInitializer {
    public static final String MOD_ID = "switchableresourcepacks";
    public static ConfigManager.SingleConfigManager configManager = new ConfigManager.SingleConfigManager(MOD_ID, new ResourcePackConfig());

    public static HashMap<String, ResourcePackConfig.RPOption> rpOptionHashMap = new HashMap<>();
    public static CustomCriterion STARTED, FINISHED, FAILED;

    @Override
    public void onInitializeServer() {
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

    public ResourcePackConfig getConfig() {
        return ((ResourcePackConfig) configManager.getConfig());
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean b) {
        dispatcher.register(CommandManager.literal("loadresource")
                .requires((source) -> source.hasPermissionLevel(0))
                .then(CommandManager.argument("packname", MessageArgumentType.message())
                        .executes((commandContext) -> {
                            String packname = MessageArgumentType.getMessage(commandContext, "packname").asString();
                            ResourcePackConfig.RPOption rpOption = rpOptionHashMap.get(packname);
                            if (rpOption == null) {
                                commandContext.getSource().sendFeedback(new LiteralText("Packname: ").append(packname).append(" was not found"), false);
                                return 1;
                            }
                            ServerPlayerEntity player = commandContext.getSource().getPlayer();
                            STARTED.revoke(player);
                            FINISHED.revoke(player);
                            FAILED.revoke(player);
                            player.sendResourcePackUrl(rpOption.url, rpOption.hash, rpOption.required, rpOption.hasPrompt ? new LiteralText(rpOption.message) : null);
                            Text feedBack = new LiteralText("Enabled pack: ").append(packname);
                            commandContext.getSource().sendFeedback(feedBack, false);
                            return 1;
                        })));
    }
}