package de.mide.verkehrszaehler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * Main-Activity für eine Android-App, die eine einfache Verkehrszähler-App
 * darstellt; hiermit soll die Verwendung einer SQLite-Datenbank demonstriert
 * werden.
 * <br><br>
 *
 * This project is licensed under the terms of the BSD 3-Clause License.
 */
public class MainActivity extends Activity
                          implements DialogInterface.OnClickListener {

    /** Tag für das Schreiben von Log-Nachrichten. */
    private static final String TAG4LOGGING = "Verkehrszaehler";

    /** Name für Zähler für KFZs ohne Bei- und Mitfahrer. */
    public static final String ZAEHLERNAME_KFZ_FAHRER_ALLEINE = "KFZ_ALLEINE";

    /** Name für Zähler für KFZs mit mindestens einem Mitfahrer außer dem Fahrer. */
    public static final String ZAEHLERNAME_KFZ_MIT_MITFAHRER = "KFZ_MITFAHRER";

    /** Name für Zähler für LKWs. */
    public static final String ZAEHLERNAME_LKW = "LKW";


    /** Objekt für die Zugriffe auf die Datenbank. */
    protected DatenbankManager _datenbankManager = null;

    /** Button zum Erhöhen Zähler "KFZs nur mit Fahrer". */
    protected Button _buttonKfzNurFahrer = null;

    /** Button zum Erhöhen Zähler "KFZs mit Mitfahrer(n)". */
    protected Button _buttonKfzMitfahrer = null;

    /** Button zum Erhöhen Zähler "LKW". */
    protected Button _buttonLkw = null;

    /** Button zum Löschen der Zählerstände nach Sicherheitsabfrage. */
    protected Button _buttonLoeschen = null;


    /**
     * Lifecyle-Methode; lädt Layout-Datei und erzeugt Instanz der Klasse {@link DatenbankManager}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _datenbankManager = new DatenbankManager(this);

    }


    /**
     * Event-Handler für den Button, um den Zähler "KFZ nur Fahrer" zu erhöhren.
     * Diese Methode wird in der Layout-Datei mit dem XML-Attribut {@code android:onClick}
     * dem Button als Event-Handler-Methode zugewiesen.
     * <br><br>
     *
     * @param view  Button, der das Event ausgelöst hat.
     */
    public void onButtonKfzNurFahrer(View view) {

        _datenbankManager.inkrementZaehler(ZAEHLERNAME_KFZ_FAHRER_ALLEINE);

        Log.i(TAG4LOGGING, "Zähler erhöht: KFZ nur mit Fahrer.");
    }


    /**
     * Event-Handler für den Button, um den Zähler "KFZ mit Mitfahrer" zu erhöhen.
     * Diese Methode wird in der Layout-Datei mit dem XML-Attribut {@code android:onClick}
     * dem Button als Event-Handler-Methode zugewiesen.
     * <br><br>
     *
     * @param view  Button, der das Event ausgelöst hat.
     */
    public void onButtonKfzMitMitfahrer(View view) {

        _datenbankManager.inkrementZaehler(ZAEHLERNAME_KFZ_MIT_MITFAHRER);

        Log.i(TAG4LOGGING, "Zähler erhöht: KFZ mit Mitfahrer.");
    }


    /**
     * Event-Handler für den Button, um den Zähler "LKW" zu erhöhen.
     * Diese Methode wird in der Layout-Datei mit dem XML-Attribut {@code android:onClick}
     * dem Button als Event-Handler-Methode zugewiesen.
     * <br><br>
     *
     * @param view  Button, der das Event ausgelöst hat.
     */
    public void onButtonLKW(View view) {

        _datenbankManager.inkrementZaehler(ZAEHLERNAME_LKW);

        Log.i(TAG4LOGGING, "Zähler erhöht: LKW.");
    }


    /**
     * Event-Handler für Button zum Löschen der Zähler nach Sicherheitsabfrage.
     * Diese Methode wird in der Layout-Datei mit dem XML-Attribut {@code android:onClick}
     * dem Button als Event-Handler-Methode zugewiesen.
     * <br><br>
     *
     * Das eigentliche Löschen ist in der Methode {@link MainActivity#onClick(DialogInterface, int)}
     * implementiert.
     *
     * @param view  Button, der das Event ausgelöst hat.
     */
    public void onButtonLoeschen(View view) {

        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);

        dialogBuilder.setTitle("Sicherheitsabfrage");
        dialogBuilder.setMessage("Möchten Sie wirklich alle Zählerstände zurücksetzen?");
        dialogBuilder.setPositiveButton("Ja"  , this);
        dialogBuilder.setNegativeButton("Nein", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    /**
     * Diese Methode wird in {@link MainActivity#onButtonLoeschen(View)} als Event-Handler
     * für den "Ja"-Button für den Dialog mit der Sicherheitsabfrage, ob wirklich alle
     * Zähler gelöscht werden sollen, gesetzt.
     */
    @Override
    public void onClick(DialogInterface dialog, int whichButton) {

        try {

            _datenbankManager.zaehlerZuruecksetzen();

            zeigeToast("Alle Zähler zurückgesetzt.");

        } catch (Exception ex) {

            String fehlernachricht = "Exception beim Löschen der Tabelle: " + ex.getMessage();
            Log.e(TAG4LOGGING, fehlernachricht, ex);
            zeigeToast(fehlernachricht);
        }
    }


    /**
     * Erzeugt Toast-Objekt zur Anzeige einer Nachricht.
     *
     * @param nachricht  Im Toast anzuzeigende Nachricht.
     */
    protected void zeigeToast(String nachricht) {

        Toast.makeText(this, nachricht, Toast.LENGTH_LONG).show();

        Log.i(TAG4LOGGING, "Toast-Nachricht ausgeben: " + nachricht);
    }

}
