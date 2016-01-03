package net.nightwhistler.htmlspanner.handlers.table;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

import net.nightwhistler.htmlspanner.style.Style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A drawable table, containing a matrix of {@link TableCell}s.
 * It draws the cell borders in an order, such that solid borders draw over dashed,
 * which in turn draw over dotted borders.
 */
public class TableDrawable extends Drawable {

    private Style style;

    private List<List<TableCell>> cellMatrix = new ArrayList<List<TableCell>>();

    public void addRow() {
        cellMatrix.add(new ArrayList<TableCell>());
    }

    public void addCell(TableCell text) {
        if (cellMatrix.isEmpty()) {
            throw new IllegalStateException("No rows added yet");
        }
        List<TableCell> row = cellMatrix.get(cellMatrix.size() - 1);
        row.add(text);
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    @Override
    public void draw(Canvas canvas) {
        ArrayList<Integer> rowHeights = getRowHeights();
        ArrayList<Integer> colWidths = getColumnWidths();

        setCellSizesAndPositions(rowHeights, colWidths);

        List<Border> borders = new LinkedList<Border>();
        for(List<TableCell> row : cellMatrix) {
            for(TableCell cell : row) {
                cell.drawContent(canvas);
                Border border = new Border();
                cell.initializeBorder(border);
                borders.add(border);
            }
        }
        borders.add(getTableBorder());

        Collections.sort(borders, new BorderPriorityComparator());
        for(Border border : borders) {
            border.draw(canvas);
        }
    }

    private Border getTableBorder() {
        Border tableBorder = new Border();
        tableBorder.setWidth(getIntrinsicWidth());
        tableBorder.setHeight(getIntrinsicHeight());
        tableBorder.setPosX(0);
        tableBorder.setPosY(0);
        tableBorder.setBorderStyle(style.getBorderStyle());
        return tableBorder;
    }

    private ArrayList<Integer> getRowHeights() {
        ArrayList<Integer> rowHeights = new ArrayList<Integer>();
        for(List<TableCell> row : cellMatrix) {
            int maxRowHeight = 0;
            for(TableCell cell : row) {
                maxRowHeight = Math.max(maxRowHeight, cell.getContent().getPaddedHeight());
            }
            rowHeights.add(maxRowHeight);
        }
        return rowHeights;
    }

    private ArrayList<Integer> getColumnWidths() {
        ArrayList<Integer> columnWidths = new ArrayList<Integer>();
        for(List<TableCell> row : cellMatrix) {
            for(int col=0; col<row.size(); col++) {
                TableCell cell = row.get(col);

                if(col >= columnWidths.size()) {
                    columnWidths.add(0);
                }
                int maxColWidth = Math.max(columnWidths.get(col), cell.getContent().getPaddedWidth());
                columnWidths.set(col,maxColWidth);
            }
        }
        return columnWidths;
    }

    private void setCellSizesAndPositions(ArrayList<Integer> rowHeights, ArrayList<Integer> colWidths) {
        int cellSpacing = getCellSpacing();

        int curY = cellSpacing;
        for(int row=0; row< cellMatrix.size(); row++) {
            int curX = cellSpacing;
            for(int col=0; col< cellMatrix.get(row).size(); col++) {
                TableCell cell = cellMatrix.get(row).get(col);
                cell.setCellSize(rowHeights.get(row), colWidths.get(col));
                cell.setPosition(curX, curY);

                curX += colWidths.get(col) + cellSpacing;
            }
            curY += rowHeights.get(row) + cellSpacing;
        }
    }

    private int getCellSpacing() {
        int cellSpacing = 4;
        if(style.getBorderCollapse() == Style.BorderCollapse.COLLAPSE) {
            cellSpacing = 0;
        }
        return cellSpacing;
    }

    @Override
    public int getIntrinsicWidth() {
        int result = 0;
        ArrayList<Integer> colWidths = getColumnWidths();
        for(Integer colWidth : colWidths) {
            result += colWidth;
        }
        result += getCellSpacing() * (colWidths.size() + 1);
        return result;
    }

    @Override
    public int getIntrinsicHeight() {
        int result = 0;
        ArrayList<Integer> rowHeights = getRowHeights();
        for(Integer rowHeight : rowHeights) {
            result += rowHeight;
        }
        result += getCellSpacing() * (rowHeights.size() + 1);
        return result;
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return 0;
    }


    private class BorderPriorityComparator implements Comparator<Border> {

        @Override
        public int compare(Border cell1, Border cell2) {
            int priority1 = getPriority(cell1);
            int priority2 = getPriority(cell2);

            if(priority1 < priority2)
                return -1;
            if(priority1 > priority2)
                return 1;
            return 0;
        }

        private int getPriority(Border border) {
            Style.BorderStyle borderStyle = border.getBorderStyle();
            if(borderStyle == Style.BorderStyle.SOLID || borderStyle == Style.BorderStyle.DOUBLE)
                return 4;
            if(borderStyle == Style.BorderStyle.DASHED)
                return 3;
            if(borderStyle == Style.BorderStyle.DOTTED)
                return 2;
            // no border
            return 1;
        }

    }

}
