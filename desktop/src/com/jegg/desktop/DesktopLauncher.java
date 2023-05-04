package com.jegg.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jegg.engine.Game;
import com.jegg.engine.GameCamera;
import com.jegg.game.*;
import com.jegg.game.ui.InventoryMenu;
import com.jegg.game.ui.IteratedLabel;
import com.jegg.game.ui.LabelTextLoader;
import com.jegg.game.world.Station;
import com.jegg.game.world.TerrainController;

public class DesktopLauncher {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Space Sim");
		config.setWindowedMode(800,600);
		config.setWindowSizeLimits(800, 600, 3840, 2160);

		new Lwjgl3Application(new Game(gameInstance -> {
			Skin skin = new Skin(Gdx.files.internal("skins/flat/skin.json"));
			Stage stage = new Stage(new ScreenViewport());
			Table table = new Table();
			table.setFillParent(true);
			stage.addActor(table);

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
			playerShip.tc = tc;

			//ship.getComponent(Transform.class).setPosition(new Vector3(1000, 0, 0));
			//ship.getComponent(Rigidbody.class).body.setTransform(new Vector2(500,0), 0);

			playerShip.homeStation = new Station();
			//new AIShip(playerShip);
			//new LineDrawing();
			//new RigidbodyDraw();

			/*TiledMap map = new TiledMap();
			TiledMapTileLayer layer = new TiledMapTileLayer(100, 100, 1, 1);
			for(int x = 0; x < 100; x++){
				for(int y = 0; y < 100; y++){
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
					cell.setTile(new StaticTiledMapTile(TileDatabase.Get(1).sprite));
					layer.setCell(x, y, cell);
				}
			}
			map.getLayers().add(layer);
			Game.TileRenderer = new OrthogonalTiledMapRenderer(map);*/
			//stage.setDebugAll(true);

			IteratedLabel fpsCounter = new IteratedLabel(skin, () -> String.valueOf(Gdx.graphics.getFramesPerSecond()));
			Image background = new Image(new Texture(new Pixmap(1,1, Pixmap.Format.Alpha)));
			fpsCounter.getStyle().background = background.getDrawable();
			fpsCounter.getStyle().font.getData().setScale(1f, 1f);
			table.add(fpsCounter).padLeft(5.0f).padTop(2.0f).width(25).height(15).top().left().expandX();

			TextButton tb = new TextButton("Full", skin);
			tb.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent e, float x, float y){
					if(!Gdx.graphics.isFullscreen()) {
						Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
					} else{
						Gdx.graphics.setWindowedMode(800, 600);
					}
				}
			});
			table.add(tb).width(50).height(50).padRight(5.0f).padTop(5.0f).top().right();

			table.row();

			IteratedLabel positionLabel = new IteratedLabel(skin, new LabelTextLoader() {
				@Override
				public String GetText() {
					Vector3 pos = GameCamera.GetMain().getPosition();
					return "(" + (int)pos.x + ", " + (int)pos.y + ")";
				}
			});
			table.add(positionLabel).left().top().padTop(5.0f).padLeft(5.0f);

			table.row();

			IteratedLabel biomeLabel = new IteratedLabel(skin, new LabelTextLoader() {
				@Override
				public String GetText() {
					Vector2 tilePos = tc.tilemap.WorldToTilePosition(GameCamera.GetMain().getPosition());
					return "Biome: " + tc.tilemap.SampleBiomeIndex((int)tilePos.x, (int)tilePos.y);
				}
			});
			table.add(biomeLabel).left().top().padLeft(5.0f).padTop(5.0f);

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
			VerticalGroup vertGroup = new VerticalGroup();
			overlayTable.bottom().add(vertGroup);
			//vertGroup.setFillParent(true);
			vertGroup.bottom().addActor(healthLabel);
			vertGroup.padBottom(5.0f);

			HorizontalGroup invGroup = new HorizontalGroup();
			invGroup.space(5.0f);
			InventoryMenu invMenu = new InventoryMenu(invGroup, playerShip.inventory);
			table.add(invMenu);
			invGroup.padBottom(10.0f);
			vertGroup.addActorAt(0, invGroup);

			playerShip.healthBar = healthBar;
			playerShip.healthLabel = healthLabel;

		}), config);
	}
}
