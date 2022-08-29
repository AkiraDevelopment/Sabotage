package ml.sabotage.database.migrations;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_Create_Tables extends DataMigration {

    public _1_Create_Tables() {
        super(1);
    }


    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        try(Statement statement = connection.createStatement()){
            statement.executeUpdate("CREATE TABLE " + tablePrefix + "player_data (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "karma INTEGER NOT NULL, " +
                    "lifetime_karma INTEGER NOT NULL, " +
                    "wins INTEGER NOT NULL, " +
                    "losses INTEGER NOT NULL, " +
                    "sabpasses INTEGER NOT NULL, " +
                    "kills INTEGER NOT NULL, " +
                    "correct_kills INTEGER NOT NULL, " +
                    "incorrect_kills INTEGER NOT NULL, " +
                    "deaths INTEGER NOT NULL, " +
                    "UNIQUE (uuid)" +
                    ")");
        }
    }
}
