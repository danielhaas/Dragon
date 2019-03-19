package dragon;

import java.util.ArrayList;
import java.util.List;

import io.jenetics.jpx.WayPoint;

public class Race {

	List<WayPoint> points = new ArrayList<>();

	public void addPoint(final WayPoint wayPoint) {
		points.add(wayPoint);
	}

}
