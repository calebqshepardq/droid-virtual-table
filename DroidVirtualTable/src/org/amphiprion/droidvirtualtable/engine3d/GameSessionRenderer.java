/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of DroidVirtualTable.
 *
 * DroidVirtualTable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DroidVirtualTable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DroidVirtualTable.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.droidvirtualtable.engine3d;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.controler.ControlerState;
import org.amphiprion.droidvirtualtable.dto.CardGroup;
import org.amphiprion.droidvirtualtable.dto.DeckSection;
import org.amphiprion.droidvirtualtable.dto.GameCard;
import org.amphiprion.droidvirtualtable.dto.GameDeck;
import org.amphiprion.droidvirtualtable.dto.GameSession;
import org.amphiprion.droidvirtualtable.dto.Player;
import org.amphiprion.droidvirtualtable.dto.TableLocation;
import org.amphiprion.droidvirtualtable.dto.TableZone;
import org.amphiprion.droidvirtualtable.engine3d.for2d.Image2D;
import org.amphiprion.droidvirtualtable.engine3d.mesh.AbstractMesh;
import org.amphiprion.droidvirtualtable.engine3d.mesh.CardMesh;
import org.amphiprion.droidvirtualtable.engine3d.mesh.CardPile;
import org.amphiprion.droidvirtualtable.engine3d.shader.Shader;
import org.amphiprion.droidvirtualtable.engine3d.util.GameTableLoader;
import org.amphiprion.droidvirtualtable.entity.Group;
import org.amphiprion.droidvirtualtable.util.SceneUtil;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

public class GameSessionRenderer implements GLSurfaceView.Renderer {
	private static final int FLOAT_SIZE_BYTES = 4;
	private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 8 * FLOAT_SIZE_BYTES;
	private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
	private static final int TRIANGLE_VERTICES_DATA_NOR_OFFSET = 3;
	private static final int TRIANGLE_VERTICES_DATA_TEX_OFFSET = 6;

	// input data
	private Context context;
	private GameSession gameSession;

