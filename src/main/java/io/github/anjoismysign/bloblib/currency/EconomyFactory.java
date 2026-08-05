package io.github.anjoismysign.bloblib.currency;

import io.github.anjoismysign.bloblib.manager.ManagerDirector;
import io.github.anjoismysign.bloblib.manager.ObjectDirector;
import io.github.anjoismysign.bloblib.manager.ObjectDirectorData;

public class EconomyFactory {

    /**
     * Creates a new ObjectDirector (of type Currency).
     * No commands or tab completions are added.
     *
     * @param managerDirector The ManagerDirector
     */
    public static ObjectDirector<Currency> CURRENCY_DIRECTOR(ManagerDirector managerDirector,
                                                             String objectName) {
        ObjectDirector<Currency> director = new ObjectDirector<>(managerDirector, ObjectDirectorData.simple(managerDirector.getRealFileManager(),
                objectName), file -> Currency.fromFile(file, managerDirector), true, false);
        director.getBuilderManager().setBuilderBiFunction(
                CurrencyBuilder::build);
        return director;
    }

}
