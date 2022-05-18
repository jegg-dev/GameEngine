package com.jegg.spacesim.core;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.jegg.spacesim.core.ecs.*;
import com.jegg.spacesim.core.ecs.Transform;
import com.jegg.spacesim.game.*;

public class Game extends ApplicationAdapter {
	private static Engine engine;
	private static Stage uiStage;
	private Label fpsCounter;
	private Label contactsCounter;
	private static GameCamera gameCamera;
	private static ShapeRenderer shapeRenderer;
	public static boolean debugging = false;
	public static Array<DebugLine> lines = new Array<>();

	@Override
	public void create () {
		Input input = new Input();
		Input.Instance = input;

		Skin skin = new Skin(Gdx.files.internal("skins/flat/skin.json"));
		uiStage = new Stage(new ScreenViewport());
		Table table = new Table();
		table.setFillParent(true);
		uiStage.addActor(table);

		fpsCounter = new Label("FPS", skin);
		Image background = new Image(new Texture(new Pixmap(0,0, Pixmap.Format.Alpha)));
		fpsCounter.getStyle().background = background.getDrawable();
		fpsCounter.getStyle().font.getData().setScale(0.5f, 0.5f);
		table.top().add(fpsCounter).padLeft(5.0f).padTop(5.0f).width(25).height(15).expandX().left();

		TextButton tb = new TextButton("Exit", skin);
		tb.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent e, float x, float y){
				Gdx.app.exit();
			}
		});
		table.add(tb).width(25).height(25).right();

		table.row();

		contactsCounter = new Label("Contacts", skin);
		contactsCounter.getStyle().background = background.getDrawable();
		contactsCounter.getStyle().font.getData().setScale(0.5f, 0.5f);
		table.add(contactsCounter).padLeft(5.0f).padBottom(5.0f).width(25).height(15).bottom().left();

		table.setDebug(true);

		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(input);
		im.addProcessor(uiStage);
		Gdx.input.setInputProcessor(im);

		Gdx.graphics.setVSync(Settings.UseVsync);

		gameCamera = new GameCamera(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		GameCamera.Main = gameCamera;
		SpriteBatch batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(gameCamera.getCombined());
		batch.setProjectionMatrix(gameCamera.getCombined());
		RenderSystem renderSystem = new RenderSystem(batch, gameCamera.orthoCam);

		Physics.world = new World(new Vector2(0,0),true);
		ContactSystem contactSystem = new ContactSystem();
		Physics.world.setContactListener(new RigidbodyContactListener(contactSystem));
		Physics.world.setContinuousPhysics(false);

		engine = new PooledEngine();
		engine.addSystem(new PhysicsSystem(Physics.world, contactSystem));
		engine.addSystem(new IteratingEntitySystem());
		engine.addSystem(renderSystem);
		engine.addSystem(new TilemapRenderSystem(shapeRenderer));
		engine.addSystem(new ShapeRenderSystem(shapeRenderer));
		engine.addSystem(new ParticleSystemRenderer(shapeRenderer));
		engine.addSystem(new PhysicsDebugSystem(Physics.world, gameCamera));
		engine.getSystem(PhysicsDebugSystem.class).setProcessing(false);
		engine.addSystem(new GarbageSystem(engine));

		//new Station();
		//SpaceGenerator gen = new SpaceGenerator();
		//gen.generate();

		//new TerrainController();
		//new PerlinTest();
		Ship ship = new Ship();
		/*for(int i = 0; i < 1; i++){
			new AIShip(ship);
		}*/
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0,0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(Input.getKeyUp(Input.Escape)){
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
			vel.scl(2);
			gameCamera.getPosition().add(x,y,0);
			gameCamera.setZoom(gameCamera.getZoom() + (Input.Scroll * 0.1f));
		}

		shapeRenderer.setProjectionMatrix(gameCamera.getCombined());
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		engine.update(Gdx.graphics.getDeltaTime());

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

		fpsCounter.setText(Gdx.graphics.getFramesPerSecond());
		contactsCounter.setText(PhysicsSystem.ContactsSize());

		uiStage.getViewport().setScreenWidth(Gdx.graphics.getWidth());
		uiStage.getViewport().setScreenHeight(Gdx.graphics.getHeight());
		uiStage.act();
		uiStage.draw();

		//Gdx.gl.glEnable(GL20.GL_BLEND);
		//Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		Input.Instance.update();
	}

	@Override
	public void resize(int width, int height){
		gameCamera.orthoCam.viewportWidth = width / RenderSystem.PIXELS_PER_METER;
		gameCamera.orthoCam.viewportHeight = height / RenderSystem.PIXELS_PER_METER;
	}

	@Override
	public void dispose(){
		Physics.world.dispose();
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
		Body body = Physics.world.createBody(def);
		PolygonShape shape = new PolygonShape();
		shape.set(verts);
		body.createFixture(shape, density);
		rb.body = body;
		return rb;
	}

	public static Rigidbody CreateRigidbody(BodyDef def, Shape shape, float density){
		Rigidbody rb = CreateComponent(Rigidbody.class);
		Body body = Physics.world.createBody(def);
		body.createFixture(shape, density);
		rb.body = body;
		return rb;
	}

	public static void DestroyEntity(Entity entity){
		entity.add(CreateComponent(DestroyedFlag.class));
	}

	public static void DestroyComponent(Entity entity, Class<? extends Component> component){
		entity.remove(component);
	}

	public static void SetUIStage(Stage stage){
		uiStage = stage;
		InputMultiplexer im = new InputMultiplexer();
		im.addProcessor(stage);
		im.addProcessor(Input.Instance);
		Gdx.input.setInputProcessor(im);
	}
}
