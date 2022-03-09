package blog.gamedevelopmentbox2dtutorial.desktop;

import blog.gamedevelopmentbox2dtutorial.Box2DTutorial;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
/**
 * @author Isabel
 */
public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new Box2DTutorial(), config);
    }
}