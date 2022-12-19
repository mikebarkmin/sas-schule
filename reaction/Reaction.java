import sas.*;
import sasio.*;
import java.awt.Color;

public class Reaction {

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
    Text auswertungsText;
    /**
     * Text zur Anzeige des zudrückenden Kreises im Reaktionszustand
     */
    Text zudrueckenderKreisText;
    /**
     * Der rote Kreis im Reaktionszustand
     */
    Circle gruenerKreis;
    /**
     * Der blaue Kreis im Reaktionszustand
     */
    Circle orangenerKreis;
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

    public Reaction() {
        fenster = new View(400, 400);
        fenster.setName("Reaction");
        fenster.setBackgroundColor(ColorPalette.DARK_PURPLE);

        // Initialisiere alle notwendigen Objekte
        auswertungsText = new Text(fenster.getWidth() / 2 - 150, fenster.getHeight() / 2 + 80, "Auswertung", Color.GREEN);
        zudrueckenderKreisText = new Text(10, 10, "Klicke", ColorPalette.OLD_LAVENDER);
        gruenerKreis = new Circle(-50, fenster.getHeight() / 2, 50, ColorPalette.DEEP_SPACE_SPARKLE);
        orangenerKreis = new Circle(fenster.getWidth() + 50, fenster.getHeight() / 2, 50, ColorPalette.TUMBLEWEED);
        startButton = new Button(fenster.getWidth() / 2 - 50, fenster.getHeight() / 2 - 25, 100, 50, "Start", ColorPalette.GRULLO);
        weiterButton = new Button(fenster.getWidth() / 2 - 75, fenster.getHeight() / 2 - 25, 150, 50, "Weiter", ColorPalette.GRULLO);

        // Verstecke alle Objekte
        auswertungsText.setHidden(true);
        zudrueckenderKreisText.setHidden(true);
        gruenerKreis.setHidden(true);
        orangenerKreis.setHidden(true);
        startButton.setHidden(true);
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
            fenster.wait(100);
        }

    }

    /**
     * Die Methode regelt den Ablauf im Startzustand
     */
    public void ablaufStart() {
        // Zeige alle notwendigen Objekte
        startButton.setHidden(false);
        // Wenn Start gedrückt wurde, gehe in den Reaktion-Status
        // und verstecke irrelevante Objekte
        if (startButton.clicked()) {
            startButton.setHidden(true);
            // Wechsel in den Reaktionszustand
            zustand = 1;
        }
    }

    /**
     * Die Methode regelt den Ablauf im Reaktionszustand
     */
    public void ablaufReaktion() {
        // Zeige alle notwendigen Objekte
        gruenerKreis.setHidden(false);
        orangenerKreis.setHidden(false);
        zudrueckenderKreisText.setHidden(false);

        // Bewege Kreise zu einer zufälligen Position
        gruenerKreis.moveTo(
            Tools.randomNumber(50, fenster.getWidth() - 50),
            Tools.randomNumber(50, fenster.getHeight() - 50)
        );
        orangenerKreis.moveTo(
            Tools.randomNumber(50, fenster.getWidth() - 50), 
            Tools.randomNumber(50, fenster.getHeight() - 50)
        );
        while(gruenerKreis.intersects(orangenerKreis)) {
            gruenerKreis.moveTo(
                Tools.randomNumber(50, fenster.getWidth() - 50),
                Tools.randomNumber(50, fenster.getHeight() - 50)
            );
            orangenerKreis.moveTo(
                Tools.randomNumber(50, fenster.getWidth() - 50), 
                Tools.randomNumber(50, fenster.getHeight() - 50)
            );
        }

        // Skaliere die Kreise entsprechend des Punktestandes
        // Mehr Punkte = kleinere Kreise
        // Weniger Punkte = größere Kreise
        // 40.000 Punkte sind die Referenz. 
        // Das heißt je näher an 40.000 Punkte, desto kleiner der Kreis.
        double factor = 1 - (punkte/40000.0);
        // Limitiere den Wertebreich des Faktors
        // auf [0.05; 2]
        if (factor > 2) {
            factor = 2;
        } else if (factor < 0.05) {
            factor = 0.05;
        }
        gruenerKreis.scaleTo(50 * factor, 50 * factor);
        orangenerKreis.scaleTo(50 * factor, 50 * factor);

        // Merke dir die Startzeit in Millisekunden zu Berechnung der Punkte
        startZeit = Tools.getStartTime();
        
        // Wähle einen zufällig einen Kreis, der gedrückt werden soll
        int zudrueckenderKreis = Tools.randomNumber(1, 2);

        // Zeige an, welcher Kreis gedrückt werden muss
        if (zudrueckenderKreis == 1) {
            zudrueckenderKreisText.setFontColor(ColorPalette.DEEP_SPACE_SPARKLE);
        } else if (zudrueckenderKreis == 2) {
            zudrueckenderKreisText.setFontColor(ColorPalette.TUMBLEWEED);
        } 

        // Warte solange bis der einer der Kreise gecklickt wurde
        // und merke den gedrückten Kreis.
        while (true) {
            if (gruenerKreis.mousePressed()) {
                gedrueckterKreis = 1;
                // verlasse die Schleife
                break;
            } else if (orangenerKreis.mousePressed()) {
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
            int neuePunkte = 3000 - (int) gebrauchteZeit;
            if (neuePunkte < 0) {
                neuePunkte = 0;
            }
            punkte = punkte + neuePunkte;
        } else {
            // Punktabzug, wenn der falsche Kreis gedrückt wurde
            punkte = punkte - 2000;
        }

        // Verstecke alle nicht mehr benötigten Objekte
        zudrueckenderKreisText.setHidden(true);
        gruenerKreis.setHidden(true);
        orangenerKreis.setHidden(true);
        // Wechsel in den Auswertungszustand
        zustand = 2;
    }

    /**
     * Die Methode regelt den ablauf im Auswertungszustand
     */
    public void ablaufAuswertung() {
        // Zeige alle notwendigen Objekte
        auswertungsText.setHidden(false);
        weiterButton.setHidden(false);
        auswertungsText.setText("Punkte: " + punkte);

        if (weiterButton.clicked()) {
            auswertungsText.setHidden(true);
            weiterButton.setHidden(true);
            // Wechsel in den Reaktionszustand
            zustand = 1;
        }
    }
    
    
    public static void main(String[] args) {
        new Reaction();
    }
}