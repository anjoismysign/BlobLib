package io.github.anjoismysign.bloblib.skript;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import io.github.anjoismysign.bloblib.api.BlobLibEconomyAPI;
import io.github.anjoismysign.bloblib.vault.multieconomy.ElasticEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.IdentityEconomy;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

public class ElasticEconomyDepositEffect extends Effect {

    private Expression<Number> amountExpression;
    private Expression<OfflinePlayer> playerExpression;
    private Expression<String> implementationExpression;
    private Expression<String> worldExpression;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        // pattern order: amount, player, [implementation], [world]
        amountExpression = (Expression<Number>) expressions[0];
        playerExpression = (Expression<OfflinePlayer>) expressions[1];
        // Skript will set the later patterns' expressions into exprs positions when present;
        // to be safe we check types/lengths:
        if (expressions.length > 2 && expressions[2] != null) implementationExpression = (Expression<String>) expressions[2];
        if (expressions.length > 3 && expressions[3] != null) worldExpression = (Expression<String>) expressions[3];
        return true;
    }

    @Override
    protected void execute(Event e) {
        Number number = amountExpression.getSingle(e);
        OfflinePlayer player = playerExpression.getSingle(e);
        if (number == null || player == null) return;

        String currency = implementationExpression != null ? implementationExpression.getSingle(e) : null;
        String world = worldExpression != null ? worldExpression.getSingle(e) : null;

        ElasticEconomy elasticEconomy = BlobLibEconomyAPI.getInstance().getElasticEconomy();

        IdentityEconomy identityEconomy = currency == null ? elasticEconomy.getDefault() : elasticEconomy.getImplementation(currency);
        if (identityEconomy == null){
            return;
        }

        // Try world-aware deposit if world provided and provider supports world method
        try {
            EconomyResponse response;
            if (world != null && !world.isEmpty()) {
                response = identityEconomy.depositPlayer(player, world, number.doubleValue());
            } else {
                response = identityEconomy.depositPlayer(player, number.doubleValue());
            }
            // Optionally: check resp.type == EconomyResponse.ResponseType.SUCCESS
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            // If the identity economy implementation doesn't expose world-overloaded methods,
            // fallback to non-world deposit:
            try {
                identityEconomy.depositPlayer(player, number.doubleValue());
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "deposit " + amountExpression + " to " + playerExpression;
    }

}
