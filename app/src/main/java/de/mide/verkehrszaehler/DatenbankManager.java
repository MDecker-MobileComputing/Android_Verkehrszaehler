package de.mide.verkehrszaehler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * In dieser Klasse kapseln wir alle Datenbank-Zugriffe.
 * <br><br>
 *
 * This project is licensed under the terms of the BSD 3-Clause License.
 */
public class DatenbankManager extends SQLiteOpenHelper {

    /**
     * Konstruktor, der Super-Konstruktor aufruft um Name der Datenbank und
     * Versions-Nummer festzulegen.
     *
     * @param context  Selbstreferenz auf Activity, die dieses Objekt erzeugt hat
     */
    public DatenbankManager(Context context) {

        super(context,
                "Verkehszaehlung.db",  // Name der DB
                null,                  // Default-CursorFactory verwenden
                1                      // Versions-Nummer der Datenbank
              );

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
     * Löscht alle Zähler.
     */
    public void zaehlerZuruecksetzen() {

    }

}
