/*
 * Copyright (C) 2011 Alex Kuiper <http://www.nightwhistler.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nightwhistler.htmlspanner.handlers.table;

import android.graphics.*;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.handlers.StyledTextHandler;
import net.nightwhistler.htmlspanner.style.Style;
import net.nightwhistler.htmlspanner.style.StyleValue;

import org.htmlcleaner.TagNode;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;

/**
 * Handles HTML tables. It instantiates a {@link TableDrawable} from the the tag node, and
 * draws the table with an image span.
 */
public class TableHandler extends TagNodeHandler {

    @Override
    public boolean rendersContent() {
        return true;
    }

    private void readNodeIntoTableDrawable(Object node, TableDrawable table, SpanStack spanStack) {

        // We can't handle plain content nodes within the table.
        if (node instanceof TagNode) {

            TagNode tagNode = (TagNode) node;

            if (tagNode.getName().equals("td") || tagNode.getName().equals("th")) {

                HtmlSpanner spanner = this.getSpanner();
                Spanned spanned = spanner.fromTagNode(tagNode, null);

                TableCellContent cellContent = new TableCellContent();
                cellContent.setSpanned(spanned);
                Style style = spanStack.getStyle(tagNode, new Style());
                cellContent.setStyle(style);

                table.addCell(new TableCell(cellContent));
                return;
            }

            if (tagNode.getName().equals("tr")) {
                table.addRow();
            }

            for (Object child : tagNode.getAllChildren()) {
                readNodeIntoTableDrawable(child, table, spanStack);
            }
        }
    }

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder,
                              int start, int end, SpanStack spanStack) {

        TableDrawable table = new TableDrawable();
        Style tableStyle = getTableStyle(node, spanStack);
        table.setStyle(tableStyle);

        TableHeaderCellHandler headerCellHandler = new TableHeaderCellHandler();
        this.getSpanner().registerHandler("th", headerCellHandler);
        readNodeIntoTableDrawable(node, table, spanStack);
        this.getSpanner().unregisterHandler("th");

        builder.append("\uFFFC");
        table.setBounds(0, 0, table.getIntrinsicWidth(), table.getIntrinsicHeight());
        spanStack.pushSpan(new ImageSpan(table), start, builder.length());

        builder.setSpan(new AlignmentSpan() {
            @Override
            public Layout.Alignment getAlignment() {
                return Layout.Alignment.ALIGN_CENTER;
            }
        }, start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private Style getTableStyle(TagNode node, SpanStack spanStack) {
        Style style = spanStack.getStyle(node, new Style());

        String borderAttributeValue = node.getAttributeByName("border");
        if (style.getBorderStyle() == null
                && "1".equals(borderAttributeValue)) {
            style = style.setBorderCollapse(Style.BorderCollapse.SEPARATE)
                    .setBorderStyle(Style.BorderStyle.SOLID)
                    .setBorderColor(Color.BLACK)
                    .setBorderWidth(StyleValue.parse("1px"));
        }
        return style;
    }

    private class TableHeaderCellHandler extends StyledTextHandler {

        @Override
        public Style getStyle() {
            return super.getStyle()
                    .setFontWeight(Style.FontWeight.BOLD)
                    .setTextAlignment(Style.TextAlignment.CENTER);
        }
    }

}
