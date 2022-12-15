import sas.*;
import sasio.*;
import java.awt.Color;

public class Reaction {

    int status = 0; // 0 = Start, 1 = Reaktion, 2 = Auswertung, 3 = Ende
    int level = 0;
    int punkte = 0;
    int leben = 3;

    Text punkteAnzeige;
    Text auswertungsAnzeige;
    Circle roterKreis;
    Circle blauerKreis;
    Button startButton;
    View view;

    public static void main (String[] args) {
        new Reaction();
    }

    public Reaction() {
        view = new View(800, 600);
        view.setName("Reaction"); 
        view.setBackgroundColor(Color.BLACK);

        punkteAnzeige = new Text(-10, 10, "0", Color.DARK_GRAY);
        auswertungsAnzeige = new Text(300, 250, "Auswertung", Color.GREEN);
        auswertungsAnzeige.setHidden(true);
        roterKreis = new Circle(-50, view.getHeight() / 2, 50, Color.RED);
        blauerKreis = new Circle(view.getWidth() + 50, view.getHeight() / 2, 50, Color.BLUE);
        startButton = new Button(300, 250, 100, 50, "Start", Color.GREEN);
        // Game-Loop

        while(true) {
            if (status == 0) {
                ablaufStart();
            } else if (status == 1) {
                ablaufReaktion();
            } else if (status == 2) {
                ablaufAuswertung();
            } else if (status == 3) {
                ablaufEnde();
            }
            view.wait(100);
        }

    }

    public void ablaufStart() {
        if (startButton.clicked()) {
            status = 1;
            startButton.setHidden(true);
        }
    }

    public void ablaufReaktion() {
        if (view.keyDownPressed() && roterKreis.intersects(blauerKreis)) {
            level = level + 1;
            punkte = punkte + 1 * level;
            status = 2;
        } else if (roterKreis.getCenterX() > view.getWidth() && blauerKreis.getCenterX() < 0) {
            // Reset
            leben = leben - 1;
            if (leben < 0) {
                status = 3;
            } else {
                roterKreis.moveTo(-50, Tools.randomNumber(50, view.getHeight()));
                blauerKreis.moveTo(view.getWidth() + 50, Tools.randomNumber(50, view.getHeight()));
            }

        } else {
            roterKreis.move(1);
            blauerKreis.move(-1);
        }

    }

    public void ablaufAuswertung() {
        auswertungsAnzeige.setHidden(false);

    }

    public void ablaufEnde() {

    }
}