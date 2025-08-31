package org.raghav.mongoStore;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import io.github.cdimascio.dotenv.Dotenv;
import static org.bson.codecs.configuration.CodecRegistries.*;

public class MongoStore<T> {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<T> collection;

    public MongoStore(String dbName, String collectionName, Class<T> clazz) {
        // Setup POJO codec registry
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMissing()
                .load();
        String uri = dotenv.get("MONGO_URI");
        this.mongoClient = MongoClients.create(uri);
        this.database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(collectionName, clazz)
                .withCodecRegistry(pojoCodecRegistry);
    }

    // Insert a POJO
    public void insert(T obj) {
        collection.insertOne(obj);
    }

    // Find by field
    public T findOne(String key, Object value) {
        return collection.find(Filters.eq(key, value)).first();
    }

    // Update a POJO (replaces fields in existing object)
    public void update(String key, Object value, T updateObj) {
        collection.replaceOne(Filters.eq(key, value), updateObj);
    }

    // Delete
    public void delete(String key, Object value) {
        collection.deleteOne(Filters.eq(key, value));
    }

    // Print all
    public void printAll() {
        collection.find().forEach(System.out::println);
    }

    // Close client
    public void close() {
        mongoClient.close();
    }
}
