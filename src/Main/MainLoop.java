package Main;


import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import HUDs.HUDRender;
import HUDs.HUDTexture;
import Materials.Camera;
import Materials.Material;
import Materials.Light;
import Materials.Player;
import models.Textured;
import shoot.Shoot;
import shoot.ShootMaster;
import renderer.DisplayManager;
import renderer.Loader;
import renderer.Master;
import renderer.OBJLoader;
import textures.ModelTexture;

public class MainLoop implements java.awt.event.KeyListener {
	Socket requestSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    ObjectOutputStream out2;
    ObjectInputStream in2;
    String message;
    static String recieved = "";
    ServerSocket providerSocket;
	Socket connection = null;
	boolean attempt=false;
	static boolean reset=false;
	public float currentSpeed;
	public static float xPos2, xPos, yPos, zPos;
	public static float yPos2;
	public static float zPos2;
	static long seed = System.currentTimeMillis();
	static long time1 = System.currentTimeMillis();

	public float dy = 0;
	public static  float currentTurnSpeed = 0;
	static boolean player2InGame=false;
	static boolean move=false;
	static boolean create = false;
	static MainLoop client = new MainLoop();
	static int counter =0;
	public static final float RUN_SPEED = 30;
	public static final float TURN_SPEED = 50;
	
	
	
	public void run2() {
		try {
	        providerSocket = new ServerSocket(2010, 10);
	        connection = providerSocket.accept();
	        System.out.println("Connection received from " + connection.getInetAddress().getHostName());
	        create=true;
	        player2InGame=true;
	        out = new ObjectOutputStream(connection.getOutputStream());
	        out.flush();
	        in = new ObjectInputStream(connection.getInputStream());
	        sendMessage("Connection successful");
	        //sends the seed it makes all the entities by
	        sendMessage("seed" + seed);
	        do {
	            try {
	                message = (String) in.readObject();
	                recieved=message;
	                
	                //these if statements check for specific inputs like coordinate positions and turning
	                if(recieved.contains("xpos")){
	    				xPos2=Float.parseFloat(recieved.substring(4));
	    			}
	    			if(recieved.contains("ypos")){
	    				yPos2=Float.parseFloat(recieved.substring(4));
	    			}
	    			if(recieved.contains("zpos")){
	    				zPos2=Float.parseFloat(recieved.substring(4));
	    				move=true;
	    			}
	    			
	    			
	                System.out.println("client>" + message);
	                if (message.equals("bye")) {
	                    sendMessage("bye");
	                }
	                if (recieved.equals("j")) {
						currentTurnSpeed = -TURN_SPEED;
						move=true;
					} else if (recieved.equals("g")) {
						currentTurnSpeed = TURN_SPEED;
						move=true;
					}
	            } catch (ClassNotFoundException classnot) {
	                System.err.println("Data received in unknown format");
	            }
	        } while (!message.equals("bye"));
	    } catch (IOException ioException) {
	        //ioException.printStackTrace();
	    } finally {
	        try {
	            in.close();
	            out.close();
	            connection.close(); 
	            providerSocket.close();
	        } catch (IOException ioException) {
	            //ioException.printStackTrace();
	        }
	    }
	}
	    
