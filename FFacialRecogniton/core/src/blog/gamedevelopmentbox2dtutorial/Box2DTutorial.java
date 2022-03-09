package blog.gamedevelopmentbox2dtutorial;

import blog.gamedevelopmentbox2dtutorial.*;
import com.badlogic.gdx.Game;

/**
 * @author Isabel
 */
public class Box2DTutorial extends Game {

	//screen options
	private LoadingScreen loadingScreen;
	private chatScreen chatScreen;
	private loginScreen loginScreen;
	private RecognitionScreen recScreen;

	public final static int LOGIN = 0;
	public final static int CHAT = 1;
	public final static int REC = 2;

	public void changeScreen(int screen) throws InterruptedException {
		switch(screen){
			case LOGIN:
				if (loginScreen == null) {
					loginScreen = new loginScreen(this); // added (this)
				}
				this.setScreen(loginScreen);
				break;
			case CHAT:
				if (chatScreen == null) {
					chatScreen = new chatScreen(this); // added (this)
				}
				this.setScreen(chatScreen);
				break;
			case REC:
				if (recScreen == null) {
					recScreen = new RecognitionScreen(this); // added (this)
				}
				this.setScreen(recScreen);
				break;
		}
	}

	public void create () {
		try {
			loadingScreen = new LoadingScreen(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setScreen(loadingScreen);
	}
}