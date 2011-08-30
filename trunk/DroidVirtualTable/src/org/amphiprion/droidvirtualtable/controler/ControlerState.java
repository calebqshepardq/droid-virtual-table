package org.amphiprion.droidvirtualtable.controler;

public class ControlerState {

	public float x;
	public float y;
	public boolean down;
	public long downAt;
	public boolean move;
	public float dx;
	public float dy;

	public void clear() {
		x = 0;
		y = 0;
		down = false;
		downAt = 0;
		move = false;
		dx = 0;
		dy = 0;
	}
}
