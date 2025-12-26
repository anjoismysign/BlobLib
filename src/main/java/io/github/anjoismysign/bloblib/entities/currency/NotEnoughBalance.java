package io.github.anjoismysign.bloblib.entities.currency;

import org.jetbrains.annotations.NotNull;

public record NotEnoughBalance(@NotNull WalletOwner owner,
                               @NotNull String currency,
                               double missing) {

}
