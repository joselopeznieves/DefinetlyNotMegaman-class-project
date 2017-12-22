package rbadia.voidspace.main;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import rbadia.voidspace.graphics.NewGraphicsManager;
import rbadia.voidspace.model.BigBullet;
import rbadia.voidspace.model.Boss;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.Platform;
import rbadia.voidspace.sounds.SoundManager;

public class Level4State extends Level3State {
	
	protected Boss boss;
	protected List<BigBullet> bossBullets;
	private boolean isBossGoingDown = true;
	private boolean isBossSpawning = true;
	private int bossDamage = 0;
	private long lastBossBulletTime;
	private long lastCollisionTime;
	
	protected Boss boss2;
	protected List<BigBullet> boss2Bullets;
	private boolean isBoss2GoingDown = false;

	private boolean isBoss2Spawning = true;
	private int boss2Damage = 0;
	
	private long lastBoss2BulletTime;
	private long lastCollisionTime2;


	private static final long serialVersionUID = 8738640199070011353L;

	public Level4State(int level, MainFrame frame, GameStatus status, 
			NewLevelLogic gameLogic, InputHandler inputHandler,
			NewGraphicsManager graphicsMan, SoundManager soundMan) {
		super(level, frame, status, gameLogic, inputHandler, graphicsMan, soundMan);
	}
	
	
	public void doStart() {	
		super.doStart();
		setStartState(GETTING_READY);
		setCurrentState(getStartState());
		
		bossBullets = new ArrayList<BigBullet>();
		
		newBoss(this);
	}
	
	protected void drawAsteroid() {
		Graphics2D g2d = getGraphics2D();
		GameStatus status = getGameStatus();
		if((asteroid.getX() + asteroid.getWidth() >  0)){
			asteroid.translate(-asteroid.getSpeed() + rand.nextInt(asteroid.getSpeed()) - 
					rand.nextInt(asteroid.getSpeed()), rand.nextInt(asteroid.getSpeed()) - 
					rand.nextInt(asteroid.getSpeed()));
			getGraphicsManager().drawAsteroid(asteroid, g2d, this);	
		}
		else {
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
				// draw a new asteroid
				lastAsteroidTime = currentTime;
				status.setNewAsteroid(false);
				asteroid.setLocation((int) (this.getWidth() - asteroid.getPixelsWide()),
						(rand.nextInt((int) (this.getHeight() - asteroid.getPixelsTall() - 32))));
			}

			else{
				// draw explosion
				getGraphicsManager().drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}
		
		if((asteroid2.getX() + asteroid2.getWidth() >  0)){
			asteroid2.translate(-asteroid2.getSpeed() + rand.nextInt(asteroid2.getSpeed()) - 
					rand.nextInt(asteroid2.getSpeed()), rand.nextInt(asteroid2.getSpeed()) - 
					rand.nextInt(asteroid2.getSpeed()));
			getGraphicsManager().drawAsteroid(asteroid2, g2d, this);	
		}
		else {
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastAsteroid2Time) > NEW_ASTEROID_DELAY){
				// draw a new asteroid
				lastAsteroid2Time = currentTime;
				status.setNewAsteroid(false);
				asteroid2.setLocation((int) (this.getWidth() - asteroid2.getPixelsWide()),
						(rand.nextInt((int) (this.getHeight() - asteroid2.getPixelsTall() - 32))));
			}

