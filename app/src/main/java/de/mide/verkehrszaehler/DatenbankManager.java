package de.mide.verkehrszaehler;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;


/**
 * In dieser Klasse kapseln wir alle Datenbank-Zugriffe.
 * <br><br>
 *
 * Für jede Zähler-Erhöhung wird eine Zeile in die Tabelle geschrieben, die neben
 * dem Namen des Zählers auch die den Zeitstempel enthält.
 * <br><br>
 *
 * This project is licensed under the terms of the BSD 3-Clause License.
 */
public class DatenbankManager extends SQLiteOpenHelper {

    /** Tag für das Schreiben von Log-Nachrichten. */
    private static final String TAG4LOGGING = "DBManager";

    /** Prepared Statement um alle Zähler zu löschen. */
    protected SQLiteStatement _preparedStatementAllesLoeschen = null;

    /** Prepared Statement um einen bestimmten Zähler um "1" zu erhöhen. */
    protected SQLiteStatement _preparedStatementZaehlerErhoehen = null;


    /**
     * Konstruktor, der Super-Konstruktor aufruft um Name der Datenbank und
     * Versions-Nummer festzulegen. Danach werden die <i>Prepared Statements</i>
     * erzeugt.
     *
     * @param context  Selbstreferenz auf Activity, die dieses Objekt erzeugt hat
     */
    public DatenbankManager(Context context) {

        super(context,
                "Verkehszaehlung.db",  // Name der DB
                null,                  // Default-CursorFactory verwenden
                1                      // Versions-Nummer der Datenbank
              );


        // Prepared Statements erzeugen

        SQLiteDatabase db = getReadableDatabase();

        _preparedStatementAllesLoeschen =
                db.compileStatement("DELETE FROM zaehler");

        _preparedStatementZaehlerErhoehen =
                db.compileStatement("INSERT INTO zaehler (name, datumzeit) VALUES ( ?, ? )");
    }

    /**
     * Abstrakte Methode aus der Oberklasse {@link SQLiteOpenHelper},
     * muss also überschrieben werden, damit diese Klasse nicht selbst
     * wieder abstrakt ist. Wird aufgerufen wenn die Datenbank für diese
     * App ganz neu angelegt werden muss.
     *
     * @param db  Referenz auf Datenbank-Objekt
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            final String createStatement =
                    "CREATE TABLE zaehler (      " +
                    "  name       TEXT NOT NULL, " +
                    "  datumzeit  LONG NOT NULL  " + // Zeitstempel als Anzahl Sekunden seit 1.1.1970
                    ")";

            db.execSQL( createStatement );

            Log.i(TAG4LOGGING, "Datenbank-Tabelle wurde angelegt.");

        } catch (SQLException ex) {

            String nachricht = "Fehler beim Anlegen Datenbank-Tabelle: " + ex.getMessage();
            Log.e(TAG4LOGGING, nachricht, ex);
        }
    }


    /**
     * Abstrakte Methode aus der Oberklasse {@link SQLiteOpenHelper},
     * muss also überschrieben werden, damit diese Klasse nicht selbst
     * wieder abstrakt ist. Wird aufgerufen wenn die Datenbank für diese
     * App auf eine neue Version aktualisiert werden muss.
     *
     * @param db  Referenz auf Datenbank-Objekt
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Absichtlich leer gelassen, da wir diese Methode für die
        // erste Version der App nicht überschreiben müssen.
    }

    /**
     * Methode zum Auslesen aktueller Wert eines bestimmten Zählers.
     *
     * @param zaehlerName  Names des auszulesenden Zählers.
     *
     * @return  Aktueller Wert des Zählers; hat Wert 0 wenn Zähler nicht gefunden.
     */
    public int getZaehlerWert(String zaehlerName) {

        return 0;
    }


    /**
     * Erhöht einen Zähler um "+1".
     *
     * @param zaehlerName  Name des Zählers, der zu erhöhen ist.
     *
     * @return  Neuer Wert des Zählers.
     */
    public int inkrementZaehler(String zaehlerName) {

        _preparedStatementZaehlerErhoehen.clearBindings();

        Date jetztDate = new Date();
        long sekundenSeit1970 = jetztDate .getTime() / 1000;


        // INSERT INTO zaehler (name, datumzeit) VALUES ( ?, ? )
        _preparedStatementZaehlerErhoehen.bindString( 1, zaehlerName      );
        _preparedStatementZaehlerErhoehen.bindLong  ( 2, sekundenSeit1970 );

        long idVonNeuerZeile = _preparedStatementZaehlerErhoehen.executeInsert();

        Log.i(TAG4LOGGING, "Neue Zeile mit ID=" + idVonNeuerZeile + " eingefügt.");

        return getZaehlerWert(zaehlerName);
    }


    /**
     * Löscht alle Zähler, d.h. macht die Datenbank-Tabelle leer.
     *
     * @throws SQLException  Datenbankfehler beim Löschen aufgetreten.
     */
    public void zaehlerZuruecksetzen() throws SQLException {

        int anzZeilen = _preparedStatementAllesLoeschen.executeUpdateDelete();

        Log.i(TAG4LOGGING, "Ganze Tabelle mit " + anzZeilen + " Zeilen gelöscht.");
    }

}
