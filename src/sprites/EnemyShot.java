package sprites;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class EnemyShot extends Sprite {
    
    private static final double MIN_VELOCITY = 2.0;
    private static final double MAX_VELOCITY = 4.0;
    private double xVelocity;
    private final double yVelocity;
    private static final int MIN_ZIGZAG = 20;
    private static final int MAX_ZIGZAG = 80;
    private final int zigzag;
    private int zigzagCounter;
    
    private final RotateTransition rt;
    
    public EnemyShot() {
//        Circle body = new Circle(0, 0, 6);
        Rectangle body = new Rectangle(0, 0, 12, 12);
        
        body.setFill(Color.GREENYELLOW);
        
        rt = new RotateTransition(Duration.seconds(1.0), body);
        rt.setFromAngle(0.0);
        rt.setToAngle(360.0);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.play();
        
        getChildren().add(body);
        
        double velocity = MIN_VELOCITY + Math.random() * (MAX_VELOCITY - MIN_VELOCITY);
        double angle = (-15.0 + Math.random() + 30.0) * Math.PI / 180;
        xVelocity = velocity * Math.sin(angle);
        yVelocity = velocity * Math.cos(angle);
        
        zigzag = (int) (MIN_ZIGZAG + Math.random() * (MAX_ZIGZAG - MIN_ZIGZAG));
        zigzagCounter = zigzag / 2;
    }
    
    public void stopAnimation() {
        rt.stop();
    }
    
    @Override
    public void update() {
        setTranslateX(getTranslateX() + xVelocity);
        setTranslateY(getTranslateY() + yVelocity);
        if (++zigzagCounter == zigzag) {
            zigzagCounter = 0;
            xVelocity = -xVelocity;
            Duration currentTime = rt.getCurrentTime();
            rt.stop();
            rt.setRate(-1);
            rt.playFrom(currentTime);
        }
    }
    
}
