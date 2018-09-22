package org.minigame.plugin.command.arguments;

import org.minigame.plugin.MinigamePlugin;
import org.minigame.utils.UniquieId;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.*;

public class MinigameIDTypeArgument extends CommandElement {

    public MinigameIDTypeArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Class<? extends UniquieId> parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String next = args.next();
        Optional<? extends UniquieId> opParse = MinigamePlugin.getUniquie().stream().filter(i -> i.getClass().getSimpleName().toLowerCase().equals(next.toLowerCase())).findFirst();
        if(opParse.isPresent()){
            return opParse.get().getClass();
        }
        throw args.createError(Text.of("Unknown type. Is it registered"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        List<String> list = new ArrayList<>();
        String peek = null;
        try{
            peek = args.peek();
        }catch(ArgumentParseException e){
            return list;
        }
        final String finalPeek = peek;
        Set<Class<? extends UniquieId>> set = new HashSet<>();
        MinigamePlugin.getUniquie().stream().forEach(f -> {
            if (!set.stream().anyMatch(t -> t.isAssignableFrom(f.getClass()))){
                set.add(f.getClass());
            }
        });
        set.stream().filter(c -> c.getSimpleName().toLowerCase().startsWith(finalPeek.toLowerCase())).forEach(c -> list.add(c.getSimpleName()));
        Collections.sort(list);
        return list;
    }
}
