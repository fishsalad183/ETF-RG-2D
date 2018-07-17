package cameras;

import javafx.scene.Group;
//import javafx.scene.Scene;
import javafx.scene.transform.Translate;
import main.Main;
import sprites.Player;

public class Camera extends Group {
    
    private Translate translate;
//    private Scene scene;
    
    public Camera() {
        translate = new Translate();
        getTransforms().add(translate);
    }
    
//    public void linkScene(Scene s) {
//        scene = s;
//    }
    
    public void fixCameraAt(Player player) {
        if (player != null) {
//            translate.xProperty().bind(player.translateXProperty().subtract(scene.widthProperty().divide(2)).negate());
            translate.xProperty().bind(player.translateXProperty().subtract(Main.WINDOW_WIDTH / 2).negate());
        } else {
            getTransforms().clear();
            translate = new Translate();
            getTransforms().add(translate);
        }
    }
}
