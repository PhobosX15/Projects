package blog.gamedevelopmentbox2dtutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.List;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import blog.gamedevelopmentbox2dtutorial.FacialRecognition;

/**
 * login window
 * @author Isabel
 */
public class RecognitionScreen implements Screen {

    boolean detect = new FacialRecognition().run();


    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
    private final Box2DTutorial parent;
    private final Stage stage;
    private TextButton join_button;
    private JLabel text;



    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public RecognitionScreen(Box2DTutorial box2dTutorial) throws InterruptedException {
        parent = box2dTutorial;
        // create stage and set it as input processor
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void show() {
        //create table
        final Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // create text field
        Label text = new Label("Please wait until the face recognition is completed", skin);
        // create button
        join_button = new TextButton("login", skin);
        // add to table
        table.add(text);
        table.row();
        table.add(join_button);


        join_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                // go to login screen if a face is recognised
                try {
                    parent.changeScreen(Box2DTutorial.LOGIN);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // change the stage's viewport when the screen size is changed
        stage.getViewport().update(width, height, true);
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
        // dispose of assets when not needed anymore
        stage.dispose();
    }

}
