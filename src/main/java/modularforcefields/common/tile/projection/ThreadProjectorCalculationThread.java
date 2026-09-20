package modularforcefields.common.tile.projection;

import modularforcefields.common.tile.FortronFieldStatus;
import modularforcefields.common.tile.TileFortronFieldProjector;
import net.minecraft.world.level.Level;

public class ThreadProjectorCalculationThread extends Thread {
    private final TileFortronFieldProjector projector;

    public ThreadProjectorCalculationThread(TileFortronFieldProjector projector) {
	this.projector = projector;
	setName("Fortron Field Calculation Thread");
    }

    public TileFortronFieldProjector getProjector() {
	return projector;
    }

    @Override
    public void run() {
	if (!projector.isRemoved() && projector.getLevel() instanceof Level level) {
	    projector.setStatus(FortronFieldStatus.CALCULATING);
	    projector.calculatedFieldPoints.clear();
	    ProjectionType type = projector.getProjectionType();
	    type.calculate(level, projector, this);
	    if (isInterrupted()) {
		projector.calculatedFieldPoints.clear();
		return;
	    }

	    projector.calculatedSize.setValue(projector.calculatedFieldPoints.size());

	    projector.setStatus(FortronFieldStatus.PROJECTING);
	}
    }
}
