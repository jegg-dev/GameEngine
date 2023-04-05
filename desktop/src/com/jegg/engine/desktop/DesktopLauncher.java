package com.jegg.engine.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jegg.engine.Game;
import com.jegg.engine.StartupAction;
import com.jegg.engine.ecs.InactiveFlag;
import com.jegg.game.*;
import com.jegg.game.ui.FPSCounter;

public class DesktopLauncher {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Space Sim");
		config.setWindowedMode(800,600);
		config.setWindowSizeLimits(800, 600, 3840, 2160);

		new Lwjgl3Application(new Game(new StartupAction() {
			@Override
			public void invoke(Game gameInstance) {
				Skin skin = new Skin(Gdx.files.internal("skins/flat/skin.json"));
				Stage stage = new Stage(new ScreenViewport());
				Table table = new Table();
				table.setFillParent(true);
				stage.addActor(table);

				FPSCounter fpsCounter = new FPSCounter("FPS", skin);
				Image background = new Image(new Texture(new Pixmap(1,1, Pixmap.Format.Alpha)));
				fpsCounter.getStyle().background = background.getDrawable();
				fpsCounter.getStyle().font.getData().setScale(1f, 1f);
				table.top().add(fpsCounter).padLeft(5.0f).padTop(2.0f).width(25).height(15).expandX().left().top();

				TextButton tb = new TextButton("Full", skin);
				tb.addListener(new ClickListener(){
					@Override
					public void clicked(InputEvent e, float x, float y){
						if(!Gdx.graphics.isFullscreen()) {
							Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
						}
						else{
							Gdx.graphics.setWindowedMode(800, 600);
						}
					}
				});
				table.add(tb).width(50).height(50).right();

				table.row().expandY().colspan(2);

				ProgressBar healthBar = new ProgressBar(0, 100, 1, false, skin);
				healthBar.setAnimateDuration(0.5f);
				healthBar.setAnimateInterpolation(Interpolation.fastSlow);
				healthBar.getStyle().background.setMinHeight(25);
				healthBar.getStyle().background.setMinWidth(300);
				healthBar.getStyle().knobBefore.setMinWidth(300);
				healthBar.getStyle().knobBefore.setMinHeight(25);
				table.add(healthBar).width(500).height(25).padBottom(5.0f).center().bottom();

				Label healthLabel = new Label("Health", skin);
				Table overlayTable = new Table();
				stage.addActor(overlayTable);
				overlayTable.setFillParent(true);
				overlayTable.bottom().add(healthLabel).padBottom(5.0f).center();

				//table.setDebug(true);
				Game.SetUIStage(stage);

				TerrainController tc = new TerrainController();
				//new Spacemap(500, 64, 16f);

				/*for(float x = -250.0f; x < 250.0f; x += tc.tilemap.getTileWidth()){
					for(float y = -250.0f; y < 250.0f; y += tc.tilemap.getTileWidth()){
						if((Math.sqrt((x * x) + (y * y)) <= 250.0f)){
							tc.tilemap.setTile(new Vector3(x, y, 0), 1);
						}
					}
				}*/

				PlayerShip playerShip = new PlayerShip();
				playerShip.healthBar = healthBar;
				playerShip.healthLabel = healthLabel;
				playerShip.tc = tc;

				//ship.getComponent(Transform.class).setPosition(new Vector3(1000, 0, 0));
				//ship.getComponent(Rigidbody.class).body.setTransform(new Vector2(500,0), 0);

				new Station();
				//new AIShip(playerShip);
				//new LineDrawing();
				//new RigidbodyDraw();
			}
		}), config);
	}
}
