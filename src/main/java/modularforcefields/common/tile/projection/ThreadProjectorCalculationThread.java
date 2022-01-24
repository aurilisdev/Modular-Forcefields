package modularforcefields.common.tile.projection;

import modularforcefields.common.tile.FortronFieldStatus;
import modularforcefields.common.tile.TileFortronFieldProjector;

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
		if (!projector.isRemoved()) {
			projector.status = FortronFieldStatus.CALCULATING;
			projector.calculatedFieldPoints.clear();
			ProjectionType type = projector.getProjectionType();
			type.calculate(projector, this);
			if (isInterrupted()) {
				projector.calculatedFieldPoints.clear();
			}
			projector.calculatedSize = projector.calculatedFieldPoints.size();
			projector.status = FortronFieldStatus.PROJECTING;
		}
	}
}
