package us.mytheria.bloblib.jlib.storage.database;

import us.mytheria.bloblib.jlib.storage.StorageLoader;

public abstract class SQLLoader extends StorageLoader {
  protected final SQLDatabase database;
  
  public SQLLoader(SQLDatabase database) {
    super(database);
    this.database = (SQLDatabase)this.storage;
  }
  
  public SQLDatabase getDatabase() {
    return this.database;
  }
}
