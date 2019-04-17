package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

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
		Player player = new Player(playerTextureModel, new Vector3f(200, 0 , 180), 0, 0, 0, 0.5f);
		
		Light light = new Light(new Vector3f(0,500, -15), new Vector3f(1,1,1));
		Camera camera = new Camera(player);
		
		MasterRenderer renderer = new MasterRenderer();

		List<GuiTexture> guis = new ArrayList<>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);
		GuiTexture gui1 = new GuiTexture(loader.loadTexture("thinmatrix"), new Vector2f(0.3f, 0.74f), new Vector2f(0.4f, 0.4f));
		guis.add(gui1);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		while(!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			for(Entity entity : entities) {
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
