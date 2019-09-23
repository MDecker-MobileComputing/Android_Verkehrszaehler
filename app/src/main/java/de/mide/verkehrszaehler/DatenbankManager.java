package de.mide.verkehrszaehler;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.HashMap;


/**
 * In dieser Klasse sind alle Datenbank-Zugriffe gekapselt.
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


    /** Name für Zähler für KFZs ohne Bei- und Mitfahrer. */
    public static final String ZAEHLERNAME_KFZ_FAHRER_ALLEINE = "KFZ_ALLEINE";

    /** Name für Zähler für KFZs mit mindestens einem Mitfahrer außer dem Fahrer. */
    public static final String ZAEHLERNAME_KFZ_MIT_MITFAHRER = "KFZ_MITFAHRER";

    /** Name für Zähler für LKWs. */
    public static final String ZAEHLERNAME_LKW = "LKW";


    /** Prepared Statement um einen bestimmten Zähler um "1" zu erhöhen. */
    protected SQLiteStatement _preparedStatementZaehlerErhoehen = null;


    /** Prepared Statement um alle Zähler zurückzusetzen. */
    protected SQLiteStatement _preparedStatementAlleZaehlerZuruecksetzen = null;

    /** Prepared Statement zum Auslesen eines bestimmten Zählers. */
    protected SQLiteStatement _preparedStatementEinenZaehlerAuslesen = null;


    /**
     * Konstruktor, der Super-Konstruktor aufruft um Name der Datenbank und
     * Versions-Nummer festzulegen. Danach werden die <i>Prepared Statements</i>
     * erzeugt.
     *
     * @param context  Selbstreferenz auf Activity, die dieses Objekt erzeugt hat.
     *
     * @throws SQLException  Datenbank-Fehler (z.B. Syntax-Fehler in SQL-Statement).
     */
    public DatenbankManager(Context context) throws SQLException {

        super(context,
                "Verkehszaehlung.db",  // Name der DB
                null,                  // Default-CursorFactory verwenden
                1                      // Versions-Nummer der Datenbank
             );


        // Prepared Statements erzeugen

        SQLiteDatabase db = getReadableDatabase();

        _preparedStatementZaehlerErhoehen =
                db.compileStatement("UPDATE zaehler SET anzahl = anzahl + 1 WHERE name = ?");

        _preparedStatementAlleZaehlerZuruecksetzen =
                db.compileStatement("UPDATE zaehler SET anzahl = 0");

        _preparedStatementEinenZaehlerAuslesen =
                db.compileStatement("SELECT anzahl FROM zaehler WHERE name = ?");
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

            // Datenbank-Tabelle anlegen
            final String createStatement =
                    "CREATE TABLE zaehler (        " +
                    "  name   TEXT    PRIMARY KEY, " +
                    "  anzahl INTEGER DEFAULT 0    " +
                    ")";

            db.execSQL( createStatement );

            Log.i(TAG4LOGGING, "Datenbanktabelle wurde angelegt.");


            //  Zähler in Tabelle einfügen
            long idNeueZeile = 0;

            SQLiteStatement prepStmtInsertZaehler =
                    db.compileStatement("INSERT INTO zaehler (name, anzahl) VALUES ( ?, 0 )");

            prepStmtInsertZaehler.bindString(1, ZAEHLERNAME_KFZ_FAHRER_ALLEINE);
            idNeueZeile = prepStmtInsertZaehler.executeInsert();
            if (idNeueZeile == -1) {

                throw new SQLException("Konnte Zähler " + ZAEHLERNAME_KFZ_FAHRER_ALLEINE +
                                       " nicht anlegen");
            }

            prepStmtInsertZaehler.bindString(1, ZAEHLERNAME_KFZ_MIT_MITFAHRER);
            idNeueZeile = prepStmtInsertZaehler.executeInsert();
            if (idNeueZeile == -1) {

                throw new SQLException("Konnte Zähler " + ZAEHLERNAME_KFZ_MIT_MITFAHRER +
                                       " nicht anlegen");
            }

            prepStmtInsertZaehler.bindString(1, ZAEHLERNAME_LKW);
            idNeueZeile = prepStmtInsertZaehler.executeInsert();
            if (idNeueZeile == -1) {

                throw new SQLException("Konnte Zähler " + ZAEHLERNAME_LKW +
                                       " nicht anlegen");
            }

            Log.i(TAG4LOGGING, "Zähler wurden angelegt.");

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
     *
     * @param oldVersion  Alte Version Datenbank-Schema die DB für diese App-Installation derzeit
     *                    hat.
     *
     * @param newVersion  Neue Version Datenbank-Schema die hergestellt werden soll.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Absichtlich leer gelassen, da wir diese Methode für die
        // erste Version der App nicht überschreiben müssen.
    }


    /**
     * Methode um alle Zähler-Werte auszulesen.
     *
     * @return  HashMap, bei der jeder Zähler-Name (String) auf die entsprechende Anzahl
     *          abgebildet wird; kann leer sein, ist aber nicht {@code null}.
     *
     * @throws SQLException  Datenbankfehler aufgetreten
     */
    public HashMap<String,Integer> getAlleZaehlerWerte() throws SQLException {

        HashMap<String,Integer> ergebnisHashMap = new HashMap<String,Integer>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT name, anzahl FROM zaehler ORDER BY NAME ASC",
                        null); // die "selectionArgs" brauchen wir hier nicht

        int anzahlErgebnisZeilen = cursor.getCount();
        if (anzahlErgebnisZeilen == 0) {

            Log.w(TAG4LOGGING, "Keinen einzigen Zähler gefunden.");
            return ergebnisHashMap;
        }


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            String zaehlerName = cursor.getString(0);
            int    zaehlerWert = cursor.getInt(1);

            ergebnisHashMap.put(zaehlerName, zaehlerWert);
        }

        cursor.close();

        return ergebnisHashMap;
    }


    /**
     * Methode zum Auslesen aktueller Wert eines bestimmten Zählers.
     *
     * @param zaehlerName  Names des auszulesenden Zählers.
     *
     * @return  Aktueller Wert des Zählers; hat Wert 0 wenn Zähler nicht gefunden.
     *
     * @throws SQLException  Datenbank-Fehler.
     */
    public int getZaehlerWert(String zaehlerName) throws SQLException {

        _preparedStatementEinenZaehlerAuslesen.clearBindings();

        // Prepared Statement: SELECT anzahl FROM zaehler WHERE name = ?
        _preparedStatementEinenZaehlerAuslesen.bindString(1, zaehlerName);

        long ergebnis = _preparedStatementEinenZaehlerAuslesen.simpleQueryForLong();
        // Diese Methode gibt es nur für Long und nicht für Int.

        return (int) ergebnis;
    }


    /**
     * Erhöht den Zähler mit Name {@code zaehlerName} um eins.
     *
     * @param zaehlerName  Name des Zählers, der zu erhöhen ist; der Zähler muss schon vorhanden
     *                     sein.
     *
     * @return  Neuer Wert des Zählers.
     *
     * @throws SQLException  Datenbank-Fehler.
     */
    public int inkrementZaehler(String zaehlerName) throws SQLException {

        // "UPDATE zaehler SET anzahl = anzahl + 1 WHERE name = ?"
        _preparedStatementZaehlerErhoehen.clearBindings();

        _preparedStatementZaehlerErhoehen.bindString(1, zaehlerName);

        int anzZeilenGeaendert = _preparedStatementZaehlerErhoehen.executeUpdateDelete();
        if (anzZeilenGeaendert != 1) {

            throw new SQLException(
                    "Nicht genau eine Tabellen-Zeile für Erhöhung von Zähler " + zaehlerName +
                    " geändert.");
        }

        return getZaehlerWert(zaehlerName);
    }


    /**
     * Löscht alle Zähler, d.h. macht die Datenbank-Tabelle leer.
     *
     * @throws SQLException  Datenbankfehler beim Löschen aufgetreten.
     */
    public void alleZaehlerZuruecksetzen() throws SQLException {

        // SQL-Statement: UPDATE zaehler SET anzahl = 0
        int anzZeilen = _preparedStatementAlleZaehlerZuruecksetzen.executeUpdateDelete();

        Log.i(TAG4LOGGING, "Ganze Tabelle mit " + anzZeilen + " Zeilen gelöscht.");
    }

}
