package sprites;

import cameras.Camera;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import main.Main;

public class Player extends Sprite implements EventHandler<KeyEvent> {

    private static enum HorizontalStates {
        LEFT, RIGHT, STALL
    };

    private static enum VerticalStates {
        UP, DOWN, STALL
    };
    private static final double PLAYER_VELOCITY = 10;

    private List<Shot> shots = new LinkedList<>();

    private double horizontalVelocity = 0;
    private double verticalVelocity = 0;
    private HorizontalStates hState = HorizontalStates.STALL;
    private VerticalStates vState = VerticalStates.STALL;
    private boolean shotButtonPressed = false;
    
    private Camera camera = null;
    
    private Timeline timeline;

    public Player() {
        Path ship = new Path();
        MoveTo m1 = new MoveTo(-25.0, 15.0);
        QuadCurveTo q1 = new QuadCurveTo(-22.5, 3.5, -12.0, 0.0);
        QuadCurveTo q2 = new QuadCurveTo(0.0, -45.0, 12.0, 0.0);
        QuadCurveTo q3 = new QuadCurveTo(22.5, 3.5, 25.0, 15.0);
        QuadCurveTo q4 = new QuadCurveTo(0.0, 3.5, -25.0, 15.0);
        ship.getElements().addAll(m1, q1, q2, q3, q4, new ClosePath());
        ship.setFill(Color.SKYBLUE);
        
        Arc window = new Arc(0.0, 0.0, 6.0, 15.0, 0.0, 180.0);
        window.setFill(Color.BLUE);
        
        Rectangle pipe1 = new Rectangle(-12.0, 8.0, 5.0, 8.0);
        pipe1.setFill(Color.BLACK);
        Rectangle pipe2 = new Rectangle(7.0, 8.0, 5.0, 8.0);
        pipe2.setFill(Color.BLACK);
        
        Ellipse flame1 = new Ellipse(-9.5, 19.0, 3.0, 6.0);
        flame1.setStrokeWidth(0.0);
        Ellipse flame2 = new Ellipse(9.5, 19.0, 3.0, 6.0);
        flame2.setStrokeWidth(0.0);
        Stop[] stops = { new Stop(0.0, Color.ORANGERED), new Stop(1.0, Color.YELLOW) };
        RadialGradient rg1 = new RadialGradient(0.0, 0.0, 0.5, 0.5, 0.3, true, CycleMethod.NO_CYCLE, stops);
        RadialGradient rg2 = new RadialGradient(0.0, 0.0, 0.5, 0.5, 0.8, true, CycleMethod.NO_CYCLE, stops);
        KeyValue kv1_f1_fill = new KeyValue(flame1.fillProperty(), rg1, Interpolator.EASE_BOTH);
        KeyValue kv2_f1_fill = new KeyValue(flame1.fillProperty(), rg2, Interpolator.EASE_BOTH);
        KeyValue kv1_f2_fill = new KeyValue(flame2.fillProperty(), rg1, Interpolator.EASE_BOTH);
        KeyValue kv2_f2_fill = new KeyValue(flame2.fillProperty(), rg2, Interpolator.EASE_BOTH);
        KeyValue kv1_f1_scaleX = new KeyValue(flame1.scaleXProperty(), 0.85, Interpolator.EASE_BOTH);
        KeyValue kv2_f1_scaleX = new KeyValue(flame1.scaleXProperty(), 1.1, Interpolator.EASE_BOTH);
        KeyValue kv1_f2_scaleX = new KeyValue(flame2.scaleXProperty(), 0.85, Interpolator.EASE_BOTH);
        KeyValue kv2_f2_scaleX = new KeyValue(flame2.scaleXProperty(), 1.1, Interpolator.EASE_BOTH);
        KeyValue kv1_f1_scaleY = new KeyValue(flame1.scaleYProperty(), 0.85, Interpolator.EASE_BOTH);
        KeyValue kv2_f1_scaleY = new KeyValue(flame1.scaleYProperty(), 1.1, Interpolator.EASE_BOTH);
        KeyValue kv1_f2_scaleY = new KeyValue(flame2.scaleYProperty(), 0.85, Interpolator.EASE_BOTH);
        KeyValue kv2_f2_scaleY = new KeyValue(flame2.scaleYProperty(), 1.1, Interpolator.EASE_BOTH);
        KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1_f1_fill, kv1_f1_scaleX, kv1_f1_scaleY, kv1_f2_fill, kv1_f2_scaleX, kv1_f2_scaleY);
        KeyFrame kf2 = new KeyFrame(Duration.ONE, kv2_f1_fill, kv2_f1_scaleX, kv2_f1_scaleY, kv2_f2_fill, kv2_f2_scaleX, kv2_f2_scaleY);
        timeline = new Timeline(kf1, kf2);
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        getChildren().addAll(ship, window, pipe1, pipe2, flame1, flame2);
    }
    
    public void stopAnimation() {
        timeline.stop();
    }

    private void setVelocity() {
        switch (hState) {
            case STALL:
                horizontalVelocity = 0;
                break;
            case RIGHT:
                horizontalVelocity = PLAYER_VELOCITY;
                break;
            case LEFT:
                horizontalVelocity = -PLAYER_VELOCITY;
                break;
            default:
                break;
        }
        switch (vState) {
            case STALL:
                verticalVelocity = 0;
                break;
            case UP:
                verticalVelocity = -PLAYER_VELOCITY;
                break;
            case DOWN:
                verticalVelocity = PLAYER_VELOCITY;
                break;
            default:
                break;
        }
    }

    public List<Shot> getShots() {
        return shots;
    }

    public void setShots(List<Shot> s) {
        shots = s;
    }

    private void makeShot() {
        Shot shot = new Shot();
        shot.setTranslateX(getTranslateX());
        shot.setTranslateY(getTranslateY() - 10);
        shots.add(shot);
    }

    @Override
    public void update() {
        if (getTranslateX() + horizontalVelocity < getBoundsInParent().getWidth() / 2 + 5) {
            setTranslateX(getBoundsInParent().getWidth() / 2 + 5);
        } else if (getTranslateX() + horizontalVelocity > Main.WINDOW_WIDTH - getBoundsInParent().getWidth() / 2 - 5) {
            setTranslateX(Main.WINDOW_WIDTH - getBoundsInParent().getWidth() / 2 - 5);
        } else {
            setTranslateX(getTranslateX() + horizontalVelocity);
        }

        if (getTranslateY() + verticalVelocity < getBoundsInParent().getHeight() / 2 + 5) {
            setTranslateY(getBoundsInParent().getHeight() / 2 + 5);
        } else if (getTranslateY() + verticalVelocity > Main.WINDOW_HEIGHT - getBoundsInParent().getHeight() / 2 - 5) {
            setTranslateY(Main.WINDOW_HEIGHT - getBoundsInParent().getHeight() / 2 - 5);
        } else {
            setTranslateY(getTranslateY() + verticalVelocity);
        }
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.RIGHT && event.getEventType() == KeyEvent.KEY_PRESSED) {
            hState = HorizontalStates.RIGHT;
            setVelocity();
        } else if (event.getCode() == KeyCode.LEFT && event.getEventType() == KeyEvent.KEY_PRESSED) {
            hState = HorizontalStates.LEFT;
            setVelocity();
        } else if (event.getCode() == KeyCode.RIGHT && event.getEventType() == KeyEvent.KEY_RELEASED && hState != HorizontalStates.LEFT) {
            hState = HorizontalStates.STALL;
            setVelocity();
        } else if (event.getCode() == KeyCode.LEFT && event.getEventType() == KeyEvent.KEY_RELEASED && hState != HorizontalStates.RIGHT) {
            hState = HorizontalStates.STALL;
            setVelocity();
        }

        if (event.getCode() == KeyCode.UP && event.getEventType() == KeyEvent.KEY_PRESSED) {
            vState = VerticalStates.UP;
            setVelocity();
        } else if (event.getCode() == KeyCode.DOWN && event.getEventType() == KeyEvent.KEY_PRESSED) {
            vState = VerticalStates.DOWN;
            setVelocity();
        } else if (event.getCode() == KeyCode.UP && event.getEventType() == KeyEvent.KEY_RELEASED && vState != VerticalStates.DOWN) {
            vState = VerticalStates.STALL;
            setVelocity();
        } else if (event.getCode() == KeyCode.DOWN && event.getEventType() == KeyEvent.KEY_RELEASED && vState != VerticalStates.UP) {
            vState = VerticalStates.STALL;
            setVelocity();
        }

        if (event.getCode() == KeyCode.SPACE && event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (!shotButtonPressed) {
                makeShot();
                shotButtonPressed = true;
            }
        } else if (event.getCode() == KeyCode.SPACE && event.getEventType() == KeyEvent.KEY_RELEASED) {
            shotButtonPressed = false;
        }
        
        if (event.getCode() == KeyCode.DIGIT1 && event.getEventType() == KeyEvent.KEY_PRESSED) {
            camera.fixCameraAt(null);
        } else if (event.getCode() == KeyCode.DIGIT2 && event.getEventType() == KeyEvent.KEY_PRESSED && camera != null) {
            camera.fixCameraAt(this);
        }
    }

    public void linkCamera(Camera c) {
        camera = c;
    }
}
