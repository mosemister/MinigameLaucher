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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MinigameIDArgument <T extends UniquieId> extends CommandElement {

    Class<T> limitedTo;

    public MinigameIDArgument(@Nullable Text text, Class<T> class1){
        super(text);
        this.limitedTo = class1;
    }

    @Nullable
    @Override
    protected T parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String next = args.next();
        Optional<T> opResult = MinigamePlugin.getUniquieSet(this.limitedTo).stream()
                .filter(f -> f.getId().equals(next.toLowerCase()) || f.getIdName().toLowerCase().equals(next.toLowerCase()))
                .findFirst();
        if(opResult.isPresent()){
            return opResult.get();
        }
        throw args.createError(Text.of("Unknown " + this.limitedTo.getSimpleName()));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        String peek = null;
        try {
            peek = args.peek();
        } catch (ArgumentParseException e) {
            return new ArrayList<>();
        }
        final String finalPeek = peek;
        List<String> list = new ArrayList<>();
        MinigamePlugin.getUniquieSet(this.limitedTo).stream()
                //.filter(f -> f.getId().startsWith(peek.toLowerCase()) || f.getIdName().toLowerCase().startsWith(peek.toLowerCase()))
                .filter(f -> {
                    String peeklc = finalPeek.toLowerCase();
                    String id = f.getId();
                    String name = f.getIdName();
                    if(id.startsWith(peeklc)){
                        return true;
                    }
                    if(name.startsWith(peeklc)){
                        return true;
                    }
                    return false;
                })
                .forEach(f -> list.add(f.getId()));
        Collections.sort(list);
        return list;
    }
}
