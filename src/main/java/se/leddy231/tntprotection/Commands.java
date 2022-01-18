package se.leddy231.tntprotection;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;;

public class Commands {

    public static void registerCommands() {
        LiteralArgumentBuilder<ServerCommandSource> rootBuilder = literal("tnt-protection");
        registerShowChunkCoords(rootBuilder);

        LiteralArgumentBuilder<ServerCommandSource> areasBuilder = literal("areas");
        registerAreasList(areasBuilder);
        registerAreasAdd(areasBuilder);
        registerAreasTest(areasBuilder);
        registerAreasDelete(areasBuilder);
        rootBuilder.then(areasBuilder);


        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(rootBuilder));
    }

    public static void registerShowChunkCoords(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.then(
                literal("chunk-coordinates")
                .executes(Commands::executeShowChunkCoords)
        );
    }

    public static void registerAreasList(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.then(
                literal("list")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(Commands::executeAreasList)
        );
    }

    public static void registerAreasAdd(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.then(
                literal("create")
                .requires(source -> source.hasPermissionLevel(4))
                .then(
                    argument("name", StringArgumentType.string())
                    .then(
                        literal("from")
                        .then(
                            argument("fromX", IntegerArgumentType.integer())
                            .then(
                                argument("fromZ", IntegerArgumentType.integer())
                                .then(
                                    literal("to")
                                    .then(
                                        argument("toX", IntegerArgumentType.integer())
                                        .then(
                                            argument("toZ", IntegerArgumentType.integer())
                                            .executes(Commands::executeAreasAdd)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
        );
    }

    public static void registerAreasTest(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.then(
                literal("test")
                .executes(Commands::executeAreasTest)
        );
    }

    public static void registerAreasDelete(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.then(
                literal("delete")
                .requires(source -> source.hasPermissionLevel(4))
                .then(
                    argument("name", StringArgumentType.string())
                    .executes(Commands::executeAreasDelete)
                )
        );
    }

    public static int executeShowChunkCoords(CommandContext<ServerCommandSource> context) {
        try {
            PlayerEntity player = context.getSource().getPlayer();
            ChunkPos coords = player.getChunkPos();
            player.sendMessage(new LiteralText("Your chunk coords are (x: " + coords.x + ", z: " + coords.z + ")"), false);
        } catch (Exception e) {
            TntProtection.LOGGER.error("Error during chunk-coords command");
            TntProtection.LOGGER.error(e);
        }

        return 0;
    }

    public static int executeAreasList(CommandContext<ServerCommandSource> context) {
        try {
            PlayerEntity player = context.getSource().getPlayer();
            ProtectionStateManager manager = ProtectionStateManager.instance;
            List<WhitelistArea> areas = manager.getAreas();
            if (areas.isEmpty()) {
                player.sendMessage(new LiteralText("No areas created"), false);
            }
            for (WhitelistArea area : areas) {
                player.sendMessage(new LiteralText("Area " + area.toChat()), false);
            }
        } catch (Exception e) {
            TntProtection.LOGGER.error("Error during areas list command");
            TntProtection.LOGGER.error(e);
        }

        return 0;
    }

    public static int executeAreasAdd(CommandContext<ServerCommandSource> context) {
        try {
            PlayerEntity player = context.getSource().getPlayer();
            String name = StringArgumentType.getString(context, "name");
            int fromX = IntegerArgumentType.getInteger(context, "fromX");
            int fromZ = IntegerArgumentType.getInteger(context, "fromZ");
            int toX = IntegerArgumentType.getInteger(context, "toX");
            int toZ = IntegerArgumentType.getInteger(context, "toZ");
            ProtectionStateManager manager = ProtectionStateManager.instance;
            WhitelistArea area = new WhitelistArea(name, fromX, fromZ, toX, toZ);
            manager.addArea(area);
            player.sendMessage(new LiteralText("Added new area " + area.toChat()), false);
        } catch (Exception e) {
            TntProtection.LOGGER.error("Error during areas add command");
            TntProtection.LOGGER.error(e);
        }

        return 0;
    }

    public static int executeAreasTest(CommandContext<ServerCommandSource> context) {
        try {
            PlayerEntity player = context.getSource().getPlayer();
            BlockPos pos = player.getBlockPos();
            ProtectionStateManager manager = ProtectionStateManager.instance;
            if (player.getEntityWorld().getRegistryKey().equals(World.OVERWORLD)) {
                for (WhitelistArea area : manager.getAreas()) {
                    if (area.isInside(pos)) {
                        player.sendMessage(new LiteralText("Inside area " + area.toChat()), false);
                        return 0;
                    }
                }
            }

            player.sendMessage(new LiteralText("Position not in whitelisted area"), false);
        } catch (Exception e) {
            TntProtection.LOGGER.error("Error during areas test command");
            TntProtection.LOGGER.error(e);
        }

        return 0;
    }

    public static int executeAreasDelete(CommandContext<ServerCommandSource> context) {
        try {
            PlayerEntity player = context.getSource().getPlayer();
            String name = StringArgumentType.getString(context, "name");
            ProtectionStateManager manager = ProtectionStateManager.instance;
            if (manager.removeArea(name)) {
                player.sendMessage(new LiteralText("Area \""+name+"\" deleted"), false);
            } else {
                player.sendMessage(new LiteralText("No area named \""+name+"\" found"), false);
            }
            
        } catch (Exception e) {
            TntProtection.LOGGER.error("Error during areas delete command");
            TntProtection.LOGGER.error(e);
        }

        return 0;
    }
}
