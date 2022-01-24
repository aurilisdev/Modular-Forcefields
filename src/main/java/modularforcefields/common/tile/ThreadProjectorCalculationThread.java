package modularforcefields.common.tile;

public class ThreadProjectorCalculationThread extends Thread {
	private TileFortronFieldProjector projector;

	public ThreadProjectorCalculationThread(TileFortronFieldProjector projector) {
		this.projector = projector;
	}

	public TileFortronFieldProjector getProjector() {
		return projector;
	}

	@Override
	public void run() {
	}
}
