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

package org.mapfish.print.map;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfLayer;
import org.mapfish.print.*;
import org.mapfish.print.map.readers.MapReader;
import org.mapfish.print.utils.PJsonArray;
import org.mapfish.print.utils.PJsonObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Special drawer for map chunks.
 */
public class MapChunkDrawer extends ChunkDrawer {
    private final Transformer transformer;
    private final double overviewMap;
    private final PJsonObject params;
    private final RenderingContext context;
    private final Color backgroundColor;
    private final String name;


    public MapChunkDrawer(PDFCustomBlocks customBlocks, Transformer transformer, double overviewMap, PJsonObject params, RenderingContext context, Color backgroundColor, String name) {
        super(customBlocks);
        this.transformer = transformer;
        this.overviewMap = overviewMap;
        this.params = params;
        this.context = context;
        this.backgroundColor = backgroundColor;
        this.name = computeName(overviewMap, name);
    }

    private static String computeName(double overviewMap, String name) {
        if (name != null) {
            return name;
        } else {
            return (Double.isNaN(overviewMap) ? "map" : "overview");
        }
    }

    public void renderImpl(Rectangle rectangle, PdfContentByte dc) {
        final PJsonObject parent = (PJsonObject) params.getParent().getParent();
        final PJsonArray layers = parent.getJSONArray("layers");
        String srs = parent.getString("srs");

        if (!context.getConfig().isScalePresent(transformer.getScale())) {
            throw new InvalidJsonValueException(params, "scale", transformer.getScale());
        }

        Transformer mainTransformer = null;
        if (!Double.isNaN(overviewMap)) {
            //manage the overview map
            mainTransformer = context.getLayout().getMainPage().getMap().createTransformer(context, params);
            transformer.zoom(mainTransformer, (float) (1.0 / overviewMap));
            transformer.setRotation(0);   //overview always north up!
            context.setStyleFactor((float) (transformer.getPaperW() / mainTransformer.getPaperW() / overviewMap));
        }

        transformer.setMapPos(rectangle.getLeft(), rectangle.getBottom());
        if (rectangle.getWidth() < transformer.getPaperW() - 0.2) {
            throw new RuntimeException("The map width on the paper is wrong");
        }
        if (rectangle.getHeight() < transformer.getPaperH() - 0.2) {
            throw new RuntimeException("The map height on the paper is wrong (" + rectangle.getHeight() + "!=" + transformer.getPaperH() + ")");
        }

        //create the readers/renderers
        List<MapReader> readers = new ArrayList<MapReader>(layers.size());
        for (int i = 0; i < layers.size(); ++i) {
            PJsonObject layer = layers.getJSONObject(i);
            if (mainTransformer == null || layer.optBool("overview", true)) {
                final String type = layer.getString("type");
                MapReader.create(readers, type, context, layer);
            }
        }

        //check if we cannot merge a few queries
        for (int i = 1; i < readers.size();) {
            MapReader reader1 = readers.get(i - 1);
            MapReader reader2 = readers.get(i);
            if (reader1.testMerge(reader2)) {
                readers.remove(i);
            } else {
                ++i;
            }

        }

        //draw some background
        if (backgroundColor != null) {
            dc.saveState();
            try {
                dc.setColorFill(backgroundColor);
                dc.rectangle(rectangle.getLeft(), rectangle.getBottom(), rectangle.getWidth(), rectangle.getHeight());
                dc.fill();
            } finally {
                dc.restoreState();
            }
        }

        //do the rendering
        dc.saveState();
        try {
            PdfLayer mapLayer = new PdfLayer(name, context.getWriter());
            transformer.setClipping(dc);
            for (int i = 0; i < readers.size(); i++) {
                MapReader reader = readers.get(i);
                PdfLayer pdfLayer = new PdfLayer(reader.toString(), context.getWriter());
                mapLayer.addChild(pdfLayer);
                dc.beginLayer(pdfLayer);
                reader.render(transformer, dc, srs, i == 0);
                dc.endLayer();
            }
        } finally {
            dc.restoreState();
        }


        if (mainTransformer != null) {
            //only for key maps: draw the real map extent
            drawMapExtent(dc, mainTransformer);
            context.setStyleFactor(1.0f);
        }
    }

    /**
     * Used by overview maps to draw the extent of the real map.
     */
    private void drawMapExtent(PdfContentByte dc, Transformer mainTransformer) {
        dc.saveState();
        try {
            //in "degrees" unit, there seems to have rounding errors if I use the
            //PDF transform facility. Therefore, I do the transform by hand :-(
            transformer.setRotation(mainTransformer.getRotation());
            dc.transform(transformer.getGeoTransform(true));
            transformer.setRotation(0);

            dc.setLineWidth(1 * transformer.getGeoW() / transformer.getPaperW());
            dc.setColorStroke(new Color(255, 0, 0));
            dc.rectangle(mainTransformer.getMinGeoX(), mainTransformer.getMinGeoY(), mainTransformer.getGeoW(), mainTransformer.getGeoH());
            dc.stroke();

            if (mainTransformer.getRotation() != 0.0) {
                //draw a little arrow
                dc.setLineWidth(0.5F * transformer.getGeoW() / transformer.getPaperW());
                dc.moveTo((3 * mainTransformer.getMinGeoX() + mainTransformer.getMaxGeoX()) / 4,
                        mainTransformer.getMinGeoY());
                dc.lineTo((mainTransformer.getMinGeoX() + mainTransformer.getMaxGeoX()) / 2,
                        (mainTransformer.getMinGeoY()*2 + mainTransformer.getMaxGeoY()) / 3);
                dc.lineTo((mainTransformer.getMinGeoX() + 3 * mainTransformer.getMaxGeoX()) / 4,
                        mainTransformer.getMinGeoY());
                dc.stroke();
            }
        } finally {
            dc.restoreState();
        }
    }
}
