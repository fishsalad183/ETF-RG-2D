package sprites;

import java.util.LinkedList;
import java.util.List;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import main.Main;

public class Enemy extends Sprite {

    private ParallelTransition dyingAnimation = null;
    private static final double DYING_DURATION = 0.7;

    private static List<EnemyShot> shots = new LinkedList<>();
//    private static final int MIN_SHOT_DELAY = 60;
//    private static final int MAX_SHOT_DELAY = 8 * 60;
//    private static int shotDelay;
//    private static int shotDelayCounter = 0;
//    static {
//        setShotDelay();
//    }
    
    private static List<Enemy> enemies = new LinkedList<>();
    
    private static final double ENEMY_VELOCITY = 1.8;
    private static double velocity = Math.random() > 0.5 ? ENEMY_VELOCITY : -ENEMY_VELOCITY;
    
    private static final int ANIMATION_TIMER_FREQUENCY = 60;
    private static final int MIN_MOVE_CYCLES = 20 * ANIMATION_TIMER_FREQUENCY;
    private static final int MAX_MOVE_CYCLES = 50 * ANIMATION_TIMER_FREQUENCY;
    private static boolean lastEnemyInListToSetVelocity = false;
    private static int moveCycles;
    private static int moveCycleCounter = 0;
    static {
        resetMoveCycles();
    }
    
    private final Group leftEye, rightEye;
    private ScaleTransition eyeBlinking;
    private final ParallelTransition earMovement = new ParallelTransition();

    public Enemy() {
        Rectangle body = new Rectangle(-25, -20, 50, 40);
        body.setFill(Color.YELLOW);
        body.setArcHeight(20.0);
        body.setArcWidth(20.0);
        
        leftEye = createEye(-10.0, -7.0);
        rightEye = createEye(10.0, -7.0);
        
        Arc mouth = new Arc(0.0, 5.0, 15.0, 10.0, 180.0, 180.0);
        mouth.setType(ArcType.CHORD);
        mouth.setFill(Color.BLACK);
        
        Path leftEar = createEar(-25.0, 0.0, true);
        Path rightEar = createEar(25.0, 0.0, false);
        earMovement.setCycleCount(Timeline.INDEFINITE);
        earMovement.setAutoReverse(true);
        earMovement.play();
        
        getChildren().addAll(body, leftEye, rightEye, mouth, leftEar, rightEar);
    }
    
    private Group createEye(double x, double y) {
        Group eye = new Group();
        Ellipse eyeball = new Ellipse(x, y, 8.0, 5.0);
        eyeball.setStrokeWidth(0.2);
        eyeball.setStroke(Color.BLACK);
        eyeball.setFill(Color.WHITE);
        Circle pupil = new Circle(x, y, 2.0);
        pupil.setFill(Color.BLACK);
        eye.getChildren().addAll(eyeball, pupil);
        return eye;
    }
    
    public void animateEye(boolean left) {
        eyeBlinking = new ScaleTransition(Duration.seconds(2.0));
        eyeBlinking.setNode(left ? leftEye : rightEye);
        eyeBlinking.setFromY(1.0);
        eyeBlinking.setToY(0.1);
        eyeBlinking.setInterpolator(Interpolator.EASE_BOTH);
        eyeBlinking.setCycleCount(ScaleTransition.INDEFINITE);
        eyeBlinking.setAutoReverse(true);
        eyeBlinking.play();
    }
    
    private Path createEar(double axisX, double axisY, boolean left) {
        double sideLength = 40.0;
        final double x = Math.cos(30 * Math.PI / 180) * sideLength * (left ? -1 : 1);
        final double sin30 = Math.sin(30 * Math.PI / 180);
        Path ear = new Path();
        MoveTo m1 = new MoveTo(0, 0);
        LineTo l1 = new LineTo(x, sin30 * sideLength);
        LineTo l2 = new LineTo(x, - sin30 * sideLength);
        ear.getElements().addAll(m1, l1, l2, new ClosePath());
        ear.setFill(Color.BLANCHEDALMOND);
        
        double angle = 30.0 * (left ? -1 : 1);
        Rotate r = new Rotate(angle, 0, 0);
        ear.getTransforms().add(r);
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(r.angleProperty(), angle)),
                new KeyFrame(Duration.seconds(2.0), new KeyValue(r.angleProperty(), -angle)));
        earMovement.getChildren().add(t);
        
        ear.setTranslateX(axisX);
        ear.setTranslateY(axisY);
        
        return ear;
    }
    
    public static List<Enemy> getEnemies() {
        return enemies;
    }

    public void die() {
        if (enemies.contains(this))
            enemies.remove(this);
        
        RotateTransition rt = new RotateTransition(Duration.seconds(DYING_DURATION), this);
        rt.setFromAngle(0.0);
        rt.setToAngle(3 * 360.0);
        FadeTransition ft = new FadeTransition(Duration.seconds(DYING_DURATION), this);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        dyingAnimation = new ParallelTransition(rt, ft);
        dyingAnimation.play();
    }

    public boolean isDyingAnimationOver() {
        return dyingAnimation != null && dyingAnimation.getStatus() == Status.STOPPED;
    }
    
    public void stopAnimation() {
        eyeBlinking.stop();
        earMovement.stop();
    }

    public static List<EnemyShot> getShots() {
        return shots;
    }

    public static void setShots(List<EnemyShot> s) {
        shots = s;
    }

    private void makeShot() {
        EnemyShot shot = new EnemyShot();
        shot.setTranslateX(getTranslateX());
        shot.setTranslateY(getTranslateY() + 10);
        shots.add(shot);
    }

//    private static void setShotDelay() {
//        shotDelay = (int) (MIN_SHOT_DELAY + Math.random() * (MAX_SHOT_DELAY - MIN_SHOT_DELAY));
//    }
    
    private static void resetMoveCycles() {
        moveCycleCounter = 0;
        moveCycles = ((int) (MIN_MOVE_CYCLES + Math.random() * (MAX_MOVE_CYCLES - MIN_MOVE_CYCLES))) / 60 * 60;
    }
    
    @Override
    public void update() {
        if (Math.random() > 0.999) {
            makeShot();
        }
//        if (++shotDelayCounter == shotDelay) {
//            makeShot();
//            shotDelayCounter = 0;
//            setShotDelay();
//        }

        if (++moveCycleCounter >= moveCycles)
            lastEnemyInListToSetVelocity = true;
        
        if (getTranslateX() + velocity < getBoundsInParent().getWidth() / 2 + 20) {
            lastEnemyInListToSetVelocity = true;
        } else if (getTranslateX() + velocity > Main.WINDOW_WIDTH - getBoundsInParent().getWidth() / 2 - 20) {
            lastEnemyInListToSetVelocity = true;
        } else {
            setTranslateX(getTranslateX() + velocity);
        }
        
        if (lastEnemyInListToSetVelocity == true && enemies.indexOf(this) == enemies.size() - 1) {
            resetMoveCycles();
            velocity = -velocity;
            lastEnemyInListToSetVelocity = false;
        }
    }

}
