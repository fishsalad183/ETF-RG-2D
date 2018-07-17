package sprites;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Background extends Sprite {

    Rectangle background;
    private List<Polyline> stars;
    private static final double STAR_VELOCITY = 1.5;
    private static final int MIN_STARS = 3, MAX_STARS = 10;

    public Background(int width, int height) {
        background = new Rectangle(0, 0, width + 10, height + 10);
        Stop[] stops = {new Stop(0, Color.BLACK), new Stop(1, Color.DARKBLUE)};
        background.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops));

        stars = new LinkedList<>();

        getChildren().addAll(background);
    }

    public Polyline createStar() {
        Polyline star = new Polyline(-3.0, -1.5, 0.0, -6.0, 3.0, -1.5,
                6.0, 0, 3.0, 1.5,
                0.0, 6.0, -3.0, 1.5,
                -6.0, 0.0, -3.0, -1.5);
        star.setStrokeWidth(0.0);
        star.setFill(new Color(1.0, 1.0, 0.4, 0.6));

        return star;
    }

    @Override
    public void update() {
        Iterator<Polyline> it = stars.iterator();
        while (it.hasNext()) {
            Polyline star = it.next();
            // setTranslateY is not necessary here if star movement is realized through TranslateTransition
            star.setTranslateY(star.getTranslateY() + STAR_VELOCITY);
            if (star.getTranslateY() >= background.getHeight() + 6.0) {
                this.getChildren().remove(star);
                it.remove();
            }
        }

        if (stars.size() < MAX_STARS) {
            double chance = stars.size() < MIN_STARS ? 1.0 : Math.random();
            if (chance > 0.988) {
                Polyline newStar = createStar();
                double factor = 0.6 + Math.random() * 0.4;
                newStar.setScaleX(factor);
                newStar.setScaleY(factor);
                newStar.setOpacity(factor);
                double y = -6.0 + Math.random() * (background.getHeight() / 10);
                newStar.setTranslateY(y);
                newStar.setTranslateX(30.0 + Math.random() * (background.getWidth() - 60.0));
                
                newStar.setOpacity(0.0);
                FadeTransition ft = new FadeTransition(Duration.millis(1000), newStar);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.setInterpolator(Interpolator.EASE_IN);
                ft.play();

                // Either TranslateTransition when a star is created or setTranslateY in each update.
//                TranslateTransition tt = new TranslateTransition(Duration.seconds((background.getHeight() - y) / (60 * STAR_VELOCITY)), newStar);
//                tt.setFromY(y);
//                tt.setToY(background.getHeight() + 6.0);
//                tt.setInterpolator(Interpolator.LINEAR);
//                tt.play();

                stars.add(newStar);
                this.getChildren().add(newStar);
            }
        }
    }
}
