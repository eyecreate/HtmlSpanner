package net.nightwhistler.htmlspanner.handlers.table;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;

import net.nightwhistler.htmlspanner.style.Style;

/**
 * The content of a table cell.
 */
public class TableCellContent {

    private Typeface typeFace = Typeface.DEFAULT;
    private float textSize = 16f;
    private int textColor = Color.BLACK;

    private static final int PADDING = 4;
    private static final int RIGHT_EXTRA_PADDING = 3;
    private static final int MAX_COLUMN_WIDTH = 400;

    private Spanned spanned;

    private Style style;

    public Spanned getSpanned() {
        return spanned;
    }

    public void setSpanned(Spanned spanned) {
        this.spanned = spanned;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public int getPaddedHeight() {
        StaticLayout staticLayout = getMaxContentWidthLayout();
        return staticLayout.getHeight() + 2 * PADDING;
    }

    public int getPaddedWidth() {
        StaticLayout maxWidthLayout = getMaxContentWidthLayout();

        float maxLineWidth = 0;
        for (int i = 0; i < maxWidthLayout.getLineCount(); i++) {
            maxLineWidth = Math.max(maxLineWidth, maxWidthLayout.getLineWidth(i));
        }

        int textWrapWidth = (int) Math.ceil(maxLineWidth);
        return textWrapWidth + 2 * PADDING + RIGHT_EXTRA_PADDING;
    }

    private StaticLayout getMaxContentWidthLayout() {
        int maxContentWidth = MAX_COLUMN_WIDTH - 2 * PADDING - RIGHT_EXTRA_PADDING;
        return new StaticLayout(spanned,
                getTextPaint(), maxContentWidth,
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true);
    }

    public void draw(Canvas canvas, int cellHeight, int cellWidth) {
        StaticLayout drawLayout = getContentLayout(cellWidth);

        // compute position of content in the cell
        int paddedHeight = drawLayout.getHeight() + 2 * PADDING;
        int verticalCenteredTopY = (cellHeight - paddedHeight) / 2 + PADDING;
        int leftAlignedLeftX = PADDING;

        // drawContent content
        canvas.translate(leftAlignedLeftX, verticalCenteredTopY);
        drawLayout.draw(canvas);
        canvas.translate(-leftAlignedLeftX, -verticalCenteredTopY);
    }

    private StaticLayout getContentLayout(int cellWidth) {
        Layout.Alignment alignment = null;
        Style.TextAlignment textAlignment = style.getTextAlignment();
        if (textAlignment == Style.TextAlignment.CENTER)
            alignment = Layout.Alignment.ALIGN_CENTER;
        else if (textAlignment == Style.TextAlignment.RIGHT)
            alignment = Layout.Alignment.ALIGN_OPPOSITE;
        else
            alignment = Layout.Alignment.ALIGN_NORMAL;

        int maxDrawWidth = cellWidth - 2 * PADDING - RIGHT_EXTRA_PADDING;
        return new StaticLayout(spanned,
                getTextPaint(), maxDrawWidth,
                alignment, 1f, 0f, true);
    }

    private TextPaint getTextPaint() {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(this.textColor);
        textPaint.linkColor = this.textColor;
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(this.textSize);
        textPaint.setTypeface(this.typeFace);

        return textPaint;
    }
}
