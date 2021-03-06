/*
 * Copyright (C) 2008  Camptocamp
 *
 * This file is part of MapFish Server
 *
 * MapFish Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MapFish Server.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mapfish.print.config.layout;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.mapfish.print.PDFUtils;
import org.mapfish.print.RenderingContext;
import org.mapfish.print.utils.PJsonArray;
import org.mapfish.print.utils.PJsonObject;

import java.net.URI;

public class LegendsBlock extends Block {
    private double maxIconWidth = 8f;
    private double maxIconHeight = 8f;
    private double classIndentation = 20f;
    private double layerSpace = 5f;
    private double classSpace = 2f;

    private String layerFont = "Helvetica";
    protected double layerFontSize = 10f;
    private String classFont = "Helvetica";
    protected double classFontSize = 8f;
    private String fontEncoding = BaseFont.WINANSI;

    public void render(PJsonObject params, PdfElement target, RenderingContext context) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100f);

        Font layerPdfFont = getLayerPdfFont();
        Font classPdfFont = getClassPdfFont();

        PJsonArray legends = context.getGlobalParams().optJSONArray("legends");
        if (legends != null && legends.size() > 0) {
            for (int i = 0; i < legends.size(); ++i) {
                PJsonObject layer = legends.getJSONObject(i);
                final PdfPCell cell = createLine(context, 0.0, layer, layerPdfFont, params);
                if (i > 0) {
                    cell.setPaddingTop((float) layerSpace);
                }
                table.addCell(cell);

                PJsonArray classes = layer.getJSONArray("classes");
                for (int j = 0; j < classes.size(); ++j) {
                    PJsonObject clazz = classes.getJSONObject(j);
                    final PdfPCell classCell = createLine(context, classIndentation, clazz, classPdfFont, params);
                    classCell.setPaddingTop((float) classSpace);
                    table.addCell(classCell);
                }
            }
        }
        table.setSpacingAfter((float) spacingAfter);
        target.add(table);
    }

    private PdfPCell createLine(RenderingContext context, double indent, PJsonObject node, Font pdfFont, PJsonObject params) throws DocumentException {
        final String name = node.getString("name");
        final String icon = node.optString("icon");

        final Paragraph result = new Paragraph();
        result.setFont(pdfFont);
        if (icon != null) {
            result.add(PDFUtils.createImageChunk(context, maxIconWidth, maxIconHeight, URI.create(icon), 0.0f));
            result.add(" ");
        }
        result.add(name);

        final PdfPCell cell = new PdfPCell(result);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(0f);
        cell.setPaddingLeft((float) indent);

        if (getBackgroundColorVal(context, params) != null) {
            cell.setBackgroundColor(getBackgroundColorVal(context, params));
        }

        return cell;
    }

    public void setMaxIconWidth(double maxIconWidth) {
        this.maxIconWidth = maxIconWidth;
    }

    public void setMaxIconHeight(double maxIconHeight) {
        this.maxIconHeight = maxIconHeight;
    }

    public void setClassIndentation(double classIndentation) {
        this.classIndentation = classIndentation;
    }

    public void setClassFont(String classFont) {
        this.classFont = classFont;
    }

    public void setClassFontSize(double classFontSize) {
        this.classFontSize = classFontSize;
    }

    public String getClassFont() {
        return classFont;
    }

    protected Font getLayerPdfFont() {
        return FontFactory.getFont(layerFont, fontEncoding, (float) layerFontSize);
    }

    protected Font getClassPdfFont() {
        return FontFactory.getFont(classFont, fontEncoding, (float) classFontSize);
    }

    public void setLayerSpace(double layerSpace) {
        this.layerSpace = layerSpace;
    }

    public void setClassSpace(double classSpace) {
        this.classSpace = classSpace;
    }

    public void setLayerFont(String layerFont) {
        this.layerFont = layerFont;
    }

    public void setLayerFontSize(double layerFontSize) {
        this.layerFontSize = layerFontSize;
    }

    public void setFontEncoding(String fontEncoding) {
        this.fontEncoding = fontEncoding;
    }
}
