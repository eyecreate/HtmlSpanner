package net.nightwhistler.htmlspanner.handlers.table;

import android.graphics.Canvas;

/**
 * A cell of a table, which in turn contains a {@link TableCellContent}.
 */
public class TableCell {

    private Integer cellHeight;
    private Integer cellWidth;
    private int cellPosX;
    private int cellPosY;

    private TableCellContent content;

    public TableCell(TableCellContent content) {
        this.content = content;
    }

    public TableCellContent getContent() {
        return content;
    }

    public void drawContent(Canvas canvas) {
        canvas.translate(cellPosX, cellPosY);
        content.draw(canvas,cellHeight,cellWidth);
        canvas.translate(-cellPosX, -cellPosY);
    }

    public void setCellSize(Integer cellHeight, Integer cellWidth) {
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
    }

    public void setPosition(int posX, int posY) {
        this.cellPosX = posX;
        this.cellPosY = posY;
    }

    public void initializeBorder(Border border) {
        border.setPosX(cellPosX);
        border.setPosY(cellPosY);
        border.setHeight(cellHeight);
        border.setWidth(cellWidth);
        border.setBorderStyle(content.getStyle().getBorderStyle());
    }
}
