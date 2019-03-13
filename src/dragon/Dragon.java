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

		final Dragon dragon = new Dragon();


		dragon.addTrack("activity_3454859070.gpx", consumer);
		index++;
		dragon.addTrack("GPSdata.2019.03.12.gpx", consumer);

		gd.show(true);
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
