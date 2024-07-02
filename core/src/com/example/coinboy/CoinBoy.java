package com.example.coinboy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Random;

public class CoinBoy extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float g = 0.2f;
	float v = 0;
	int man_y = 0;
	Rectangle manRectangle;
	BitmapFont font;

	ArrayList<Integer> coinX = new ArrayList<Integer>();
	ArrayList<Integer> coinY = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;


	ArrayList<Integer> bombX = new ArrayList<Integer>();
	ArrayList<Integer> bombY = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;
	int score = 0;
	int gameState = 0;
	Texture dizzy;
	Random random;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		man_y = Gdx.graphics.getHeight() / 2;
		coin =new Texture("coin.png");
		bomb =new Texture("bomb.png");
		random = new Random();
		manRectangle = new Rectangle();

		dizzy= new Texture("dizzy-1.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	public void  makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinY.add((int)height);
		coinX.add(Gdx.graphics.getWidth());
	}

	public void  makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombY.add((int)height);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1){
			//GAME IS LIVE
			//BOMB
			if(bombCount < 250){
				bombCount++;
			} else{
				bombCount = 0;
				makeBomb();
			}

			bombRectangle.clear();
			for(int i = 0; i < bombX.size(); i++){
				batch.draw(bomb, bombX.get(i), bombY.get(i));
				bombX.set(i, bombY.get(i) - 8);
				bombRectangle.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//Coin
			if(coinCount < 100){
				coinCount++;
			} else{
				coinCount = 0;
				makeCoin();
			}

			coinRectangle.clear();
			for(int i = 0; i < coinX.size(); i++){
				batch.draw(coin, coinX.get(i), coinY.get(i));
				coinX.set(i, coinX.get(i) - 4);
				coinRectangle.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(), coin.getHeight()));
			}

			if(Gdx.input.justTouched()){
				v = -20;
			}

			if(pause < 2){
				pause++;
			}else{
				pause = 0;
				if(manState < 3){
					manState++;
				} else{
					manState = 0;
				}
			}

			v += g;
			man_y -= v;

			if(man_y <= 0){
				man_y = 0;
			}

		} else if (gameState == 0){
			//Waiting to Start
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		} else if (gameState == 2) {
			//Game Over
			if(Gdx.input.justTouched()){
				gameState = 1;
				man_y = Gdx.graphics.getHeight() / 2;
				score = 0;
				v = 0;
				coinX.clear();
				coinY.clear();
				coinRectangle.clear();
				coinCount = 0;

				bombX.clear();
				bombY.clear();
				bombRectangle.clear();
				bombCount = 0;
			}
		}

		if(gameState == 2){
			batch.draw(dizzy, (Gdx.graphics.getWidth() - man[manState].getWidth()) / 2, man_y);
		} else {
			batch.draw(man[manState], (Gdx.graphics.getWidth() - man[manState].getWidth()) / 2, man_y);
		}

		batch.draw(man[manState], (Gdx.graphics.getWidth() - man[manState].getWidth()) / 2, man_y);

		manRectangle = new Rectangle((Gdx.graphics.getWidth() - man[manState].getWidth()) / 2, man_y, man[manState].getWidth(), man[manState].getHeight());

		for(int i = 0; i < coinRectangle.size(); i++){
			if(Intersector.overlaps(manRectangle, coinRectangle.get(i))){
				score++;

				coinRectangle.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				break;
			}
		}

		for(int i = 0; i < bombRectangle.size(); i++){
			if(Intersector.overlaps(manRectangle, bombRectangle.get(i))){
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