	void Run() {
		try {
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
	        requestSocket = new Socket("10.20.220.127", 2010); //192.168.1.117
	        System.out.println("Connected to localhost in port 2010");
	        attempt=true;
	        create=true;
	        
	        player2InGame=true;
	        
	        out = new ObjectOutputStream(requestSocket.getOutputStream());
	        out.flush();
	        in = new ObjectInputStream(requestSocket.getInputStream());
	        sendMessage("Hi my server");
	        do {
	            try {
	                message = (String) in.readObject();
	                recieved=message;
	                if(recieved.contains("xpos")){
	    				xPos2=Float.parseFloat(recieved.substring(4));
	    			}
	    			if(recieved.contains("ypos")){
	    				yPos2=Float.parseFloat(recieved.substring(4));
	    			}
	    			if(recieved.contains("zpos")){
	    				zPos2=Float.parseFloat(recieved.substring(4));
	    				move=true;
	    			}
	    			if(recieved.contains("seed")){
	    				seed=Long.parseLong(recieved.substring(4));
	    				reset=true;
	    			}
	    			if (recieved.equals("j")) {
						currentTurnSpeed = -TURN_SPEED;
						move=true;
					} else if (recieved.equals("g")) {
						currentTurnSpeed = TURN_SPEED;
						move=true;
					}
	                System.out.println("Friend>" + message);
	            } catch (ClassNotFoundException classNot) {
	                System.err.println("data received in unknown format");
	            }
	        } while (!message.equals("bye"));
	    } catch (UnknownHostException unknownHost) {
	        System.err.println("You are trying to connect to an unknown host!");
	    } catch (IOException ioException) {
	        //ioException.printStackTrace();
	    } finally {
	    	/*
	    	 * If the initial attempt to connect was unsuccessful, then it stops trying to be a client
	    	 * and starts up the server process run2
	    	 */
	    	if(!attempt){
	    		System.out.println("Waiting for connection...");
				while (true)
					run2();
			    }  
	        try {
	        	attempt=false;
	            in.close();
	            out.close();
	            requestSocket.close();
	            
	        } catch (IOException ioException) {
	            //ioException.printStackTrace();
	        }
	        
	        
	    }

		
	}
	    
	    
	    
