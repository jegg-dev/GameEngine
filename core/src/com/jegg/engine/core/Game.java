package com.jegg.engine.core;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jegg.engine.core.ecs.*;
import com.jegg.engine.core.ecs.Transform;
import com.jegg.engine.core.particles.ParticleSystemRenderer;
import com.jegg.engine.core.physics.*;
import com.jegg.engine.core.rendering.ShapeRenderSystem;
import com.jegg.engine.core.rendering.PolygonSpriteRenderSystem;
import com.jegg.engine.core.rendering.SpriteRenderSystem;
import com.jegg.engine.core.tilemap.TilemapRenderSystem;
import com.jegg.engine.game.*;

import java.awt.*;
import java.awt.event.ActionEvent;

public class Game extends ApplicationAdapter {
	private static StartupAction Startup;
	private static Engine engine;
	private static Stage uiStage;
	private static GameCamera gameCamera;
	private static ShapeRenderer shapeRenderer;
	private static SpriteBatch batch;
	private static PolygonSpriteBatch polyBatch;
	public static boolean debugging = false;
	private final static Array<IIteratedBehavior> scripts = new Array<>();
	public static Array<DebugLine> lines = new Array<>();

	public Game(StartupAction startup){
		super();
		Startup = startup;
	}

	@Override
	public void create () {
		Input.Instance = new Input();

		Gdx.graphics.setVSync(Settings.UseVsync);

		AssetDatabase.Load();

		gameCamera = new GameCamera(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		GameCamera.Main = gameCamera;
		batch = new SpriteBatch();
		polyBatch = new PolygonSpriteBatch();
		shapeRenderer = new ShapeRenderer();

		Physics.CreateWorld();
		ContactSystem contactSystem = new ContactSystem();
		Physics.GetWorld().setContactListener(new RigidbodyContactListener(contactSystem));
		Physics.GetWorld().setContinuousPhysics(false);

		engine = new PooledEngine();
		engine.addSystem(new PhysicsSystem(Physics.GetWorld(), contactSystem));
		engine.addSystem(new IteratingEntitySystem());
		engine.addSystem(new SpriteRenderSystem(batch, gameCamera.orthoCam));
		engine.addSystem(new PolygonSpriteRenderSystem(batch, polyBatch, gameCamera.orthoCam));
		engine.addSystem(new TilemapRenderSystem(batch));
		engine.addSystem(new ShapeRenderSystem(shapeRenderer, batch));
		engine.addSystem(new ParticleSystemRenderer(batch));
		engine.addSystem(new PhysicsDebugSystem(Physics.GetWorld(), gameCamera));
		engine.getSystem(PhysicsDebugSystem.class).setProcessing(false);
		engine.addSystem(new GarbageSystem(engine));

		Startup.invoke(this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0,0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(Input.getKey(Input.LeftControl) && Input.getKeyDown(Input.E)){
			if(!debugging){
				debugging = true;
				engine.getSystem(IteratingEntitySystem.class).setProcessing(false);
				engine.getSystem(PhysicsSystem.class).setProcessing(false);
			}
			else{
				debugging = false;
				engine.getSystem(IteratingEntitySystem.class).setProcessing(true);
				engine.getSystem(PhysicsSystem.class).setProcessing(true);
			}
		}
		if(Input.getKeyUp(Input.Tab)){
			engine.getSystem(PhysicsDebugSystem.class).setProcessing(
					!engine.getSystem(PhysicsDebugSystem.class).checkProcessing());
		}

		if(Input.getKey(Input.LeftControl) && Input.getKey(Input.LeftAlt) && Input.getKeyDown(Input.V)){
			Settings.UseVsync = !Settings.UseVsync;
			Gdx.graphics.setVSync(Settings.UseVsync);
		}

		if(debugging){
			float x = Input.getKey(Input.D) ? 1 : Input.getKey(Input.A) ? -1 : 0;
			float y = Input.getKey(Input.W) ? 1 : Input.getKey(Input.S) ? -1 : 0;
			Vector3 vel = new Vector3(x,y,0);
			vel.nor();
			vel.scl(100f * Gdx.graphics.getDeltaTime());
			gameCamera.getPosition().add(vel.x,vel.y,0);
			gameCamera.setZoom(gameCamera.getZoom() + (Input.Scroll * 0.1f));

			/*Vector3 mousePos = gameCamera.screenToWorld(Input.MousePos);
			RaycastHit hit = Physics.Raycast(new Vector2(mousePos.x, mousePos.y), new Vector2(0, 0), 1);
			if(hit != null && hit.entity != null){

			}*/
		}

		for(IIteratedBehavior s : scripts){
			s.update(Gdx.graphics.getDeltaTime());
		}

		batch.setProjectionMatrix(gameCamera.getCombined());
		batch.enableBlending();
		batch.begin();

		engine.update(Gdx.graphics.getDeltaTime());

		batch.end();

		shapeRenderer.setProjectionMatrix(gameCamera.getCombined());
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

		Array<DebugLine> toRemove = new Array<>();
		for(int i = 0; i < lines.size; i++){
			shapeRenderer.setColor(lines.get(i).color);
			shapeRenderer.line(lines.get(i).start.x, lines.get(i).start.y, lines.get(i).end.x, lines.get(i).end.y);
			lines.get(i).timer -= Gdx.graphics.getDeltaTime();
			if(lines.get(i).timer <= 0){
				toRemove.add(lines.get(i));
			}
		}
		lines.removeAll(toRemove, true);

		shapeRenderer.end();

		if(uiStage != null) {
			uiStage.act(Gdx.graphics.getDeltaTime());
			uiStage.draw();
		}

		//Gdx.gl.glEnable(GL20.GL_BLEND);
		//Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		Input.Instance.update();
	}

	@Override
	public void resize(int width, int height){
		float aspectRatio = (float)width / (float)height;
		float scaledWidth = 1920;
		float scaledHeight = 1920 * (1 / aspectRatio);

		gameCamera.orthoCam.viewportWidth = scaledWidth / PolygonSpriteRenderSystem.PIXELS_PER_METER;
		gameCamera.orthoCam.viewportHeight = scaledHeight / PolygonSpriteRenderSystem.PIXELS_PER_METER;

		uiStage.getViewport().setScreenWidth(width);
		uiStage.getViewport().setScreenHeight(height);

		uiStage.getViewport().setWorldWidth(scaledWidth);
		uiStage.getViewport().setWorldHeight(scaledHeight);
		uiStage.getViewport().apply(true);
	}

	@Override
	public void dispose(){
		Physics.GetWorld().dispose();
	}

	public static void AddScript(IIteratedBehavior script){
		scripts.add(script);
	}

	public static void RemoveScript(IIteratedBehavior script){
		scripts.removeValue(script, true);
	}

	public static Entity CreateEntity() {
		Entity entity = engine.createEntity();
		engine.addEntity(entity);
		return entity;
	}

	@SuppressWarnings("deprecation")
	public static <T extends Entity> T CreateEntity(Class<T> entityType){
		try {
			return entityType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Entity CreateWorldEntity(Vector3 position, float rotation){
		Entity entity = engine.createEntity();
		Transform t = engine.createComponent(Transform.class);
		t.setPosition(position);
		t.setRotation(rotation);
		entity.add(t);
		engine.addEntity(entity);
		return entity;
	}

	public static void AddEntity(Entity entity){
		engine.addEntity(entity);
	}

	public static <T extends Component> T CreateComponent(Class<T> componentType){
		return engine.createComponent(componentType);
	}

	public static Rigidbody CreateRigidbody(float[] verts, BodyDef.BodyType bodyType, float density){
		Rigidbody rb = CreateComponent(Rigidbody.class);
		BodyDef def = new BodyDef();
		def.type = bodyType;
		Body body = Physics.GetWorld().createBody(def);
		PolygonShape shape = new PolygonShape();
		shape.set(verts);
		body.createFixture(shape, density);
		Filter filter = new Filter();
		filter.categoryBits = Physics.CATEGORY_NORMAL;
		filter.maskBits = Physics.MASK_NORMAL;
		body.getFixtureList().get(0).setFilterData(filter);
		rb.body = body;
		return rb;
	}

	public static Rigidbody CreateRigidbody(BodyDef def, Shape shape, float density){
		Rigidbody rb = CreateComponent(Rigidbody.class);
		Body body = Physics.GetWorld().createBody(def);
		body.createFixture(shape, density);
		Filter filter = new Filter();
		filter.categoryBits = Physics.CATEGORY_NORMAL;
		filter.maskBits = Physics.MASK_NORMAL;
		body.getFixtureList().get(0).setFilterData(filter);
		rb.body = body;
		return rb;
	}

	public static void DestroyEntity(Entity entity){
		entity.add(CreateComponent(DestroyedFlag.class));
	}

	public static void DestroyComponent(Entity entity, Class<? extends Component> component){
		entity.remove(component);
	}

	public static Stage GetUIStage(){
		return uiStage;
	}

	public static void SetUIStage(Stage stage){
		uiStage = stage;
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(uiStage);
		im.addProcessor(Input.Instance);
		Gdx.input.setInputProcessor(im);
	}
}
