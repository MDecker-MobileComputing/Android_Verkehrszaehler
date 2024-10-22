package de.mide.verkehrszaehler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;


/**
 * Main-Activity für eine Android-App, die eine einfache Verkehrszähler-App
 * darstellt; hiermit soll die Verwendung einer SQLite-Datenbank demonstriert
 * werden.
 * <br><br>
 *
 * This project is licensed under the terms of the BSD 3-Clause License.
 */
public class MainActivity extends AppCompatActivity
                          implements DialogInterface.OnClickListener {

    /** Tag für das Schreiben von Log-Nachrichten. */
    private static final String TAG4LOGGING = "Verkehrszaehler";


    /**
     * Objekt für die Zugriffe auf die Datenbank; wird in Methode {@linke MainActivity#onCreate(Bundle)}
     * befüllt.
     */
    protected DatenbankManager _datenbankManager = null;

    /** Button zum Erhöhen Zähler "KFZs nur mit Fahrer". */
    protected Button _buttonKfzNurFahrer = null;

    /** Button zum Erhöhen Zähler "KFZs mit Mitfahrer(n)". */
    protected Button _buttonKfzMitfahrer = null;

    /** Button zum Erhöhen Zähler "LKW". */
    protected Button _buttonLkw = null;


    /**
     * Lifecyle-Methode; lädt Layout-Datei und füllt Member-Variablen mit Referenzen
     * auf Button-Objekte.
     * Es wird außerdem eine Instanz der Klasse {@link DatenbankManager} erzeugt
     * (mit Fehlerbehandlung).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _buttonKfzNurFahrer = findViewById( R.id.buttonKfzAllein    );
        _buttonKfzMitfahrer = findViewById( R.id.buttonKfzMitfahrer );
        _buttonLkw          = findViewById( R.id.buttonLkw          );

        actionBarKonfigurieren();

        try {

            _datenbankManager = new DatenbankManager(this);
            initZaehlerstaendeAufButtons();

        } catch (SQLException ex) {

            String fehlernachricht = "Exception bei Vorbereitung Datenbank-Manager: " + ex.getMessage();

            Log.e(TAG4LOGGING, fehlernachricht, ex);

            _buttonKfzNurFahrer.setEnabled(false);
            _buttonKfzMitfahrer.setEnabled(false);
            _buttonLkw.setEnabled(false);

            zeigeDialog("Fehler", fehlernachricht);
        }
    }


    /**
     * Titel der ActionBar setzen.
     */
    private void actionBarKonfigurieren() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {

            Toast.makeText(this, "Keine ActionBar vorhanden.", Toast.LENGTH_LONG).show();
            return;
        }

        actionBar.setTitle( R.string.app_name );
        //actionBar.setSubtitle(R.string.app_untertitel);
    }


    /**
     * Methode um die aktuellen Zählerstände aus der DB auszulesen und auf den
     * Buttons anzuzeigen.
     * Diese Methode muss am Ende der Methode {@link MainActivity#onCreate(Bundle)}
     * aufgerufen werden sowie nach dem Zurücksetzen aller Zähler.
     */
    protected void initZaehlerstaendeAufButtons() {

        String buttonText   = "";

        try {

            HashMap<String,Integer> zaehlerHashMap = _datenbankManager.getAlleZaehlerWerte();

            Integer zaehler1 = zaehlerHashMap.get(DatenbankManager.ZAEHLERNAME_KFZ_FAHRER_ALLEINE );
            buttonText   = getString(R.string.button_kfzNurFahrer);
            if (zaehler1 != null) {

                buttonText += " (" + zaehler1 + ")";

            } else {

                buttonText += " (???)";
                Log.e(TAG4LOGGING,
                        "Kein Zähler-Wert gefunden für " + DatenbankManager.ZAEHLERNAME_KFZ_FAHRER_ALLEINE + ".");
            }
            _buttonKfzNurFahrer.setText( buttonText );


            Integer zaehler2 = zaehlerHashMap.get(DatenbankManager.ZAEHLERNAME_KFZ_MIT_MITFAHRER);
            buttonText   = getString(R.string.button_kfzMitMitfahrer);
            if (zaehler2 != null) {

                buttonText += " (" + zaehler2 + ")";

            } else {

                buttonText += " (???)";
                Log.e(TAG4LOGGING,
                        "Kein Zähler-Wert gefunden für " + DatenbankManager.ZAEHLERNAME_KFZ_MIT_MITFAHRER  + ".");
            }
            _buttonKfzMitfahrer.setText( buttonText );


            Integer zaehler3 = zaehlerHashMap.get(DatenbankManager.ZAEHLERNAME_LKW);
            buttonText   = getString(R.string.button_lkw);
            if (zaehler3 != null) {

                buttonText  += " (" + zaehler3 + ")";

            } else {

                buttonText += " (???)";
                Log.e(TAG4LOGGING,
                        "Kein Zähler-Wert gefunden für " + DatenbankManager.ZAEHLERNAME_LKW + ".");
            }
            _buttonLkw.setText( buttonText );

        }
        catch (SQLException ex) {

            String fehlernachricht =
                    "Exception bei Anzeige von Zähler-Stand auf Button: " + ex.getMessage();

            Log.e(TAG4LOGGING, fehlernachricht, ex);

            zeigeDialog("Fehler", fehlernachricht);
        }
    }


    /**
     * Methode um einen bestimmten Zähler um "+1" zu erhöhen.
     *
     * @param zaehlerName  Name des Zählers, der um 1 erhöht werden soll.
     *
     * @return  Neuer Wert des Zählers (nach Erhöhung); liefert -1 zurück wenn
     *          ein Fehler aufgetreten ist.
     */
    protected int erhoeheZaehler(String zaehlerName) {

        int neuerZaehlerWert = -1;

        try {

            neuerZaehlerWert = _datenbankManager.inkrementZaehler( zaehlerName );

            Log.i(TAG4LOGGING, "Zähler " + zaehlerName + " erhöht: KFZ nur mit Fahrer, " +
                    "neuer Wert " + neuerZaehlerWert + ".");

        } catch (Exception ex) {

            String fehlernachricht =
                    "Exception bei Erhöhen von Zähler " + zaehlerName + ": " + ex.getMessage();

            Log.e(TAG4LOGGING, fehlernachricht, ex);

            zeigeDialog("Fehler", fehlernachricht);

            neuerZaehlerWert = -1;
        }

        return neuerZaehlerWert;
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

        String neuerButtonText = "";

        int neuerWert = erhoeheZaehler( DatenbankManager.ZAEHLERNAME_KFZ_FAHRER_ALLEINE );

        neuerButtonText  = getString(R.string.button_kfzNurFahrer);
        neuerButtonText += " (" + neuerWert + ")";

        _buttonKfzNurFahrer.setText( neuerButtonText );
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

        String neuerButtonText = "";

        int neuerWert = erhoeheZaehler( DatenbankManager.ZAEHLERNAME_KFZ_MIT_MITFAHRER );

        neuerButtonText  = getString(R.string.button_kfzMitMitfahrer);
        neuerButtonText += " (" + neuerWert + ")";

        _buttonKfzMitfahrer.setText( neuerButtonText );
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

        String neuerButtonText = "";

        int neuerWert = erhoeheZaehler( DatenbankManager.ZAEHLERNAME_LKW );

        neuerButtonText  = getString(R.string.button_lkw);
        neuerButtonText += " (" + neuerWert + ")";

        _buttonLkw.setText( neuerButtonText );
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

            _datenbankManager.alleZaehlerZuruecksetzen();

            initZaehlerstaendeAufButtons();

            zeigeToast("Alle Zähler zurückgesetzt.");

        } catch (Exception ex) {

            String fehlernachricht = "Exception beim Löschen der Tabelle: " + ex.getMessage();
            Log.e(TAG4LOGGING, fehlernachricht, ex);
            zeigeDialog("Fehler", fehlernachricht);
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


    /**
     * Hilfsmethods zum Anzeigen einer Nachricht in einem Dialog.
     *
     * @param titel  Titel des Dialogs.
     *
     * @param nachricht  Text der im Dialog angezeigt werden soll.
     */
    protected void zeigeDialog(String titel, String nachricht) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(titel);
        dialogBuilder.setMessage(nachricht);
        dialogBuilder.setPositiveButton("Weiter", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}
