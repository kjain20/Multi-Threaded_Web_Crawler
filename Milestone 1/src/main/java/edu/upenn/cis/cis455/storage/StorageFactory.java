package edu.upenn.cis.cis455.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StorageFactory {
    public static StorageInterface getDatabaseInstance(String directory) {

        if (!Files.exists(Paths.get(directory))) {
            try {
                Files.createDirectory(Paths.get(directory));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	// TODO: factory object, instantiate your storage server
        // this must automatically start the server connection...take it into the constructor
        StorageImpl database_wrapper = new StorageImpl(directory);
        return database_wrapper;
    }
}
