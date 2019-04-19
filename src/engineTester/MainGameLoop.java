package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TextureModel;
import objectConverter.ModelData;
import objectConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {
	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		TerrainTexture bgTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(bgTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		Terrain terrain = new Terrain(0,0,loader, texturePack, blendMap, "heightmap");
		
		ModelData data = OBJFileLoader.loadOBJ("tree");
		RawModel treeModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TextureModel staticModel = new TextureModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
		
		TextureModel grass = new TextureModel(OBJLoader.loadObjModel("grassModel", loader),
				new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumerOfRows(2);
		TextureModel fern = new TextureModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
		fern.getTexture().setHasTransparency(true);
		
		TextureModel lamp = new TextureModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));
		
		List<Entity> entities = new ArrayList<>();
		Random random = new Random();
		for(int i = 0; i < 400; i++) {
			if(i%10==0) {
				float x = random.nextFloat() * 600;
				float z = random.nextFloat() * 600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(staticModel, new Vector3f(x, y, z), 0, 0, 0, 4));
			}
			if(i%3==0) {
				float x = random.nextFloat() * 600;
				float z = random.nextFloat() * 600;
				float y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1));
				x = random.nextFloat() * 600;
				z = random.nextFloat() * 600;
				y = terrain.getHeightOfTerrain(x, z);
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, 0, 0, 0.6f));
			}
		}

		RawModel playerModel = OBJLoader.loadObjModel("person", loader);
		TextureModel playerTextureModel = new TextureModel(playerModel, new ModelTexture(loader.loadTexture("playerTexture")));
		Player player = new Player(playerTextureModel, new Vector3f(75, 0 , 130), 0, 0, 0, 0.5f);
		entities.add(player);
		
		List<Light> lights = new ArrayList<>();
		lights.add(new Light(new Vector3f(0,1000, -700), new Vector3f(1,1,1)));
		/*
		lights.add(new Light(new Vector3f(-200,100, -200), new Vector3f(10,0,0)));
		lights.add(new Light(new Vector3f(200,100, 200), new Vector3f(0,0,10)));
		*/
		Light light = new Light(new Vector3f(220,22, 180), new Vector3f(4,0,0), new Vector3f(1,0.01f,0.002f));
		lights.add(light);
		//lights.add(new Light(new Vector3f(160,13, 205), new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		//lights.add(new Light(new Vector3f(195,23, 170), new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));
		
		Entity lampEntity = new Entity(lamp, new Vector3f(220, 10, 180), 0, 0, 0, 1);
		entities.add(lampEntity);
		//entities.add(new Entity(lamp, new Vector3f(160, 3, 205), 0, 0, 0, 1));
		//entities.add(new Entity(lamp, new Vector3f(195, 10, 170), 0, 0, 0, 1));
		
		Camera camera = new Camera(player);
		
		MasterRenderer renderer = new MasterRenderer(loader);

		List<GuiTexture> guis = new ArrayList<>();
		//guis.add(new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f)));
		//guis.add(new GuiTexture(loader.loadTexture("thinmatrix"), new Vector2f(0.3f, 0.74f), new Vector2f(0.4f, 0.4f)));
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<>();
		WaterTile water = new WaterTile(115, 120, -3);
		waters.add(water);
		
		while(!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			picker.update();
			//System.out.println(picker.getCurrentRay());
			//Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			/*if(terrainPoint != null) {
				lampEntity.setPosition(terrainPoint);
				light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 15, terrainPoint.z));
			}*/
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.RenderScene(entities, terrain, lights, camera, new Vector4f(0,1,0,-water.getHeight() + 1f));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			fbos.bindRefractionFrameBuffer();
			renderer.RenderScene(entities, terrain, lights, camera, new Vector4f(0,-1,0,water.getHeight()));
			
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			fbos.unbindCurrentFrameBuffer();
			renderer.RenderScene(entities, terrain, lights, camera, new Vector4f(0,1,0,10000));
			
			waterRenderer.render(waters, camera, lights.get(0));
			guiRenderer.render(guis);
			
			DisplayManager.updateDisplay();
		}
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
