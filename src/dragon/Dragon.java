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



	public static long timeDiff(final WayPoint p1, final WayPoint p2)
	{
		final long oldTime = p1.getTime().get().toEpochSecond();
		final long time = p2.getTime().get().toEpochSecond();

		return time - oldTime;
	}


	public static void main(final String[] args) {
		final GraphDrawer gd = new GraphDrawer("Dan", "Fred", "Searcher");


		final SearchStarts searchStarts = new SearchStarts();

		final PaintRaces pr = new PaintRaces(searchStarts.starts, searchStarts.ends, gd, 2);




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

		index++;
		dragon.addTrack("activity_3454859070.gpx", searchStarts);

		final SearchRaces races_searcher = new SearchRaces();

		dragon.addTrack("activity_3454859070.gpx", races_searcher);


		int counter = 10;

		for (final Race race : races_searcher.races) {

			if (counter--==0) return;

			final GraphDrawer gd2 = new GraphDrawer();
			WayPoint oldPoint = null;

			for (final WayPoint point : race.points) {

				if (oldPoint!=null)
				{

					final double distance = point.distance(oldPoint).doubleValue();

					final long oldTime = oldPoint.getTime().get().toEpochSecond();
					final long time = point.getTime().get().toEpochSecond();

					final long timeDiff = time - oldTime;

					final double speed = distance/timeDiff*3.6;

					gd2.addValue(time*1000, speed, 0);
				}

				oldPoint = point;


			}

			gd2.show(false);
		}


		dragon.addTrack("activity_3454859070.gpx", pr);

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
