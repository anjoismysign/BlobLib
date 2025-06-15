package io.github.anjoismysign.psa.sql;

public interface SQLContainer {
    SQLDatabase getDatabase();

    void disconnect();
}
