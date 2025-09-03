package io.github.anjoismysign.bloblib.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.lang.ExpressionType;
import io.github.anjoismysign.bloblib.BlobLib;

public class BlobLibSkriptAddon {

    private SkriptAddon skriptAddon;

    public BlobLibSkriptAddon(){
        try {
            this.skriptAddon = Skript.registerAddon(BlobLib.getInstance());
            Skript.registerEffect(ElasticEconomyDepositEffect.class,
                    "deposit %number% to %offlineplayer% in implementation %string% in world %string%");
            Skript.registerEffect(ElasticEconomyWithdrawEffect.class,
                    "withdraw %number% from %offlineplayer%",
                    "withdraw %number% from %offlineplayer% in implementation %string%",
                    "withdraw %number% from %offlineplayer% in implementation %string% in world %string%");
            Skript.registerExpression(ElasticEconomyBalanceExpression.class, Number.class, ExpressionType.SIMPLE,
                    "balance of %offlineplayer%",
                    "balance of %offlineplayer% in implementation %string%",
                    "balance of %offlineplayer% in implementation %string% in world %string%");
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

}
