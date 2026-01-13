package io.github.anjoismysign.bloblib.middleman.skript;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.anjoismysign.bloblib.api.BlobLibEconomyAPI;
import io.github.anjoismysign.bloblib.vault.multieconomy.ElasticEconomy;
import net.milkbowl.vault.economy.IdentityEconomy;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

public class ElasticEconomyBalanceExpression extends SimpleExpression<Number> {

    private Expression<OfflinePlayer> playerExpression;
    private Expression<String> implementationExpression;
    private Expression<String> worldExpression;

    @Override
    public boolean isSingle() { return true; }

    @Override
    public Class<? extends Number> getReturnType() { return Number.class; }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<OfflinePlayer>) expressions[0];
        if (expressions.length > 1 && expressions[1] instanceof Expression) implementationExpression = (Expression<String>) expressions[1];
        if (expressions.length > 2 && expressions[2] instanceof Expression) worldExpression = (Expression<String>) expressions[2];
        return true;
    }

    @Override
    protected Number[] get(Event e) {
        OfflinePlayer player = playerExpression.getSingle(e);
        if (player == null) return new Number[0];

        String currency = implementationExpression != null ? implementationExpression.getSingle(e) : null;
        String world = worldExpression != null ? worldExpression.getSingle(e) : null;

        ElasticEconomy elasticEconomy = BlobLibEconomyAPI.getInstance().getElasticEconomy();

        IdentityEconomy identityEconomy = currency == null ? elasticEconomy.getDefault() : elasticEconomy.getImplementation(currency);
        if (identityEconomy == null){
            return new Number[0];
        }

        double balance;
        try {
            if (world != null && !world.isEmpty()) balance = identityEconomy.getBalance(player, world);
            else balance = identityEconomy.getBalance(player);
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            try {
                balance = identityEconomy.getBalance(player);
            } catch (Exception ex2) {
                return new Number[0];
            }
        } catch (Exception ex) {
            return new Number[0];
        }
        return new Number[]{balance};
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "balance of " + playerExpression;
    }
}
