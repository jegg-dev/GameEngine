package com.jegg.spacesim.core;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jegg.spacesim.core.ecs.*;
import com.jegg.spacesim.game.*;

import java.util.List;

public class Game extends ApplicationAdapter {

	private static Engine engine;
	private Input input;
	private static CollisionSystem collisionSystem;
	private static OrthographicCamera gameCamera;
	private static OrthographicCamera uiCamera;
	private static SpriteBatch batch;
	private static BitmapFont font;
	private float radius = 1;
	public static boolean debugging = false;
	public static Array<DebugLine> lines = new Array<>();

	@Override
	public void create () {
		input = new Input();
		Gdx.input.setInputProcessor(input);

		batch = new SpriteBatch();
		RenderSystem renderSystem = new RenderSystem(batch);
		gameCamera = RenderSystem.getCamera();
		batch.setProjectionMatrix(gameCamera.combined);

		uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.position.set(uiCamera.viewportWidth / 2.0f, uiCamera.viewportHeight / 2.0f, 1.0f);
		//uiBatch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("default.fnt"));

		Gdx.graphics.setWindowedMode(800, 600);
		Gdx.graphics.setVSync(false);
		//Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

		Physics.world = new World(new Vector2(0,0),true);
		collisionSystem = new CollisionSystem();
		Physics.world.setContactListener(new RigidbodyContactListener(collisionSystem));

		engine = new PooledEngine();
		engine.addSystem(new PhysicsSystem(Physics.world));
		engine.addSystem(new IteratingEntitySystem());
		engine.addSystem(renderSystem);
		engine.addSystem(new TilemapRenderSystem(new ShapeRenderer()));
		engine.addSystem(new ShapeRenderSystem(new ShapeRenderer()));
		engine.addSystem(new ParticleSystemRenderer(new ShapeRenderer()));
		engine.addSystem(new PhysicsDebugSystem(Physics.world, gameCamera));
		engine.getSystem(PhysicsDebugSystem.class).setProcessing(false);

		new Station();
		SpaceGenerator gen = new SpaceGenerator();
		gen.generate();

		//new TerrainController();
		//new PerlinTest();
		Ship ship = new Ship();
		/*for(int i = 0; i < 1; i++){
			new AIShip(ship);
		}*/
		//new DodgeEnemy();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0,0,0,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gameCamera.update();

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

		if(debugging){
			float x = Input.getKey(Input.D) ? 1 : Input.getKey(Input.A) ? -1 : 0;
			float y = Input.getKey(Input.W) ? 1 : Input.getKey(Input.S) ? -1 : 0;
			Vector3 vel = new Vector3(x,y,0);
			vel.nor();
			vel.scl(2);
			gameCamera.position.add(x,y,0);
			RenderSystem.getCamera().zoom += Input.scroll * 0.1f;
		}

		engine.update(Gdx.graphics.getDeltaTime());
		collisionSystem.update();

		ShapeRenderer shape = new ShapeRenderer();
		shape.setProjectionMatrix(gameCamera.combined);
		shape.begin(ShapeRenderer.ShapeType.Line);
		Array<DebugLine> toRemove = new Array<>();
		for(int i = 0; i < lines.size; i++){
			shape.setColor(lines.get(i).color);
			shape.line(lines.get(i).start.x, lines.get(i).start.y, lines.get(i).end.x, lines.get(i).end.y);
			lines.get(i).timer -= Gdx.graphics.getDeltaTime();
			if(lines.get(i).timer <= 0){
				toRemove.add(lines.get(i));
			}
		}
		lines.removeAll(toRemove, true);
		shape.end();

		uiCamera.update();

		/*ShapeRenderer shape = new ShapeRenderer();
		shape.setProjectionMatrix(uiCamera.combined);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(Color.GRAY);
		shape.rect(0,uiCamera.viewportHeight - font.getLineHeight(), 100, font.getLineHeight());
		shape.end();*/

		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();

		font.draw(batch, "" + Gdx.graphics.getFramesPerSecond(), 0, uiCamera.viewportHeight - 5);
		batch.end();
		//Gdx.gl.glEnable(GL20.GL_BLEND);
		//Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		/*ShapeRenderer rend = new ShapeRenderer();
		rend.setProjectionMatrix(RenderSystem.getCamera().combined);
		rend.begin(ShapeRenderer.ShapeType.Filled);
		rend.setColor(1, 0, 0,0.5f);
		radius += Gdx.graphics.getDeltaTime();
		rend.circle(0,0,radius,100);
		rend.end();*/
		input.update();
	}

	@Override
	public void resize(int width, int height){
		gameCamera.viewportWidth = width / RenderSystem.PIXELS_PER_METER;
		gameCamera.viewportHeight = height / RenderSystem.PIXELS_PER_METER;
		uiCamera.viewportWidth = width;
		uiCamera.viewportHeight = height;
		uiCamera.position.set(uiCamera.viewportWidth / 2.0f, uiCamera.viewportHeight / 2.0f, 1.0f);
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

	public static void DestroyEntity(Entity entity){
		if(ComponentMappers.rigidbody.get(entity) != null){
			Physics.world.destroyBody(ComponentMappers.rigidbody.get(entity).body);
		}
		engine.removeEntity(entity);
	}

	public static void DestroyComponent(Entity entity, Class<? extends Component> component){
		entity.remove(component);
	}

	public static Vector3 ScreenToWorld(Vector2 screenPos){
		/*return new Vector3((screenPos.x - RenderSystem.getCamera().position.x) * RenderSystem.METERS_PER_PIXEL,
				(RenderSystem.getCamera().position.y - screenPos.y) * RenderSystem.METERS_PER_PIXEL, 0);*.
		 */
		return RenderSystem.getCamera().unproject(new Vector3(screenPos.x, screenPos.y, 0));
	}

	public static Camera getUICamera(){
		return uiCamera;
	}

	public static void WriteUI(String text, float x, float y){
		batch.setProjectionMatrix(Game.getUICamera().combined);
		batch.begin();
		font.draw(batch, text, x, y);
		batch.end();
	}
}
