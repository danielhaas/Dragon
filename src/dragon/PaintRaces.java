package dragon;

import java.util.List;
import java.util.function.Consumer;

import hk.warp.apps.GraphDrawer;
import io.jenetics.jpx.WayPoint;

public class PaintRaces implements Consumer{

	List<WayPoint> starts;
	List<WayPoint> ends;
	GraphDrawer gd;
	int index;
	WayPoint oldPoint = null;
	boolean paint = false;

	public PaintRaces(final List<WayPoint> starts_, final List<WayPoint> ends_, final GraphDrawer gd_, final int index_) {
		starts = starts_;
		ends = ends_;
		gd = gd_;
		index = index_;
	}


	@Override
	public void accept(final Object t) {
		final WayPoint point = (WayPoint) t;



		if (starts.size()>0 && starts.get(0).equals(t))
		{
			starts.remove(0);
			paint = true;
		}

		if (ends.size()>0 && ends.get(0).equals(t))
		{
			ends.remove(0);
			paint = false;
		}


		if (oldPoint!=null && paint)
		{

			final double distance = point.distance(oldPoint).doubleValue();

			final long oldTime = oldPoint.getTime().get().toEpochSecond();
			final long time = point.getTime().get().toEpochSecond();

			final long timeDiff = time - oldTime;

			final double speed = distance/timeDiff*3.6;

			gd.addValue(time*1000, speed+1, index);
		}

		oldPoint = point;
	}

}
