package blog.gamedevelopmentbox2dtutorial;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.opencv.core.Core;

/**
 * @author Isabel
 */
public class LoadingScreen implements Screen {

    private final Box2DTutorial parent;
    private final Stage stage;
    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
    FacialRecognition fr = new FacialRecognition();
    boolean face = fr.run();
    public LoadingScreen(Box2DTutorial box2dTutorial) throws InterruptedException {
        parent = box2dTutorial;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        // Load the native OpenCV library


    }


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        try {
            if (face){
                parent.changeScreen(Box2DTutorial.LOGIN);
            }
            else
                parent.changeScreen(Box2DTutorial.CHAT);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();

        }
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
