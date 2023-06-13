package us.mytheria.bloblib.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import me.anjoismysign.anjo.entities.Result;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * MongoDB is a Document oriented storage.
 * The idea behind it is flexibility and scalability.
 * <p>
 * This is a small wrapper for CRUD operations.
 *
 * @author anjoismysign
 */
public class MongoDB {

    private final String connection;
    private final String database;

    private final MongoClient mongoClient;

    /**
     * Will create a new MongoDB instance with the default localhost connection and the provided database.
     *
     * @param database The database to use.
     * @return A new MongoDB instance with the default localhost connection and the provided database.
     */
    public static MongoDB LOCALHOST(String database) {
        return new MongoDB("mongodb://localhost:27017/", database);
    }

    /**
     * Attempts to retrieve a MongoDB instance from the provided configuration section.
     *
     * @param configurationSection The configuration section to retrieve from.
     * @return A valid result if found MongoDB settings, otherwise an invalid result.
     */
    public static Result<MongoDB> fromConfigurationSection(ConfigurationSection configurationSection) {
        if (configurationSection.isString("Hostname") &&
                configurationSection.isString("Database") &&
                configurationSection.isString("Username") &&
                configurationSection.isString("Password"))
            return Result.valid(loadFromConfigurationSection(configurationSection));
        if (!configurationSection.isConfigurationSection("Database"))
            return Result.invalidBecauseNull();
        ConfigurationSection section = configurationSection.getConfigurationSection("Database");
        if (section.isString("Hostname") &&
                section.isString("Database") &&
                section.isString("Username") &&
                section.isString("Password"))
            return Result.valid(loadFromConfigurationSection(section));
        return Result.invalidBecauseNull();
    }

    private static MongoDB loadFromConfigurationSection(ConfigurationSection configurationSection) {
        String host = configurationSection.getString("Hostname");
        String database = configurationSection.getString("Database");
        String username = configurationSection.getString("Username");
        String password = configurationSection.getString("Password");
        String connection = "mongodb+srv://" + username + ":" + password + "@" + host + "/?retryWrites=true&w=majority";
        return new MongoDB(connection, database);
    }

    /**
     * Creates a new MongoDB instance.
     *
     * @param connection The connection string to use.
     */
    public MongoDB(String connection, String database) {
        this.connection = connection;
        this.database = database;
        this.mongoClient = connect();
    }

    private MongoClient connect() {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        LoggerConfig loggerConfig = configuration.getLoggerConfig("org.mongodb.driver");
        loggerConfig.setLevel(Level.WARN);
        return MongoClients.create(connection);
    }

    private MongoDatabase getDatabase() {
        return mongoClient.getDatabase(this.database);
    }

    /**
     * Attempts to retrieve a Document from the provided collection and database.
     *
     * @param collection  The collection name to select from.
     * @param searchQuery The document to filter by.
     * @return A valid result if found, otherwise an invalid result.
     */
    public Result<Document> getDocument(String collection, Document searchQuery) {
        try (MongoClient client = connect()) {
            MongoCollection<Document> mongoCollection = getDatabase().getCollection(collection);
            Document iterable = mongoCollection.find(searchQuery).first();
            return Result.ofNullable(iterable);
        }
    }

    /**
     * Attempts to update the first document that matches the search query with the new values
     * provided in the newValues document.
     *
     * @param collection  The collection name to select from.
     * @param searchQuery The document to filter by.
     * @param replacement The document to replace with
     * @return True if the document was updated, false otherwise.
     */
    public boolean replaceOne(String collection, Document searchQuery,
                              Document replacement) {
        try (MongoClient client = connect()) {
            MongoCollection<Document> mongoCollection = getDatabase().getCollection(collection);
            UpdateResult result = mongoCollection.replaceOne(searchQuery, replacement);
            return result.getModifiedCount() > 0;
        }
    }

    /**
     * Attempts to update all documents that match the search query with the new values
     * provided in the newValues document.
     *
     * @param collection  The collection name to select from.
     * @param searchQuery The document to filter by.
     * @param newValues   The document to update with.
     * @return True if at least one document was updated, false otherwise.
     */
    public boolean updateMany(String collection, Document searchQuery,
                              Document newValues) {
        try (MongoClient client = connect()) {
            MongoCollection<Document> mongoCollection = getDatabase().getCollection(collection);
            Document updateQuery = new Document("$set", newValues);
            UpdateResult result = mongoCollection.updateMany(searchQuery, updateQuery);
            return result.getModifiedCount() > 0;
        }
    }

    /**
     * Attempts to delete the first document that matches the search query.
     *
     * @param collection  The collection name to select from.
     * @param searchQuery The document to filter by.
     * @return True if the document was deleted, false otherwise.
     */
    public boolean deleteOne(String collection, Document searchQuery) {
        try (MongoClient client = connect()) {
            MongoCollection<Document> mongoCollection = getDatabase().getCollection(collection);
            DeleteResult result = mongoCollection.deleteOne(searchQuery);
            return result.getDeletedCount() > 0;
        }
    }

    /**
     * Attempts to delete all documents that match the search query.
     *
     * @param collection  The collection name to select from.
     * @param searchQuery The document to filter by.
     * @return True if the document was deleted, false otherwise.
     */
    public boolean deleteMany(String collection, Document searchQuery) {
        try (MongoClient client = connect()) {
            MongoCollection<Document> mongoCollection = getDatabase().getCollection(collection);
            DeleteResult result = mongoCollection.deleteMany(searchQuery);
            return result.getDeletedCount() > 0;
        }

    }

    /**
     * Creates a new collection in the provided database.
     *
     * @param newCollection The name of the new collection.
     */
    public void createCollection(String newCollection) {
        try (MongoClient client = connect()) {
            getDatabase().createCollection(newCollection);
        }
    }

    /**
     * Checks if a collection exists in the provided database.
     *
     * @param collection The collection to check for.
     * @return True if the collection exists, false otherwise.
     */
    public boolean collectionExists(String collection) {
        try (MongoClient client = connect()) {
            return getDatabase().listCollectionNames().into(new ArrayList<>()).contains(collection);
        }
    }

    /**
     * Selects all documents provided in a collection and passes them to the consumer.
     *
     * @param collection The collection to select from.
     * @param consumer   The consumer to pass the documents to.
     */
    public void selectAllFromCollection(String collection, Consumer<Document> consumer) {
        try (MongoClient client = connect()) {
            MongoCollection<Document> mongoCollection = getDatabase().getCollection(collection);
            mongoCollection.find().forEach(consumer);
        }
    }

    /**
     * Inserts a document into a collection.
     *
     * @param collection The collection to insert into.
     * @param document   The document to insert.
     */
    public void insertOne(String collection, Document document) {
        try (MongoClient client = connect()) {
            MongoCollection<Document> mongoCollection = getDatabase().getCollection(collection);
            mongoCollection.insertOne(document);
        }
    }


}