			else{
				// draw explosion
				getGraphicsManager().drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}
	}
	

	@Override
	public Platform[] newPlatforms(int n){
		platforms = new Platform[n];
		int k = 1;
		for(int i=0; i<n; i++){
			this.platforms[i] = new Platform(0,0);
			
			if(i < 4 ) {
				platforms[i].setLocation(200, getHeight() / 4 + (i + 1) * 60);
			}
			else {
			
				platforms[i].setLocation(250, getHeight() / 6 + (k) * 60);
				k++;
			}
		}
		return platforms;
	}
	
	protected void drawBoss(){
		Graphics2D g2d = getGraphics2D();
		if(isBossSpawning){
			boss.setLocation(this.getWidth() - boss.getPixelsWide(), 0);
			getGraphicsManager().drawBoss(boss, g2d, this);
			isBossSpawning = false;
		}
		
		if(this.getHeight() - boss.getPixelsTall() > boss.getY() && isBossGoingDown){
			boss.translate(0, boss.getDefaultSpeed());
			getGraphicsManager().drawBoss(boss, g2d, this);
		}
		else{
			isBossGoingDown = false;
		}
		
		if(boss.getY() > 0 && !isBossGoingDown){
			boss.translate(0, -boss.getDefaultSpeed());
			getGraphicsManager().drawBoss(boss, g2d, this);
		}
		else if(!isBossGoingDown){
			isBossGoingDown = true;
			boss.translate(0, boss.getDefaultSpeed());
			getGraphicsManager().drawBoss(boss, g2d, this);
		}
	}
	
	protected void checkMegaManBossCollisions() {
		GameStatus status = getGameStatus();
		if(boss.intersects(megaMan)){
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastCollisionTime) > 1000){
				lastCollisionTime = currentTime;
				status.setLivesLeft(status.getLivesLeft() - 1);
			}
		}
	}
	
	protected void checkBossBulletMegaManCollisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bossBullets.size(); i++){
			BigBullet bigBullet = bossBullets.get(i);
			if(megaMan.intersects(bigBullet)){
				status.setLivesLeft(status.getLivesLeft() - 1);
				bossBullets.remove(i);
				break;
			}
		}
	}
	
	protected void checkBulletBossCollisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(boss.intersects(bullet)){
				bossDamage++;
				if(bossDamage >= 5){
					status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 500);
					removeBoss(boss);
				}
				// remove bullet
				bullets.remove(i);
				break;
			}
		}
	}
	
	protected void checkBigBulletBossCollisions() {
		GameStatus status = getGameStatus();
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			if(boss.intersects(bigBullet)){
				bossDamage += 3;
				if(bossDamage >= 5){
					status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 500);
					removeBoss(boss);
				}
				// remove big bullet
				bigBullets.remove(i);
				break;
			}
		}
	}
	
	protected void drawBossBullets() {
		Graphics2D g2d = getGraphics2D();
		
		long currentTime = System.currentTimeMillis();
		if((currentTime - lastBossBulletTime) > 1000){
			lastBossBulletTime = currentTime;
			fireBossBullet();
		}
		for(int i=0; i<bossBullets.size(); i++){
			BigBullet bigBullet = bossBullets.get(i);
			getGraphicsManager().drawBigBullet(bigBullet, g2d, this);

			boolean remove = this.moveBossBullet(bigBullet);
			if(remove){
				bossBullets.remove(i);
				i--;
			}
		}
	}
	
	public void fireBossBullet(){
		int xPos = boss.x;
		int yPos = boss.y + boss.width/2 - BigBullet.HEIGHT + 4;
		BigBullet  bossBullet = new BigBullet(xPos, yPos);
		bossBullets.add(bossBullet);
		this.getSoundManager().playBulletSound();
	}
	
	public boolean moveBossBullet(BigBullet bossBullet){
		if(bossBullet.getY() - bossBullet.getSpeed() >= 0){
			bossBullet.translate(-bossBullet.getSpeed(), 0);
			return false;
		}
		else{
			return true;
		}
	}
	
	public Boss newBoss(NewLevel2State screen){
		int xPos = (int) (screen.getWidth() - Boss.WIDTH);
		int yPos = 0;
		boss = new Boss(xPos, yPos);
		return boss;
	}
	
	public void removeBoss(Boss boss){
		// "remove" boss
		bossExplosion = new Rectangle(
				boss.x,
				boss.y,
				boss.getPixelsWide(),
				boss.getPixelsTall());
		boss.setLocation(-boss.getPixelsWide(), -boss.getPixelsTall());
		// play boss explosion sound
		this.getSoundManager().playAsteroidExplosionSound();
	}


}
