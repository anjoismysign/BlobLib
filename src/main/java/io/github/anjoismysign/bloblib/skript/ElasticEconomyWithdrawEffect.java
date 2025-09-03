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

public class ElasticEconomyWithdrawEffect extends Effect {

    private Expression<Number> amountExpr;
    private Expression<OfflinePlayer> playerExpr;
    private Expression<String> implementationExpression;
    private Expression<String> worldExpression;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        amountExpr = (Expression<Number>) expressions[0];
        playerExpr = (Expression<OfflinePlayer>) expressions[1];
        if (expressions.length > 2 && expressions[2] instanceof Expression) implementationExpression = (Expression<String>) expressions[2];
        if (expressions.length > 3 && expressions[3] instanceof Expression) worldExpression = (Expression<String>) expressions[3];
        return true;
    }

    @Override
    protected void execute(Event e) {
        Number number = amountExpr.getSingle(e);
        OfflinePlayer player = playerExpr.getSingle(e);
        if (number == null || player == null) return;

        String currency = implementationExpression != null ? implementationExpression.getSingle(e) : null;
        String world = worldExpression != null ? worldExpression.getSingle(e) : null;

        ElasticEconomy elasticEconomy = BlobLibEconomyAPI.getInstance().getElasticEconomy();

        IdentityEconomy identityEconomy = currency == null ? elasticEconomy.getDefault() : elasticEconomy.getImplementation(currency);
        if (identityEconomy == null){
            return;
        }

        try {
            EconomyResponse response;
            if (world != null && !world.isEmpty()) {
                response = identityEconomy.withdrawPlayer(player, world, number.doubleValue());
            } else {
                response = identityEconomy.withdrawPlayer(player, number.doubleValue());
            }
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            try {
                identityEconomy.withdrawPlayer(player, number.doubleValue());
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "withdraw " + amountExpr + " from " + playerExpr;
    }

}
