package io.github.anjoismysign.bloblib.profile;

import org.jetbrains.annotations.NotNull;

public interface ProfileView {
    @NotNull String getIdentification();
    @NotNull String getName();

    static ProfileView of(@NotNull String identification,
                          @NotNull String name) {
        return new ProfileView() {
            @Override
            public @NotNull String getIdentification() {
                return identification;
            }

            @Override
            public @NotNull String getName() {
                return name;
            }
        };
    }
}
