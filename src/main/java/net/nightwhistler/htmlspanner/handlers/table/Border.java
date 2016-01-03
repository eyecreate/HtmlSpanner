package net.nightwhistler.htmlspanner.handlers.table;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import net.nightwhistler.htmlspanner.style.Style;

/**
 * The border of a table or table cell.
 */
public class Border {

    private Style.BorderStyle borderStyle;

    private int posX, posY, width, height;

    public Style.BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(Style.BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
    }

    public void draw(Canvas canvas) {
        if (borderStyle == null)
            return;

        canvas.translate(posX, posY);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        clearBorder(canvas, paint);
        drawBorder(canvas, paint);
        canvas.translate(-posX,-posY);
    }

    private void drawBorder(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);

        Style.BorderStyle borderStyle = getBorderStyle();
        if (borderStyle == Style.BorderStyle.DASHED) {
            paint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        } else if (borderStyle == Style.BorderStyle.DOTTED) {
            paint.setPathEffect(new DashPathEffect(new float[]{1, 1}, 0));
        } else {
            paint.setPathEffect(new DashPathEffect(new float[]{0, 0}, 0));
        }

        canvas.drawRect(0, 0, width, height, paint);
    }

    private void clearBorder(Canvas canvas, Paint paint) {
        // this is needed because of an issue, where the rectangle is slightly at a different position, if path effect is null
        paint.setPathEffect(new DashPathEffect(new float[]{0, 0}, 0));

        // better use background color
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);
    }


    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }
}
