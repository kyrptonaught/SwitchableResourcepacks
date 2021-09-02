package net.kyrptonaught.switchableresourcepacks;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PackListArgumentType implements ArgumentType<String> {

    private PackListArgumentType() {
    }

    public static PackListArgumentType word() {
        return new PackListArgumentType();
    }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        SwitchableResourcepacksMod.rpOptionHashMap.keySet().forEach(pack -> {
            if (pack.startsWith(builder.getRemainingLowerCase()))
                builder.suggest(pack);
        });
        return builder.buildFuture();
    }

    @Override
    public String parse(final StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public String toString() {
        return "string()";
    }

    @Override
    public Collection<String> getExamples() {
        return StringArgumentType.StringType.SINGLE_WORD.getExamples();
    }

    public static class StringArgumentSerializer implements ArgumentSerializer<PackListArgumentType> {
        public void toPacket(PackListArgumentType stringArgumentType, PacketByteBuf packetByteBuf) {
        }

        public PackListArgumentType fromPacket(PacketByteBuf packetByteBuf) {
            return PackListArgumentType.word();
        }

        public void toJson(PackListArgumentType stringArgumentType, JsonObject jsonObject) {
        }
    }
}
