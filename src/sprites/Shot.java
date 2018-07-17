package sprites;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class Shot extends Sprite {

    private static final double SHOT_VELOCITY = -5;
    
    private RotateTransition rt;
    
    public Shot() {
        Path body = new Path();
        MoveTo m1 = new MoveTo(0.0, -6.0);
        LineTo l1 = new LineTo(4.0, -3.0);
        LineTo l2 = new LineTo(4.0, 3.0);
        LineTo l3 = new LineTo(0.0, 6.0);
        LineTo l4 = new LineTo(-4.0, 3.0);
        LineTo l5 = new LineTo(-4.0, -3.0);
        body.getElements().addAll(m1, l1, l2, l3, l4, l5, new ClosePath());
        body.setScaleX(1.2);
        body.setScaleY(1.2);
                
        body.setFill(Color.RED);
        
        rt = new RotateTransition(Duration.seconds(2.0), body);
        rt.setFromAngle(0.0);
        rt.setToAngle(360.0);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.play();
        
        getChildren().addAll(body);
    }
    
    public void stopAnimation() {
        rt.stop();
    }
    
    @Override
    public void update() {
        setTranslateY(getTranslateY() + SHOT_VELOCITY);
    }
    
}