	// Modelview/Projection matrices
	private float[] mMVPMatrix = new float[16];
	private float[] m2DProjMatrix = new float[16];
	private float[] mProjMatrix = new float[16];
	private float[] mScaleMatrix = new float[16]; // scaling
	private float[] mRotXMatrix = new float[16]; // rotation x
	private float[] mRotYMatrix = new float[16]; // rotation x
	private float[] mTranslateMatrix = new float[16]; // translate x
	private float[] mMMatrix = new float[16]; // rotation
	private float[] mVMatrix = new float[16]; // modelview
	private float[] normalMatrix = new float[16]; // modelview normal
	/**
	 * Stores a copy of the model matrix specifically for the light position.
	 */
	private float[] mLightModelMatrix = new float[16];
	/**
	 * Used to hold a light centered on the origin in model space. We need a 4th
	 * coordinate so we can get translations to work when we multiply this by
	 * our transformation matrices.
	 */
	private final float[] mLightPosInModelSpace = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };

	/**
	 * Used to hold the current position of the light in world space (after
	 * transformation via model matrix).
	 */
	private final float[] mLightPosInWorldSpace = new float[4];

	/**
	 * Used to hold the transformed position of the light in eye space (after
	 * transformation via modelview matrix)
	 */
	private final float[] mLightPosInEyeSpace = new float[4];
	private Player myself;
	// light parameters
	private float[] lightPos;
	private float[] lightColor;
	// material properties
	private float[] matAmbient;
	private float[] matDiffuse;
	private float[] matSpecular;
	private float matShininess;

	// eye pos
	private float[] eyePos = { 0.0f, -7.5f, 9f };
	private float[] lookAt = { 0.0f, -7.5f, 9f };
	private int angleCameraX;
	private int angleCameraZ;

	private CardGroup dropIn;
	// Shader
	private Shader shader;

	private float screenRatio;
	private int realWidth;
	private int realHeight;
	private float screenScaleX = 1;
	private float screenScaleY = 1;

	// TODO temporaire
	CardPile cardPile;
	List<Image2D> playerImages = new ArrayList<Image2D>();

	public GameSessionRenderer(Context context, GameSession gameSession) {
		this.context = context;
		this.gameSession = gameSession;
		// TODO recup�rer reellement le joueur me representant
		myself = gameSession.getPlayers().get(0);
	}

	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(ApplicationConstants.PACKAGE, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// touch up, process here to avoid concurent list modification
		if (!controlerState.down && dropIn != null && selectedCard != null) {
			selectedCard.getContainer().remove(selectedCard);
			dropIn.add(selectedCard);
			dropIn = null;
			selectedCard = null;
		}

		// avoid clearing a mustSelectCard that have been set during this method
		// (thread safe)
		boolean clearMustSelect = mustSelectCard;
		if (mustSelectCard) {
			selectedCard = null;
		}

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		// Ignore the passed-in GL10 interface, and use the GLES20
		// class's static methods instead.
		GLES20.glClearColor(.0f, .0f, .0f, 1.0f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		GLES20.glUseProgram(0);

		// the current shader
		int _program = shader.get_program();

		// Start using the shader
		GLES20.glUseProgram(_program);
		checkGlError("glUseProgram");

		// angleCameraZ++;
		// updateCamera();

		// MMatrix
		Matrix.setLookAtM(mVMatrix, 0, eyePos[0], eyePos[1], eyePos[2], lookAt[0], lookAt[1], lookAt[2], 0, 0, 1);

		// Calculate position of the light. Rotate and then push into the
		// distance.
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, lightPos[0], lightPos[1], lightPos[2]);

		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mVMatrix, 0, mLightPosInWorldSpace, 0);
		// TODO a voir pourquoi elle bouge avec la camera (en attendant histoire
		// de la mettre au bon endroit)
		mLightPosInEyeSpace[0] = lightPos[0];
		mLightPosInEyeSpace[1] = lightPos[1];
		mLightPosInEyeSpace[2] = lightPos[2];
		mLightPosInEyeSpace[3] = lightPos[3];

		/*** DRAWING OBJECT **/
		// Get buffers from mesh
		List<AbstractMesh> meshes = gameSession.getGameTable().getMeshes();

		if (controlerState.move
				&& selectedCard != null
				&& (selectedCard.getContainer().getGroup().getType() == Group.Type.HAND && controlerState.y < 698 - 138 / 2 || selectedCard.getContainer().getGroup().getType() == Group.Type.PILE)) {
			selectedCard.getContainer().remove(selectedCard);
			myself.getCardGroup("Table").add(selectedCard);
			selectedCard.setFrontDisplayed(true);
		}
		for (AbstractMesh mesh : meshes) {
			mesh.draw(true, _program, mMMatrix, mVMatrix, mMVPMatrix, mProjMatrix, normalMatrix, mLightPosInEyeSpace, lightColor, matAmbient, matDiffuse, matSpecular,
					matShininess, eyePos);
		}

		for (Player p : gameSession.getPlayers()) {
			for (CardGroup cg : p.getCardGroups()) {
				if (cg.getGroup().getType() == Group.Type.TABLE) {
					for (GameCard card : cg.getCards()) {
						CardMesh cardMesh = card.getCardMesh();
						if (controlerState.move && card == selectedCard) {
							computeMovingCardPosition();
						}
						// cardMesh.z = tz.getZ() + CardMesh.CARD_HEIGHT *
						// (nbCard - 1) + CardMesh.CARD_HEIGHT / 2;
						cardMesh.globalPlayerRotationZ = p.getTableLocation().getGlobalRotation();
						cardMesh.draw(cg.isFrontDisplayed(card, myself.getName()), _program, mMMatrix, mVMatrix, mMVPMatrix, mProjMatrix, normalMatrix, mLightPosInEyeSpace,
								lightColor, matAmbient, matDiffuse, matSpecular, matShininess, eyePos);

						if (mustSelectCard) {
							float[] intersect = SceneUtil.ScreenTo3D(controlerState.x, controlerState.y, realWidth, realHeight, eyePos, mProjMatrix, mVMatrix, cardMesh.z);
							if (Math.abs(intersect[0] - cardMesh.x) < cardMesh.getWidth() / 2 && Math.abs(intersect[1] - cardMesh.y) < cardMesh.getHeight() / 2) {
								selectedCard = card;
							}
						}
					}
				} else if (cg.getGroup().getType() == Group.Type.PILE) {

					TableZone tz = p.getTableLocation().getTableZone(cg.getGroup().getName());
					int nbCard = cg.count();
					if (nbCard > 0) {
						if (nbCard > 1) {
							// TODO recup�rer la taille
							cardPile.scaleX = cg.getGroup().getWidth() / 100.0f;
							cardPile.scaleY = cg.getGroup().getHeight() / 100.0f;
							cardPile.scaleZ = CardMesh.CARD_HEIGHT * (nbCard - 1);

							cardPile.x = tz.getX();
							cardPile.y = tz.getY();
							cardPile.z = tz.getZ() + CardMesh.CARD_HEIGHT * (nbCard - 1) / 2.0f;
							cardPile.draw(true, _program, mMMatrix, mVMatrix, mMVPMatrix, mProjMatrix, normalMatrix, mLightPosInEyeSpace, lightColor, matAmbient, matDiffuse,
									matSpecular, matShininess, eyePos);
						}

						GameCard card = cg.getCards().get(nbCard - 1);
						CardMesh cardMesh = card.getCardMesh();
						cardMesh.x = tz.getX();
						cardMesh.y = tz.getY();
						cardMesh.z = tz.getZ() + CardMesh.CARD_HEIGHT * (nbCard - 1) + CardMesh.CARD_HEIGHT / 2;
						cardMesh.globalPlayerRotationZ = p.getTableLocation().getGlobalRotation();
						cardMesh.draw(cg.isFrontDisplayed(card, myself.getName()), _program, mMMatrix, mVMatrix, mMVPMatrix, mProjMatrix, normalMatrix, mLightPosInEyeSpace,
								lightColor, matAmbient, matDiffuse, matSpecular, matShininess, eyePos);

						// ///
						if (mustSelectCard) {
							float[] intersect = SceneUtil.ScreenTo3D(controlerState.x, controlerState.y, realWidth, realHeight, eyePos, mProjMatrix, mVMatrix, cardMesh.z);
							if (Math.abs(intersect[0] - cardMesh.x) < cardMesh.getWidth() / 2 && Math.abs(intersect[1] - cardMesh.y) < cardMesh.getHeight() / 2) {
								selectedCard = card;
							}
						}
					}
				}
			}
		}
		// 2D
		// TODO essayer de virer tous les parametres de la method draw et faire
		// un shader plus
		// simple
		// au niveau lumiere
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		// player.y = 1;// 0.3218f * realHeight / 2;
		for (Image2D img2D : playerImages) {
			img2D.draw(_program, mMMatrix, mVMatrix, mMVPMatrix, m2DProjMatrix, normalMatrix, mLightPosInEyeSpace, lightColor, matAmbient, matDiffuse, matSpecular, matShininess,
					eyePos);
		}
		// TODO temporaire affiche la main du joeuru 0 (faire en vrai
		// l'affichage de sa propre main)
		Player p = myself;
		CardGroup cg = p.getCardGroup("Hand");
		int offsetX = 1280 / 2 - cg.count() / 2 * (99 + 5 / 2);
		int selectedIndex = -1;
		int index = -1;
		for (GameCard card : cg.getCards()) {
			index++;
			Image2D img = card.getImage2D();
			if (card.getCardMesh().isLandscape()) {
				img.scaleX = 138f / img.getTexture().originalWidth;
				img.scaleY = 99f / img.getTexture().originalHeight;
				img.ownRotationZ = 90;
			} else {
				img.scaleX = 99f / img.getTexture().originalWidth;
				img.scaleY = 138f / img.getTexture().originalHeight;
				img.ownRotationZ = 0;
			}
			img.x = offsetX;
			img.y = 698;
			if (mustSelectCard && controlerState.y > 698 - 138 / 2 && Math.abs(offsetX - controlerState.x) <= 99 / 2) {
				selectedCard = card;
			}
			offsetX += 99 + 5;
			if (selectedCard == null || selectedCard != card) {
				img.draw(_program, mMMatrix, mVMatrix, mMVPMatrix, m2DProjMatrix, normalMatrix, mLightPosInEyeSpace, lightColor, matAmbient, matDiffuse, matSpecular, matShininess,
						eyePos);
			} else {
				selectedIndex = index;
			}
		}
		// affiche la carte selectionn�e en dernier afin d'�tre toujours au
		// premier plan
		if (selectedIndex != -1) {
			Image2D img = selectedCard.getImage2D();
			if (controlerState.move) {
				if (controlerState.dx > 0 && selectedIndex < cg.count() - 1) {
					img.x += controlerState.dx;
					// pour le prochain passage on deplace la carte dans la main
					if (controlerState.dx > 99 / 2 + 5) {
						controlerState.dx -= 99 + 5;
						cg.move(selectedIndex, selectedIndex + 1);
					}
				} else if (controlerState.dx < 0 && selectedIndex > 0) {
					img.x += controlerState.dx;
					// pour le prochain passage on deplace la carte dans la main
					if (-controlerState.dx > 99 / 2 + 5) {
						controlerState.dx += 99 + 5;
						cg.move(selectedIndex, selectedIndex - 1);
					}
				}
			}
			img.draw(_program, mMMatrix, mVMatrix, mMVPMatrix, m2DProjMatrix, normalMatrix, mLightPosInEyeSpace, lightColor, matAmbient, matDiffuse, matSpecular, matShininess,
					eyePos);

		}

		if (showDetail != null) {
			Image2D img = showDetail.getImage2D();
			img.scaleX = 1f / screenScaleX;
			img.scaleY = 1f / screenScaleY;
			img.ownRotationZ = 0;
			img.x = 1280 / 2;
			img.y = 768 / 2;
			img.draw(_program, mMMatrix, mVMatrix, mMVPMatrix, m2DProjMatrix, normalMatrix, mLightPosInEyeSpace, lightColor, matAmbient, matDiffuse, matSpecular, matShininess,
					eyePos);
		}
		/** END DRAWING OBJECT ***/
		GLES20.glDisable(GLES20.GL_BLEND);

		if (clearMustSelect) {
			mustSelectCard = false;
		}

		update();

	}

	private void computeMovingCardPosition() {
		TableZone zone = null;
		CardGroup group = null;
		if (controlerState.y > 698 - 138 / 2) {
			dropIn = myself.getCardGroup("Hand");
		} else {
			// parourir tous les groupes
			// parourir toutes les structure 3d
			for (Player p : gameSession.getPlayers()) {
				for (CardGroup cg : p.getCardGroups()) {
					if (cg.getGroup().getType() == Group.Type.PILE) {
						TableZone tz = p.getTableLocation().getTableZone(cg.getGroup().getName());
						if (zone == null || zone.getZ() < tz.getZ()) {
							float demiScaleX = cg.getGroup().getWidth() / 200.0f;
							float demiScaleY = cg.getGroup().getHeight() / 200.0f;
							float[] intersect = SceneUtil.ScreenTo3D(controlerState.x, controlerState.y, realWidth, realHeight, eyePos, mProjMatrix, mVMatrix, tz.getZ());
							if (intersect[0] >= tz.getX() - demiScaleX && intersect[0] <= tz.getX() + demiScaleX && intersect[1] >= tz.getY() - demiScaleY
									&& intersect[1] <= tz.getY() + demiScaleY) {
								// on est sur un groupe
								zone = tz;
								group = cg;
							}
						}
					}
				}
			}
			dropIn = group;
		}
		CardMesh cardMesh = selectedCard.getCardMesh();
		if (zone != null) {
			cardMesh.x = zone.getX();
			cardMesh.y = zone.getY();
			cardMesh.z = zone.getZ() + CardMesh.CARD_HEIGHT * group.count() + CardMesh.CARD_HEIGHT / 2;
		} else {
			float[] intersect = SceneUtil.ScreenTo3D(controlerState.x, controlerState.y, realWidth, realHeight, eyePos, mProjMatrix, mVMatrix, 0f);
			cardMesh.x = intersect[0];
			cardMesh.y = intersect[1];
			cardMesh.z = 0.115465f + CardMesh.CARD_HEIGHT / 2; // tempo,
																// normalmet il
			// faut mettre
			// CardMesh.CARD_HEIGHT
			// / 2
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		realWidth = width;
		realHeight = height;
		screenScaleX = width / 1280.0f;
		screenScaleY = height / 768.0f;

		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;

		float zNear = 0.5f;
		float zFar = 100;
		float top = zNear * (float) Math.tan(45 * Math.PI / 360.0);
		float bottom = -top;
		float left = bottom * ratio;
		float right = top * ratio;
		screenRatio = ratio;
		// View projection
		Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, zNear, zFar);
		Matrix.orthoM(m2DProjMatrix, 0, 0, 1280, 0, 768, 0, 10);

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		shader = new Shader(R.raw.gouraud_vs, R.raw.gouraud_ps, context, false, 0); // gouraud

		// TODO organiser les image par player dans gamesession
		Image2D img2D = new Image2D(context, gl, "active_player", R.drawable.mask_texture);
		img2D.x = 1280 / 2;
		img2D.y = 698;
		img2D.scaleX = 1280 / 2;// 2 pixel texture widht
		img2D.scaleY = 155 / 2.0f;// 2 pixel texture height
		playerImages.add(img2D);
		img2D = new Image2D(context, gl, "active_player", R.drawable.active_player);
		img2D.x = 86;
		img2D.y = 698;
		playerImages.add(img2D);
		img2D = new Image2D(context, gl, "default_player_avatar", R.drawable.default_player_avatar);
		img2D.x = 86;
		img2D.y = 698;
		playerImages.add(img2D);
		img2D = new Image2D(context, gl, "default_player_avatar", R.drawable.player_canvas);
		img2D.x = 86;
		img2D.y = 698;
		playerImages.add(img2D);
		img2D = new Image2D(context, gl, "default_player_avatar", R.drawable.deck_counter);
		img2D.x = 86 - 121 / 2;
		img2D.y = 698 - 29 / 2;
		playerImages.add(img2D);
		img2D = new Image2D(context, gl, "default_player_avatar", R.drawable.hand_counter);
		img2D.x = 86 - 121 / 2;
		img2D.y = 698 + 29 / 2;
		playerImages.add(img2D);

		// TODO tempo (faire un loader task, pour voir l'avancement)
		for (Player p : gameSession.getPlayers()) {
			GameDeck deck = p.getDeck();
			for (DeckSection section : deck.getSections()) {
				for (GameCard card : section.getCards()) {
					p.addCard(section.getStartupGroupName(), card);
					// TODO prendre le back de la definition
					CardMesh cardMesh = new CardMesh(gl, card.getCard().getName(), gameSession.getGameTable().getTable().getGame().getId() + "/cards/back.jpg", gameSession
							.getGameTable().getTable().getGame().getId()
							+ "/sets/" + card.getCard().getGameSet().getId() + "/" + card.getCard().getImageName());
					card.setCardMesh(cardMesh);
				}
			}
		}
		// TODO temporaire pour mettre des carte dans la main
		Player p = myself;
		for (int i = 0; i < 7; i++) {
			GameCard c = p.getCardGroup("Deck").takeTopCard();
			p.addCard("Hand", c);
		}

		cardPile = new CardPile(context, gl);

		// load meshes
		try {
			GameTableLoader.load(gl, gameSession);
		} catch (Exception e) {
			Log.e(ApplicationConstants.PACKAGE, "onSurfaceCreated", e);
		}

		TableLocation loc = myself.getTableLocation();
		eyePos[0] = loc.getCamera().getX();
		eyePos[1] = loc.getCamera().getY();
		eyePos[2] = loc.getCamera().getZ();
		angleCameraX = loc.getCamera().getAngleX();
		angleCameraZ = loc.getCamera().getAngleZ();

		GLES20.glClearDepthf(1.0f);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);

		// cull backface
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);

		// light variables
		float[] lightP = { 0.0f, -7.5f, 16f, 1 };
		lightPos = lightP;

		float[] lightC = { 1f, 1f, 1f };
		lightColor = lightC;

		// material properties
		float[] mA = { 0.5f, 0.5f, 0.5f, 1.0f };
		matAmbient = mA;

		float[] mD = { 1f, 1f, 1f, 1f };
		matDiffuse = mD;

		float[] mS = { 1f, 1f, 1f, 1f };
		matSpecular = mS;

		matShininess = 5f;

		updateCamera();
	}

	private void updateCamera() {
		double dist = Math.sqrt(eyePos[0] * eyePos[0] + eyePos[1] * eyePos[1]);
		eyePos[0] = (float) (dist * Math.sin(angleCameraZ * Math.PI / 180));
		eyePos[1] = -(float) (dist * Math.cos(angleCameraZ * Math.PI / 180));

		lookAt[0] = eyePos[0] - (float) Math.sin(angleCameraZ * Math.PI / 180) * (float) Math.cos(angleCameraX * Math.PI / 180);
		lookAt[1] = eyePos[1] + (float) Math.cos(angleCameraZ * Math.PI / 180) * (float) Math.cos(angleCameraX * Math.PI / 180);
		lookAt[2] = eyePos[2] - (float) Math.sin(angleCameraX * Math.PI / 180);

	}

	private boolean mustSelectCard = false;
	private GameCard selectedCard;
	private GameCard showDetail;
	private ControlerState controlerState = new ControlerState();

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			controlerState.down = true;
			controlerState.downAt = System.currentTimeMillis();
			controlerState.x = event.getX() / screenScaleX;
			controlerState.y = event.getY() / screenScaleY;
			mustSelectCard = true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			controlerState.clear();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (controlerState.move || Math.abs(event.getX() / screenScaleX - controlerState.x) > 10 / screenScaleX
					|| Math.abs(event.getY() / screenScaleY - controlerState.y) > 10 / screenScaleY) {
				controlerState.move = true;
				showDetail = null;
				// calculer dx, dy -> delta global par rapport au point
				// d'origine
				controlerState.dx += event.getX() / screenScaleX - controlerState.x;
				controlerState.dy += event.getY() / screenScaleY - controlerState.y;
				controlerState.x = event.getX() / screenScaleX;
				controlerState.y = event.getY() / screenScaleY;
			}
		}

		return true;
	}

	private void update() {
		if (showDetail != null && !controlerState.down) {
			showDetail = null;
		} else if (showDetail == null && controlerState.down && !controlerState.move) {
			if (System.currentTimeMillis() - controlerState.downAt > 1000) {
				if (selectedCard != null) {
					if (selectedCard.getContainer().isFrontDisplayed(selectedCard, myself.getName())) {
						showDetail = selectedCard;
						showDetail.setImage2D(new Image2D(showDetail.getCardMesh().getTexture(true), showDetail.getCardMesh().getName()));
					}
				}
			}
		}
	}

}
