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

/**
 * login window
 * @author Isabel
 */
public class loginScreen implements Screen {

    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
    private Box2DTutorial parent;
    private Stage stage;
    private TextButton join_button;
    private TextField name_field;
    private static String username;

    public static String getUsername() {
        return username;
    }

    public loginScreen(Box2DTutorial box2dTutorial){
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

        //create login window
        Window window = new Window("Login", skin);
        window.getTitleLabel().setAlignment(Align.center);

        join_button = new TextButton("Join", skin);
        name_field = new TextField("", skin);

        //add to window
        window.add(new Label("Enter Your Name", skin));
        window.row();
        window.add(name_field);
        window.row();
        window.add(join_button);

        table.add(window);



        join_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                username = name_field.getText();
                try {
                    parent.changeScreen(Box2DTutorial.CHAT);
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
