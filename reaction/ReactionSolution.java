import sas.*;
import sasio.*;
import java.awt.Color;

public class ReactionSolution {

    /**
     * Gibt den aktuellen Zustand des Programms an
     * 0: Start
     * 1: Reaktion
     * 2: Auswertung
     */
    int zustand = 0;
    /**
     * Enthält die aktuelle Punktzahl
     */
    int punkte = 0;
    /**
     * Enthält die Startzeit im Reaktions-Status
     */
    long startZeit = 0;
    /**
     * Enthält, welcher Kreis gedrückt werden muss
     */
    int gedrueckterKreis = 0; // 1 = Rot; 2 = Blau

    /**
     * Text zur Anzeige im Auswertungszustand
     */
    Text auswertungsAnzeige;
    /**
     * Text zur Anzeige des zudrückenden Kreises im Reaktionszustand
     */
    Text zudrueckenderKreisAnzeige;
    /**
     * Der rote Kreis im Reaktionszustand
     */
    Circle roterKreis;
    /**
     * Der blaue Kreis im Reaktionszustand
     */
    Circle blauerKreis;
    /**
     * Der Start-Button im Startzustand
     */
    Button startButton;
    /**
     * Der Weiter-Button im Auswertungszusand
     */
    Button weiterButton;
    /**
     * Das Hauptfenster
     */
    View fenster;

    public static void main(String[] args) {
        new ReactionSolution();
    }

    public ReactionSolution() {
        fenster = new View(400, 400);
        fenster.setName("Reaction");
        fenster.setBackgroundColor(Color.BLACK);

        // Initialisiere alle notwendigen Objekte
        auswertungsAnzeige = new Text(fenster.getWidth() / 2 - 100, fenster.getHeight() / 2 + 80, "Auswertung", Color.GREEN);
        zudrueckenderKreisAnzeige = new Text(10, 10, "Warte", Color.GREEN);
        roterKreis = new Circle(-50, fenster.getHeight() / 2, 50, Color.RED);
        blauerKreis = new Circle(fenster.getWidth() + 50, fenster.getHeight() / 2, 50, Color.BLUE);
        startButton = new Button(fenster.getWidth() / 2 - 50, fenster.getHeight() / 2 - 25, 100, 50, "Start", Color.GREEN);
        weiterButton = new Button(fenster.getWidth() / 2 - 75, fenster.getHeight() / 2 - 25, 150, 50, "Weiter", Color.PINK);

        // Verstecke alle irrelevanten Objekte
        zudrueckenderKreisAnzeige.setHidden(true);
        auswertungsAnzeige.setHidden(true);
        roterKreis.setHidden(true);
        blauerKreis.setHidden(true);
        weiterButton.setHidden(true);

        /**
         * Der Game-Loop wird kontunierlich ausgeführt.
         * Zwischen den einzelnen Zuständen wird gewechselt,
         * indem das Attribut zustand verändert wird.
         */
        while (true) {
            if (zustand == 0) {
                ablaufStart();
            } else if (zustand == 1) {
                ablaufReaktion();
            } else if (zustand == 2) {
                ablaufAuswertung();
            }
            // Limitieren der Aktualisierungsrate des Fensters
            // auf ca. 60 FPS, da 1 Sekunde = 1000 Millisekunden
            // und wir warten 1000 / 60 Millisekunden = 16.66666.
            fenster.wait(17);
        }

    }

    public void ablaufStart() {
        // Wenn Start gedrückt wurde, gehe in den Reaktion-Status
        // und verstecke irrelevante Objekte
        if (startButton.clicked()) {
            zustand = 1;
            startButton.setHidden(true); // TODO nicht verstecken
        }
    }

    public void ablaufReaktion() {
        // Zeige alle notwendigen Objekte
        roterKreis.setHidden(false);
        blauerKreis.setHidden(false);
        zudrueckenderKreisAnzeige.setHidden(false);

        // Bewege Kreise zu einer zufälligen Position
        roterKreis.moveTo(
            Tools.randomNumber(50, fenster.getWidth() - 50),
            Tools.randomNumber(50, fenster.getHeight() - 50)
        );
        blauerKreis.moveTo(
            Tools.randomNumber(50, fenster.getWidth() - 50), 
            Tools.randomNumber(50, fenster.getHeight() - 50)
        ); // TODO auf dem Fenster bewegen

        // Skaliere die Kreise entsprechend des Punktestandes
        // Mehr Punkte = kleinere Kreise
        // Weniger Punkte = größere Kreise

        double factor = 1 - (punkte/40000.0);
        // Limitiere den Wertebreich des Faktors
        // auf [0.1; 2]
        if (factor > 2) {
            factor = 2;
        } else if (factor < 0.1) {
            factor = 0.1;
        }
        roterKreis.scaleTo(50 * factor, 50 * factor);
        blauerKreis.scaleTo(50 * factor, 50 * factor);

        // Merke dir die Startzeit in Millisekunden
        startZeit = Tools.getStartTime();
        
        // Wähle einen zufällig einen Kreis, der gedrückt werden soll
        int zudrueckenderKreis = Tools.randomNumber(1, 2); // TODO 3 inkludieren

        // Zeige an welcher Kreis gedrückt werden muss
        if (zudrueckenderKreis == 1) {
            zudrueckenderKreisAnzeige.setFontColor(Color.BLUE);
            zudrueckenderKreisAnzeige.setText("Rot");
        } else if (zudrueckenderKreis == 2) {
            zudrueckenderKreisAnzeige.setFontColor(Color.RED);
            zudrueckenderKreisAnzeige.setText("Blau");
        }

        // Warte solange bis der rote oder blaue Kreis gecklickt wurde
        while (true) {
            // TODO je nach Punktestand sollen sich die Kreise bewegen
            if (roterKreis.mousePressed()) {
                gedrueckterKreis = 1;
                // verlasse die Schleife
                break;
            } else if (blauerKreis.mousePressed()) {
                gedrueckterKreis = 2;
                // verlasse die Schleife
                break;
            }
        }

        // Überprüfe ob der gedrückte Kreis, dem zufällig gewählten Kreis entspricht
        if (zudrueckenderKreis == gedrueckterKreis) {
            long endZeit = Tools.getStartTime();
            long gebrauchteZeit = endZeit - startZeit;

            // Gibt mindestens 0 Punkte und maximal 3000, wenn sofort geklickt wurde.
            // Nach 3s gib 0 Punkte
            punkte = punkte + Math.max(0, 3000 - (int) gebrauchteZeit);
        } else {
            // Punktabzug
            punkte = punkte - 2000;
        }

        // Verstecke alle nicht mehr notwendigen Objekte
        zudrueckenderKreisAnzeige.setHidden(true);
        roterKreis.setHidden(true);
        blauerKreis.setHidden(true); // TODO entfernen
        zustand = 2;
    }

    public void ablaufAuswertung() {
        // Zeige alle notwendigen Objekte
        auswertungsAnzeige.setHidden(false);
        weiterButton.setHidden(false);
        auswertungsAnzeige.setText("Punkte: " + punkte); // TODO punkte entfernen

        if (weiterButton.clicked()) {
            auswertungsAnzeige.setHidden(true);
            weiterButton.setHidden(true);
            zustand = 1;
        }
    }
}