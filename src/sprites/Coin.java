package sprites;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Coin extends Sprite {
    
    private static final double COIN_VELOCITY = 5.0;
    private final double x_velocity;
    private final double y_velocity;
    
    private Circle body;
    
    public Coin() {
        body = new Circle(10);
        body.setFill(Color.GOLD);
        this.getChildren().add(body);
        
        double fallAngle = (-15.0 + Math.random() * 30.0) * Math.PI / 180;   // if angle is 0, the coin will fall straight downwards
        x_velocity = Math.sin(fallAngle) * COIN_VELOCITY;
        y_velocity = Math.cos(fallAngle) * COIN_VELOCITY;
    }
    
    @Override
    public void update() {
        setTranslateX(getTranslateX() + x_velocity);
        setTranslateY(getTranslateY() + y_velocity);
    }
}
