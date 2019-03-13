package dragon;

import java.io.IOException;
import java.util.function.Consumer;

import hk.warp.apps.GraphDrawer;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class Dragon {

	static int index = 0;
	public static void main(final String[] args) {
		try {

			final GraphDrawer gd = new GraphDrawer("Dan", "Fred");




			final Consumer<WayPoint> consumer = new Consumer() {
				WayPoint oldPoint = null;

				@Override
				public void accept(final Object t) {
					final WayPoint point = (WayPoint) t;
					// TODO Auto-generated method stub
					if (oldPoint!=null)
					{

						final double distance = point.distance(oldPoint).doubleValue();

						final long oldTime = oldPoint.getTime().get().toEpochSecond();
						final long time = point.getTime().get().toEpochSecond();

						final long timeDiff = time - oldTime;

						final double speed = distance/timeDiff*3.6;

						gd.addValue(time*1000, speed, index);
					}

					oldPoint = point;
				}
			};

			final Dragon dragon = new


					index++;

			GPX.read("GPSdata.2019.03.12.gpx").tracks()
			//			GPX.read("activity_3454859070.gpx").tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.forEachOrdered(consumer);

			gd.show(true);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * processes a file to the consumer
	 * @param filename a Filename with ending gpx
	 * @param consumer Consumer that processes the data
	 */
	public void addTrack(final String filename, final Consumer<? super WayPoint> consumer)
	{
		try {
			GPX.read(filename).tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.forEachOrdered(consumer);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
