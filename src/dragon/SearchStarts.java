package dragon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.jenetics.jpx.WayPoint;

public class SearchStarts implements Consumer{

	WayPoint oldPoint = null;
	WayPoint belowThreshold;

	List<WayPoint> starts = new ArrayList<>();
	List<WayPoint> ends = new ArrayList<>();

	boolean started = false;
	static double threshold = 1.5;
	static double threshold2 =10;

	static int seconds = 20;

	@Override
	public void accept(final Object t) {
		final WayPoint point = (WayPoint) t;
		// TODO Auto-generated method stub
		if (oldPoint!=null)
		{

			final double distance = point.distance(oldPoint).doubleValue();
			final long timeDiff = timeDiff(oldPoint, point);

			final double speed = distance/timeDiff*3.6;

			if (speed<threshold)
			{
				if (started)
				{
					ends.add(point);
					started = false;
				}
				belowThreshold = point;
			}
			else if (speed>threshold2 && !started)
			{
				if (belowThreshold!=null)
				{
					final long timeDiff2 = timeDiff(belowThreshold, point);
					if (timeDiff2< seconds)
					{
						starts.add(belowThreshold);
						started = true;
					}
				}
			}

		}

		oldPoint = point;
	}



	public static long timeDiff(final WayPoint p1, final WayPoint p2)
	{
		final long oldTime = p1.getTime().get().toEpochSecond();
		final long time = p2.getTime().get().toEpochSecond();

		return time - oldTime;
	}


}
