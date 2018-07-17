package main;

import cameras.Camera;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.geometry.VPos;
import javafx.scene.Group; 
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import sprites.Background;
import sprites.Coin;
import sprites.Enemy;
import sprites.EnemyShot;
import sprites.Player;
import sprites.Shot;

public class Main extends Application {

    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;

    public static final int ENEMIES_IN_A_ROW = 6;
    public static final int ENEMIES_IN_A_COLUMN = 3;

    private Background background;
    private Player player;
    private List<Enemy> enemies;
    private List<Enemy> dyingEnemies;
    private List<EnemyShot> enemyShots;
    private List<Shot> shots;
    private List<Coin> coins;

    private Camera camera;

    private Group root;
    private Scene scene;
    private double time = 0;
    private Text timeText;
    private int points = 0;
    private Text pointsText;
    private boolean theEnd = false;

    @Override
    public void start(Stage primaryStage) {
        enemies = Enemy.getEnemies();
        enemyShots = Enemy.getShots();
        dyingEnemies = new LinkedList<>();
        coins = new LinkedList<>();
        root = new Group();
        camera = new Camera();

        background = new Background(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(background);

        player = new Player();
        player.linkCamera(camera);
        player.setTranslateX(WINDOW_WIDTH / 2);
        player.setTranslateY(WINDOW_HEIGHT * 0.95);
        camera.getChildren().add(player);

        for (int i = 0; i < ENEMIES_IN_A_COLUMN; i++) {
            for (int j = 0; j < ENEMIES_IN_A_ROW; j++) {
                Enemy enemy = new Enemy();
                enemy.setTranslateX((j + 1) * WINDOW_WIDTH / (ENEMIES_IN_A_ROW + 1));
                enemy.setTranslateY((i + 1) * 100);
                enemy.animateEye((i + j) % 2 == 0);
                camera.getChildren().add(enemy);
                enemies.add(enemy);
            }
        }
        
        root.getChildren().add(camera);
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(player);
        scene.setOnKeyReleased(player);
//        camera.linkScene(scene);
        
        pointsText = new Text();
        pointsText.setFont(Font.font("Tahoma", 25));
        pointsText.setTextOrigin(VPos.TOP);
        pointsText.layoutXProperty().bind(scene.widthProperty().subtract(pointsText.layoutBoundsProperty().get().getWidth()).divide(1.15));
        pointsText.layoutYProperty().bind(scene.heightProperty().multiply(0).add(pointsText.layoutBoundsProperty().get().getHeight()));
        pointsText.setFill(Color.BEIGE);
        root.getChildren().add(pointsText);
        
        timeText = new Text();
        timeText.setFont(Font.font("Tahoma", 15));
        timeText.setTextOrigin(VPos.TOP);
        timeText.setTextAlignment(TextAlignment.CENTER);
        timeText.layoutXProperty().bind(scene.widthProperty().subtract(timeText.layoutBoundsProperty().get().getWidth()).divide(2));
        timeText.layoutYProperty().bind(scene.heightProperty().multiply(0).add(timeText.layoutBoundsProperty().get().getHeight()));
        timeText.setFill(Color.RED);
        root.getChildren().add(timeText);

        primaryStage.setTitle("Svemirci");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                update();
            }
        }.start();
    }

    public void update() {
        if (theEnd == false) {
            shots = player.getShots();

            Iterator<Enemy> itEnemies = enemies.iterator();
            while (itEnemies.hasNext()) {
                Enemy currentEnemy = itEnemies.next();

                if (player.getBoundsInParent().intersects(currentEnemy.getBoundsInParent())) {
                    theEnd = true;
                    gameOver(false);
                    return;
                }

                Iterator<Shot> itShots = shots.iterator();
                while (itShots.hasNext()) {
                    Shot currentShot = itShots.next();

                    if (currentShot.getTranslateY() < 5) {
                        itShots.remove();
                        continue;
                    }

                    if (currentShot.getBoundsInParent().intersects(currentEnemy.getBoundsInParent())) {
                        points++;
                        itShots.remove();
                        itEnemies.remove();
                        setDyingAndReleaseCoin(currentEnemy);
                        
                        break;
                    }
                }
            }
            
            Iterator<Enemy> itDying = dyingEnemies.iterator();
            while (itDying.hasNext()) {
                Enemy dyingEnemy = itDying.next();
                if (dyingEnemy.isDyingAnimationOver())
                    itDying.remove();
            }
            
            Iterator<EnemyShot> itES = enemyShots.iterator();
            while (itES.hasNext()) {
                EnemyShot currentES = itES.next();
                
                if (currentES.getTranslateY() > WINDOW_HEIGHT - 5) {
                    itES.remove();
                    continue;
                }
                
                if (currentES.getBoundsInParent().intersects(player.getBoundsInParent())) {
                    theEnd = true;
                    gameOver(false);
                    return;
                }
            }
            
            Iterator<Coin> itCoin = coins.iterator();
            while (itCoin.hasNext()) {
                Coin currentCoin = itCoin.next();
                
                if (player.getBoundsInParent().intersects(currentCoin.getBoundsInParent())) {
                    points++;
                    itCoin.remove();
                    continue;
                }
                
                double x = currentCoin.getTranslateX();
                double y = currentCoin.getTranslateY();
                if (x < 5 || x > WINDOW_WIDTH - 5 || y > WINDOW_HEIGHT - 5)
                    itCoin.remove();
            }

            camera.getChildren().clear();
            camera.getChildren().add(player);

            if (enemies.isEmpty() && coins.isEmpty()) {
                theEnd = true;
                gameOver(true);
                return;
            } else {
                camera.getChildren().addAll(shots);
                shots.forEach(e -> e.update());
                camera.getChildren().addAll(enemyShots);
                enemyShots.forEach(e -> e.update());
                camera.getChildren().addAll(enemies);
                enemies.forEach(e -> e.update());
                camera.getChildren().addAll(dyingEnemies);
                camera.getChildren().addAll(coins);
                coins.forEach(e -> e.update());
            }

//            player.setShots(shots);
            player.update();

            background.update();

            pointsText.setText("Points: " + points);
            
            time += 1.0 / 60;
            timeText.setText("Time: " + (int) time);
        }
    }

    private void gameOver(boolean victory) {
        Text text = new Text((victory ? "You win!" : "Game over, you lose!") + "\nPoints: " + points);
        text.setFont(Font.font("Tahoma", 70));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setTextOrigin(VPos.TOP);
        text.layoutXProperty().bind(scene.widthProperty().subtract(text.layoutBoundsProperty().get().getWidth()).divide(2));
        text.layoutYProperty().bind(scene.heightProperty().subtract(text.layoutBoundsProperty().get().getHeight()).divide(2));
        root.getChildren().add(text);
        pointsText.setText("");
        
        player.stopAnimation();
        enemies.forEach(e -> e.stopAnimation());
        shots.forEach(e -> e.stopAnimation());
        enemyShots.forEach(e -> e.stopAnimation());
        
        if (!victory) {
            camera.getChildren().remove(player);
            text.setFill(new Color(1.0, 0.15, 0.15, 1.0));
        } else {
            text.setFill(Color.LIME);
        }
    }
    
    private void setDyingAndReleaseCoin(Enemy dyingEnemy) {
        dyingEnemy.die();
        dyingEnemies.add(dyingEnemy);
        
        Coin coin = new Coin();
        coin.setTranslateX(dyingEnemy.getTranslateX());
        coin.setTranslateY(dyingEnemy.getTranslateY());
        coins.add(coin);
        camera.getChildren().add(coin);
    }
    
    public static void main(String[] args) {
        launch(args);
    }

}
