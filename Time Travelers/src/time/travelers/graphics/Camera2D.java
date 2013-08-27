package time.travelers.graphics;

import java.awt.geom.Rectangle2D;

public class Camera2D 
{
	public double x, y, width, height;
	public double zoomFactor;
	
	public double getMiddleX(){ return x+width/2; }
	public double getMiddleY(){ return y+height/2; }
	public double getTrueWidth() { return width*zoomFactor;}
	public double getTrueHeight() { return height*zoomFactor;}
	
	public Rectangle2D.Double getViewRectangle() {
		return new Rectangle2D.Double(
				getMiddleX() - getTrueWidth() / 2,
				getMiddleY() - getTrueHeight() / 2,
				getTrueWidth(),
				getTrueHeight());
	}
	public void translateCamera(double x, double y) {
		this.x+=x;
		this.y+=y;
	}
	
	/**
	 * Gets the width of this camera's viewing rectangle, with no zoom taken into account.
	 * @return
	 * The width without zoom taken into account.
	 */
	public double getWidth() {
		return this.width;
	}
	
	public void setWidth(double width) {
		if(width < 0)
			width = 0;
		this.width = width;
	}
	
	/**
	 * Gets the height of this camera's viewing rectangle, with no zoom taken into account.
	 * @return
	 * The height without zoom taken into account.
	 */
	public double getHeight() {
		return this.height;
	}
	
	public void setHeight(double height) {
		if(height < 0)
			height = 0;
		this.height = height;
	}
	
	/**
	 * Checks if this camera is an "invalid" camera that does not show anything in it's view.
	 * @return
	 * True if this camera's view is invalid, else false.
	 */
	public boolean isNullCamera() {
		return this.width == 0 || this.height == 0 || this.zoomFactor == 0;
	}
	
	public void setZoom(double factor) {
		if(factor < 0)
			factor = 0;
		this.zoomFactor = factor;
	}
	
	public double getZoom() {
		return this.zoomFactor;
	}
}
