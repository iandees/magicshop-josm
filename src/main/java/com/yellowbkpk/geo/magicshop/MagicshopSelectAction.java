package com.yellowbkpk.geo.magicshop;

import static com.yellowbkpk.geo.magicshop.MagicshopPlugin.latlon2eastNorth;
import static org.openstreetmap.josm.tools.I18n.marktr;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.AWTEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.mapmode.MapMode;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.MapViewPaintable;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Shortcut;

@SuppressWarnings("serial")
public class MagicshopSelectAction extends MapMode implements
        MapViewPaintable, AWTEventListener {
    enum Mode {
        None, FirstPoint, SecondPoint
    }

    final private Cursor cursorCrosshair;
    private Cursor currCursor;

    private Mode mode = Mode.None;

    private Color selectedColor;
	private EastNorth firstPoint;
	private EastNorth secondPoint;

    public MagicshopSelectAction(MapFrame mapFrame) {
        super(tr("Magicshop Mode"), "magicshop", tr("Magicshop Mode"),
                Shortcut.registerShortcut("mapmode:magicshop", tr("Mode: {0}",
                        tr("Magicshop Mode")), KeyEvent.VK_M,
                        Shortcut.GROUP_EDIT), mapFrame, getCursor());

        cursorCrosshair = getCursor();
        currCursor = cursorCrosshair;

        selectedColor = Main.pref.getColor(marktr("selected"), Color.red);
    }

    private static Cursor getCursor() {
        try {
            return ImageProvider.getCursor("crosshair", null);
        } catch (Exception e) {
        }
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }

    /**
     * Displays the given cursor instead of the normal one
     * 
     * @param Cursors
     *            One of the available cursors
     */
    private void setCursor(final Cursor c) {
        if (currCursor.equals(c))
            return;
        try {
            // We invoke this to prevent strange things from happening
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    // Don't change cursor when mode has changed already
                    if (!(Main.map.mapMode instanceof MagicshopSelectAction))
                        return;
                    Main.map.mapView.setCursor(c);
                }
            });
            currCursor = c;
        } catch (Exception e) {
        }
    }

    @Override
    public void enterMode() {
        super.enterMode();
        if (getCurrentDataSet() == null) {
            Main.map.selectSelectTool(false);
            return;
        }
        currCursor = cursorCrosshair;
        Main.map.mapView.addMouseListener(this);
        Main.map.mapView.addMouseMotionListener(this);
        Main.map.mapView.addTemporaryLayer(this);
        try {
            Toolkit.getDefaultToolkit().addAWTEventListener(this,
                    AWTEvent.KEY_EVENT_MASK);
        } catch (SecurityException ex) {
        }
    }

    @Override
    public void exitMode() {
        super.exitMode();
        Main.map.mapView.removeMouseListener(this);
        Main.map.mapView.removeMouseMotionListener(this);
        Main.map.mapView.removeTemporaryLayer(this);
        try {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        } catch (SecurityException ex) {
        }
        if (mode != Mode.None)
            Main.map.mapView.repaint();
        mode = Mode.None;
    }

    public void eventDispatched(AWTEvent arg0) {
    }

    public void paint(Graphics2D g, MapView mv, Bounds bbox) {
        if (mode == Mode.None)
            return;

        g.setColor(selectedColor);
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND,
        		BasicStroke.JOIN_ROUND));
        
		if (firstPoint != null) {
			Point pt1 = mv.getPoint(firstPoint);
			g.drawOval(pt1.x, pt1.y, 3, 3);
		}
		
		if (secondPoint != null) {
			Point pt2 = mv.getPoint(secondPoint);
			g.drawOval(pt2.x, pt2.y, 3, 3);
		}

        g.setStroke(new BasicStroke(1));

    }

	@Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1)
            return;
        if (!Main.map.mapView.isActiveLayerDrawable())
            return;

        if (mode == Mode.None) {
            firstPoint = latlon2eastNorth(Main.map.mapView.getLatLon(
                    e.getX(), e.getY()));
            Main.map.mapView.repaint();
            mode = Mode.FirstPoint;
        } else if(mode == Mode.FirstPoint) {
            secondPoint = latlon2eastNorth(Main.map.mapView.getLatLon(
                    e.getX(), e.getY()));
            Main.map.mapView.repaint();
            mode = Mode.SecondPoint;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    private void updCursor() {
        if (!Main.isDisplayingMapView())
            return;

        setCursor(cursorCrosshair);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!Main.map.mapView.isActiveLayerDrawable())
            return;
        updCursor();
        if (mode != Mode.None)
            Main.map.mapView.repaint();
    }

    @Override
    public String getModeHelpText() {
        if (mode == Mode.None)
            return tr("Click to place first point");
        if (mode == Mode.FirstPoint)
            return tr("Click to place second point");
        return "";
    }

    @Override
    public boolean layerIsSupported(Layer l) {
        return l instanceof OsmDataLayer;
    }

}