	    public void sendMessage(String msg) {
	        try {
	            out.writeObject(msg);
	            out.flush();
	            System.out.println("You>" + msg);
	        } catch (IOException ioException) {
	            ioException.printStackTrace();
	        }
	    }
	    

	    
	    //check inputs only for turning, everything else is done by sending position coordinates
	    //if you hold down the turn key, i dont think the networked computer gets all of the inputs
	    private static void checkInputs() {
			/*if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				client.sendMessage("y");
			} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				client.sendMessage("h");
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				client.sendMessage("w");
			} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				client.sendMessage("s");
			} */
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				client.sendMessage("j");
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				client.sendMessage("g");
			}
		}
	
	
	
	

	public static void main(String[] args) {

		float shoot = 0;
		float dxx;
		float dzz;
		float sx=-0.9f;
		float sy= 0.87f;
		DisplayManager.creatDisplay();
		
		Loader loader = new Loader();

		
		Textured staticModel = new Textured(OBJLoader.loadObjModel("ship1", loader), new ModelTexture(loader.loadTexture("spaceship")));
		Textured jup = new Textured(OBJLoader.loadObjModel("jup", loader), new ModelTexture(loader.loadTexture("jup")));
		Textured nexus = new Textured(OBJLoader.loadObjModel("asterobj", loader), new ModelTexture(loader.loadTexture("nexus")));
		Textured moon = new Textured(OBJLoader.loadObjModel("asterobj2", loader), new ModelTexture(loader.loadTexture("moon")));
		Textured Earth = new Textured(OBJLoader.loadObjModel("Earth", loader), new ModelTexture(loader.loadTexture("earth")));
		Textured asteroids = new Textured(OBJLoader.loadObjModel("asteroid", loader), new ModelTexture(loader.loadTexture("asteroid")));
		Textured asteroids3 = new Textured(OBJLoader.loadObjModel("asteroid3", loader), new ModelTexture(loader.loadTexture("jup")));
		Textured star = new Textured(OBJLoader.loadObjModel("star", loader), new ModelTexture(loader.loadTexture("nexus")));

		
		Player player2 = new Player(staticModel, new Vector3f(0,-100000f,-15),0,0,0,1);

		
		
		Player player = new Player(staticModel, new Vector3f(0,-3f,-15),0,0,0,1);
		Material entity2 = new Material(Earth, new Vector3f(-20,-2f,-10),0,0,0,1);

		Thread one = new Thread(){
			public void run(){
				client.Run();
			}
		};
		one.start();
		
		
		
		Light light = new Light(new Vector3f(3000,0,0),new Vector3f(1,1,1));
		
		List<HUDTexture> guis = new ArrayList<HUDTexture>();
		
		//GuiTexture health = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.74f, 0.925f), new Vector2f(0.25f, 0.25f));

		//guis.add(health);
		
		HUDRender guiRenderer = new HUDRender(loader);
		
		List<Material> entities1 = new ArrayList<Material>();
		List<Material> entities2 = new ArrayList<Material>();
		List<Material> entities3 = new ArrayList<Material>();
		List<Material> allAster = new ArrayList<Material>();
		List<Material> allAster2 = new ArrayList<Material>();
		List<Material> allAster3 = new ArrayList<Material>();
		List<Material> allstar = new ArrayList<Material>();

		
		

		
		
		
		
		Random random = new Random(seed);

		for (int i = 0; i<5; i++){
			float x = random.nextFloat() * 1000 -500;
			float y = random.nextFloat() * 500 - 250;
			float z = random.nextFloat()* -2000 ;
			entities1.add(new Material(jup, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
		}
		for (int i = 0; i<15; i++){
			float x = random.nextFloat() * 1000 -500;
			float y = random.nextFloat() * 500 - 250;
			float z = random.nextFloat()* -3000 ;
			entities2.add(new Material(nexus, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
		}
		for (int i = 0; i<25; i++){
			float x = random.nextFloat() * 1000 -500;
			float y = random.nextFloat() * 500 - 250;
			float z = random.nextFloat()* -2000 ;
			entities3.add(new Material(moon, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
		}
		for (int i = 0; i<60; i++){
			float x = random.nextFloat() * 1000 -500;
			float y = random.nextFloat() * 1000 - 500;
			float z = random.nextFloat()* -2000 ;
			allAster.add(new Material(asteroids, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
		}
		for (int i = 0; i<60; i++){
			float x = random.nextFloat() * 1000 -500;
			float y = random.nextFloat() * 1000 - 500;
			float z = random.nextFloat()* -2000 ;
			allAster2.add(new Material(asteroids, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
		}
		for (int i = 0; i<200; i++){
			float x = random.nextFloat() * 1000 -500;
			float y = random.nextFloat() * 500 - 250;
			float z = random.nextFloat()* -2000 ;
			allAster3.add(new Material(asteroids3, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
		}
		for (int i = 0; i<50; i++){
			float x = random.nextFloat() * 500 -250;
			float y = random.nextFloat() * 50 - 25;
			float z = random.nextFloat()* -2000 ;
			allstar.add(new Material(star, new Vector3f(x,y,z), 0f, 0f, 0f, 3f));
		}
		
		Master renderer = new Master(loader);
		Camera camera = new Camera(player);
		ShootMaster.init(loader, renderer.getProjectionMatrix());
	//	ParticleTexture ParticleTexture = new ParticleTexture(loader.loadTexture("particle"), 1);

		while(!Display.isCloseRequested()){
			if(reset){
				entities1 = new ArrayList<Material>();
				entities2 = new ArrayList<Material>();
				entities3 = new ArrayList<Material>();
				allAster = new ArrayList<Material>();
				allAster2 = new ArrayList<Material>();
				allAster3 = new ArrayList<Material>();
				allstar = new ArrayList<Material>();

				random = new Random(seed);
			

				for (int i = 0; i<5; i++){
					float x = random.nextFloat() * 1000 -500;
					float y = random.nextFloat() * 500 - 250;
					float z = random.nextFloat()* -2000 ;
					entities1.add(new Material(jup, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
				}
				for (int i = 0; i<15; i++){
					float x = random.nextFloat() * 1000 -500;
					float y = random.nextFloat() * 500 - 250;
					float z = random.nextFloat()* -3000 ;
					entities2.add(new Material(nexus, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
				}
				for (int i = 0; i<25; i++){
					float x = random.nextFloat() * 1000 -500;
					float y = random.nextFloat() * 500 - 250;
					float z = random.nextFloat()* -2000 ;
					entities3.add(new Material(moon, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
				}
				for (int i = 0; i<60; i++){
					float x = random.nextFloat() * 1000 -500;
					float y = random.nextFloat() * 1000 - 500;
					float z = random.nextFloat()* -2000 ;
					allAster.add(new Material(asteroids, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
				}
				for (int i = 0; i<60; i++){
					float x = random.nextFloat() * 1000 -500;
					float y = random.nextFloat() * 1000 - 500;
					float z = random.nextFloat()* -2000 ;
					allAster2.add(new Material(asteroids, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
				}
				for (int i = 0; i<200; i++){
					float x = random.nextFloat() * 1000 -500;
					float y = random.nextFloat() * 500 - 250;
					float z = random.nextFloat()* -2000 ;
					allAster3.add(new Material(asteroids3, new Vector3f(x,y,z), random.nextFloat() *180f, random.nextFloat()*180f, 0f, 1f));
				}
				for (int i = 0; i<50; i++){
					float x = random.nextFloat() * 500 -250;
					float y = random.nextFloat() * 50 - 25;
					float z = random.nextFloat()* -2000 ;
					allstar.add(new Material(star, new Vector3f(x,y,z), 0f, 0f, 0f, 3f));
				}
				reset=false;
			}
			
			
			if(create){ // one time only creation of the other player
				player2.setPosition(new Vector3f(5,-3f,-15));
				create=false;
			}
			
			
			
			entity2.increaseRotation(0, 0.05f, 0);
			player.move();
			camera.move();
			
			
			if(player2InGame && (xPos != player.getPosition().x || yPos != player.getPosition().y || zPos != player.getPosition().z)){
			client.sendMessage("xpos" + xPos+5);
			client.sendMessage("ypos" + yPos);
			client.sendMessage("zpos" + zPos);
			}
			if(player2InGame){
				xPos = player.getPosition().x;
				yPos = player.getPosition().y;
				zPos = player.getPosition().z;
				checkInputs();
			}
			if(move){ //move is true if the other player has sent x, y, and z coordinates
				//moves the other player to those coordinates and resets each of the member variables
				player2.setPosition(new Vector3f(xPos2,yPos2,zPos2));
				player2.currentTurnSpeed = currentTurnSpeed;
				player2.move2();
				player2.currentSpeed=0;
				player2.currentTurnSpeed=0;
				currentTurnSpeed=0;
				player2.dy=0;
				move=false;
			}

			
			
			
			
			float dx = player.getPosition().x - entity2.getPosition().x;
			float dy = player.getPosition().y - entity2.getPosition().y;
			float dz = player.getPosition().z - entity2.getPosition().z;
			float distance = dx*dx + dy*dy + dz*dz;
			if(distance<140){
				guis = new ArrayList<HUDTexture>();
				sx=-0.9f;

				player.setPosition(new Vector3f(0,-3f,-15));
				Player.xpos = new Vector<Float>();
				Player.ypos = new Vector<Float>();
				Player.zpos = new Vector<Float>();
				Player.turn = new Vector<Float>();
			}
			
			ShootMaster.update();
			
			


			renderer.processEntity(player);
			renderer.processEntity(entity2);
			
			if(player2InGame)
				renderer.processEntity(player2);
			
			
			
			
			
			for (Material entity : entities1){
				renderer.processEntity(entity);
				entity.increaseRotation(0, 0.05f, 0);
				float dx2 = player.getPosition().x - entity.getPosition().x;
				float dy2 = player.getPosition().y - entity.getPosition().y;
				float dz2 = player.getPosition().z - entity.getPosition().z;
				float distance2 = dx2*dx2 + dy2*dy2 + dz2*dz2;
				
					if(distance2<5000){
						guis = new ArrayList<HUDTexture>();
						sx=-0.9f;

						player.setPosition(new Vector3f(0,-3f,-15));
						Player.xpos = new Vector<Float>();
						Player.ypos = new Vector<Float>();
						Player.zpos = new Vector<Float>();
						Player.turn = new Vector<Float>();
					}
							
			}
			for (Material entity : entities2){
				renderer.processEntity(entity);
				entity.increaseRotation(0, 0.05f, 0);
				float dx2 = player.getPosition().x - entity.getPosition().x;
				float dy2 = player.getPosition().y - entity.getPosition().y;
				float dz2 = player.getPosition().z - entity.getPosition().z;
				float distance2 = dx2*dx2 + dy2*dy2 + dz2*dz2;
				
					if(distance2<500){
						guis = new ArrayList<HUDTexture>();
						sx=-0.9f;

						player.setPosition(new Vector3f(0,-3f,-15));
						Player.xpos = new Vector<Float>();
						Player.ypos = new Vector<Float>();
						Player.zpos = new Vector<Float>();
						Player.turn = new Vector<Float>();
					}
						
			}
			for (Material entity : entities3){
				renderer.processEntity(entity);
				entity.increaseRotation(0, 0.05f, 0);
				float dx2 = player.getPosition().x - entity.getPosition().x;
				float dy2 = player.getPosition().y - entity.getPosition().y;
				float dz2 = player.getPosition().z - entity.getPosition().z;
				float distance2 = dx2*dx2 + dy2*dy2 + dz2*dz2;
				
					if(distance2<70){
						guis = new ArrayList<HUDTexture>();
						sx=-0.9f;

						player.setPosition(new Vector3f(0,-3f,-15));
						Player.xpos = new Vector<Float>();
						Player.ypos = new Vector<Float>();
						Player.zpos = new Vector<Float>();
						Player.turn = new Vector<Float>();
					}
							
			}
			for (Material aster : allAster){
				
				renderer.processEntity(aster);
				aster.increaseRotation(5, 0, 0);
				aster.increasePosition2(-1f, -1f, 0);
				float dx2 = player.getPosition().x - aster.getPosition().x;
				float dy2 = player.getPosition().y - aster.getPosition().y;
				float dz2 = player.getPosition().z - aster.getPosition().z;
				float distance2 = dx2*dx2 + dy2*dy2 + dz2*dz2;
				if(distance2<50){
					guis = new ArrayList<HUDTexture>();
					sx=-0.9f;

					player.setPosition(new Vector3f(0,-3f,-15));
					Player.xpos = new Vector<Float>();
					Player.ypos = new Vector<Float>();
					Player.zpos = new Vector<Float>();
					Player.turn = new Vector<Float>();
				}
				for(Shoot p : ShootMaster.particles){
					float dx2x = p.getPosition().x - aster.getPosition().x;
					float dy2y = p.getPosition().y - aster.getPosition().y;
					float dz2z = p.getPosition().z - aster.getPosition().z;
					float distance22 = dx2x*dx2x + dy2y*dy2y + dz2z*dz2z;
					if(distance22<50){
						aster.setPosition(new Vector3f (0,0,-10000));
						}
				}
			}
			for (Material aster2 : allAster2){
				
				renderer.processEntity(aster2);
				aster2.increaseRotation(5, 0, 0);
				aster2.increasePosition3(1f, -1f, 0);
				float dx2 = player.getPosition().x - aster2.getPosition().x;
				float dy2 = player.getPosition().y - aster2.getPosition().y;
				float dz2 = player.getPosition().z - aster2.getPosition().z;
				float distance2 = dx2*dx2 + dy2*dy2 + dz2*dz2;
				if(distance2<50){
					guis = new ArrayList<HUDTexture>();
					sx=-0.9f;

					player.setPosition(new Vector3f(0,-3f,-15));
					Player.xpos = new Vector<Float>();
					Player.ypos = new Vector<Float>();
					Player.zpos = new Vector<Float>();
					Player.turn = new Vector<Float>();
				}
				for(Shoot p : ShootMaster.particles){
					float dx2x = p.getPosition().x - aster2.getPosition().x;
					float dy2y = p.getPosition().y - aster2.getPosition().y;
					float dz2z = p.getPosition().z - aster2.getPosition().z;
					float distance22 = dx2x*dx2x + dy2y*dy2y + dz2z*dz2z;
					if(distance22<50){
						aster2.setPosition(new Vector3f (0,0,-10000));
						}
				}
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				shoot = -100;
				dxx = (float) (shoot * Math.sin(Math.toRadians(player.getRotY())));
				dzz = (float) (shoot * Math.cos(Math.toRadians(player.getRotY())));
				new Shoot(new Vector3f(player.getPosition()), new Vector3f(dxx,0,dzz), 3);
				
				
			}else{
				shoot = 0;
			}

			
			
			for (Material aster3 : allAster3){
				
				renderer.processEntity(aster3);
				aster3.increaseRotation(0.1f, 0, 0);
				float dx2 = player.getPosition().x - aster3.getPosition().x;
				float dy2 = player.getPosition().y - aster3.getPosition().y;
				float dz2 = player.getPosition().z - aster3.getPosition().z;
				float distance2 = dx2*dx2 + dy2*dy2 + dz2*dz2;
				if(distance2<50){
					guis = new ArrayList<HUDTexture>();
					sx=-0.9f;

					player.setPosition(new Vector3f(0,-3f,-15));
					Player.xpos = new Vector<Float>();
					Player.ypos = new Vector<Float>();
					Player.zpos = new Vector<Float>();
					Player.turn = new Vector<Float>();
				}
				for(Shoot p : ShootMaster.particles){
					float dx2x = p.getPosition().x - aster3.getPosition().x;
					float dy2y = p.getPosition().y - aster3.getPosition().y;
					float dz2z = p.getPosition().z - aster3.getPosition().z;
					float distance22 = dx2x*dx2x + dy2y*dy2y + dz2z*dz2z;
					if(distance22<50){
						aster3.setPosition(new Vector3f (0,0,-10000));
						}
				}
				
				
				
			}
			for (Material stars : allstar){

				renderer.processEntity(stars);
				stars.increaseRotation(0f, 0, -0.1f);
				float dx3 = player.getPosition().x - stars.getPosition().x;
				float dy3 = player.getPosition().y - stars.getPosition().y;
				float dz3 = player.getPosition().z - stars.getPosition().z;
				float distance3 = dx3*dx3 + dy3*dy3 + dz3*dz3;
				if(distance3<40){
					
					stars.setPosition(new Vector3f(0,0,-10000));
					HUDTexture points = new HUDTexture(loader.loadTexture("star"), new Vector2f(sx, sy), new Vector2f(0.06f, 0.1f));
					guis.add(points);
					sx+=0.08;

					
				
				}
				float dx31 = player2.getPosition().x - stars.getPosition().x;
				float dy31 = player2.getPosition().y - stars.getPosition().y;
				float dz31 = player2.getPosition().z - stars.getPosition().z;
				float distance31 = dx31*dx31 + dy31*dy31 + dz31*dz31;
				if(distance31<40)
					stars.setPosition(new Vector3f(0,0,-10000));

			}
			

			
			//renderer.render(entity2,shader);
			renderer.render(light, camera);
			
			ShootMaster.renderParticles(camera);
			
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
			
		}
		
		ShootMaster.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		long dt= (System.currentTimeMillis()-time1)/1000;

		System.out.print("time = "+ dt+" s");
		System.exit(0);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		int key = e.getKeyChar();
		if(key==KeyEvent.VK_LEFT){
			client.sendMessage("g");
		}
		if(key==KeyEvent.VK_RIGHT){
			client.sendMessage("j");
		}
		
	}
	
   

}
