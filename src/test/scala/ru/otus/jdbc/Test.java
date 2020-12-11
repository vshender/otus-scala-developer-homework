package ru.otus.jdbc;

import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_6;

public class Test {

    public static void main(String[] args) throws IOException, SQLException {
        final EmbeddedPostgres postgres = new EmbeddedPostgres(V9_6);
// predefined data directory
// final EmbeddedPostgres postgres = new EmbeddedPostgres(V9_6, "/path/to/predefined/data/directory");
        final String url = postgres.start("localhost", 5432, "dbName", "userName", "password");

// connecting to a running Postgres and feeding up the database
        final Connection conn = DriverManager.getConnection(url);
        conn.createStatement().execute("DROP TABLE films;");
        conn.createStatement().execute("CREATE TABLE films (code char(5));");
        conn.createStatement().execute("INSERT INTO films VALUES ('movie');");

// ... or you can execute SQL files...
//postgres.getProcess().importFromFile(new File("someFile.sql"))
// ... or even SQL files with PSQL variables in them...
//postgres.getProcess().importFromFileWithArgs(new File("someFile.sql"), "-v", "tblName=someTable")
// ... or even restore database from dump file
//postgres.getProcess().restoreFromFile(new File("src/test/resources/test.binary_dump"))

// performing some assertions
        final Statement statement = conn.createStatement();

        boolean execute = statement.execute("SELECT * FROM films;");
        statement.getResultSet().next();
        String code = statement.getResultSet().getString("code");


        System.out.println(execute);
        System.out.println(code);

//        assertThat(statement.execute("SELECT * FROM films;"), is(true));
//        assertThat(statement.getResultSet().next(), is(true));
//        assertThat(statement.getResultSet().getString("code"), is("movie"));

// close db connection
        conn.close();
// stop Postgres
        postgres.stop();
    }


}
