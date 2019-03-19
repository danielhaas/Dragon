package dragon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.jenetics.jpx.WayPoint;

public class SearchRaces implements Consumer{


	WayPoint oldPoint = null;
	WayPoint belowThreshold;


	List<Race> races = new ArrayList<>();
	List<WayPoint> buffer = new ArrayList<>();

	boolean started = false;
	static double threshold = 1.5;
	static double threshold2 =10;

	static int seconds = 20;

	Race myRace;

	@Override
	public void accept(final Object t) {
		final WayPoint point = (WayPoint) t;
		if (oldPoint!=null)
		{

			final double distance = point.distance(oldPoint).doubleValue();
			final long timeDiff = timeDiff(oldPoint, point);

			final double speed = distance/timeDiff*3.6;

			if (speed<threshold)
			{
				if (started)
				{
					races.add(myRace);
					started = false;
				}
				belowThreshold = point;
				buffer.clear();
			}
			else if (speed>threshold2 && !started)
			{
				if (belowThreshold!=null)
				{
					final long timeDiff2 = timeDiff(belowThreshold, point);
					if (timeDiff2< seconds)
					{
						myRace = new Race();

						for (final WayPoint wayPoint : buffer) {
							myRace.addPoint(wayPoint);
						}
						started = true;
					}
				}
			}
			else
			{
				buffer.add(point);
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
