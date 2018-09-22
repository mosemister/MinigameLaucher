package org.minigame.plugin.command.arguments;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.minigame.map.truemap.MinigameMap;
import org.minigame.map.truemap.PositionableMap;
import org.minigame.plugin.MinigamePlugin;
import org.minigame.running.RunningGame;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Locatable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RunningPositionArgument extends CommandElement {

    public RunningPositionArgument(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Vector3d parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        Optional<String> opString = args.nextIfPresent();
        if((!opString.isPresent()) && source instanceof Locatable){
            Locatable locatable = (Locatable)source;
            Optional<RunningGame<? extends MinigameMap>> opRunning = MinigamePlugin.getRunningGame(locatable);
            if(!opRunning.isPresent()){
                throw args.createError(Text.of("You are not in a minigame"));
            }
            if(opRunning.get().getMap() instanceof PositionableMap){
                ((PositionableMap)opRunning.get().getMap()).getMinLoc(locatable.getLocation().getPosition()).getPosition();
            }
            throw args.createError(Text.of("This map does not support positions"));
        }else if(!opString.isPresent()){
            throw args.createError(Text.of(""));
        }
        String xs = opString.get();
        String ys = args.next();
        String zs = args.next();

        double x;
        double y;
        double z;

        try {
            x = Integer.parseInt(xs);
        } catch(NumberFormatException e) {
            try {
                x = Double.parseDouble(xs);
            } catch(NumberFormatException e1) {
                throw args.createError(Text.of("Position X (" + xs + ") is not a number"));
            }
        }
        try {
            y = Integer.parseInt(ys);
        } catch (NumberFormatException e){
            try {
                y = Double.parseDouble(ys);
            } catch(NumberFormatException e1) {
                throw args.createError(Text.of("Position Y (" + ys + ") is not a number"));
            }
        }
        try {
            z = Integer.parseInt(zs);
        } catch (NumberFormatException e){
            try {
                z = Double.parseDouble(zs);
            } catch(NumberFormatException e1) {
                throw args.createError(Text.of("Position Z (" + zs + ") is not a number"));
            }
        }
        return new Vector3d(x, y, z);
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return new ArrayList<>();
    }
}
