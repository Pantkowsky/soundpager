package com.example.komputer.discogify.database;

/**
 * Created by Komputer on 02/10/2016.
 */
public class DatabaseSchema {

    public static final class ArtistTable {
        public static final String NAME = "artists";

        public static final class Columns {
            public static final String UUID = "uuid";
            public static final String NAME = "name";
            public static final String TYPE = "type";
            public static final String URL = "url";
            public static final String ID = "id";
        }
    }
}
