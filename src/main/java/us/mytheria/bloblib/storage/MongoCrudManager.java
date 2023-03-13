package us.mytheria.bloblib.storage;

import me.anjoismysign.anjo.crud.CrudManager;
import me.anjoismysign.anjo.entities.Result;
import me.anjoismysign.anjo.logger.Logger;
import org.bson.Document;
import us.mytheria.bloblib.entities.BlobCrudable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class MongoCrudManager<T extends BlobCrudable> implements CrudManager<T> {
    private final MongoDB mongoDB;
    private final String collection;
    private final Function<String, T> createFunction;
    private final Logger logger;

    public MongoCrudManager(MongoDB mongoDB, String collection,
                            Function<String, T> createFunction,
                            Logger logger) {
        this.mongoDB = mongoDB;
        this.collection = collection;
        this.createFunction = createFunction;
        this.logger = logger;
        load();
    }

    public void load() {
        boolean isNewCollection = !mongoDB.collectionExists(collection);
        if (isNewCollection)
            log("Create collection '" + collection + "' was executed successfully.");
    }

    /**
     * @param id The primary key id
     * @return Whether the record exists
     */
    public boolean exists(String id) {
        Result<Document> result = mongoDB.getDocument(collection, new Document("_id", id));
        return result.isValid();
    }

    /**
     * Creates a new instance of the Crudable and registers it in the database
     * using the given identification. Will only update the identification.
     *
     * @param identification The identification to use.
     * @return The new instance of the Crudable.
     */
    @Override
    public T create(String identification) {
        T crudable = createFunction.apply(identification);
        Document document = crudable.getDocument();
        document.put("_id", identification);
        mongoDB.insertOne(collection, document);
        return crudable;
    }

    /**
     * Will attempt to read the Crudable with the given id from the database.
     * If not found, will create a new instance of the Crudable and register it
     * using the given id.
     *
     * @param id The id of the Crudable to get
     * @return The Crudable with the given id
     */
    @Override
    public T read(String id) {
        Result<Document> result = mongoDB.getDocument(collection, new Document("_id", id));
        if (result.isValid()) {
            Document document = result.value();
            T crudable = createFunction.apply(id);
            crudable.getDocument().putAll(document);
            return crudable;
        }
        return create();
    }

    /**
     * @param crudable The Crudable to be updated (defaults version to 0)
     */
    @Override
    public void update(T crudable) {
        Document document = crudable.getDocument();
        document.put("_id", crudable.getIdentification());
        mongoDB.insertOne(collection, document);
    }

    /**
     * @param id The id of the Crudable to delete
     */
    @Override
    public void delete(String id) {
        mongoDB.deleteOne(collection, new Document("_id", id));
    }

    /**
     * @param biConsumer First parameter is Crudable.
     *                   Second parameter is useless in MongoCrudManager.
     */
    public void forEachRecord(BiConsumer<T, Integer> biConsumer) {
        mongoDB.selectAllFromCollection(collection, document -> {
            T crudable = createFunction.apply(document.getString("_id"));
            crudable.getDocument().putAll(document);
            biConsumer.accept(crudable, 0);
        });
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private void log(String message) {
        if (logger != null)
            logger.log(message);
    }
}
