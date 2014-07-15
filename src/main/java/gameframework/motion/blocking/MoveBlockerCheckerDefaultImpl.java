package gameframework.motion.blocking;

import gameframework.motion.IntersectTools;
import gameframework.motion.Movable;
import gameframework.motion.SpeedVector;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MoveBlockerCheckerDefaultImpl implements MoveBlockerChecker {
	private ConcurrentLinkedQueue<MoveBlocker> moveBlockers;
	private MoveBlockerRulesApplier moveBlockerRuleApplier;

	public MoveBlockerCheckerDefaultImpl() {
		moveBlockers = new ConcurrentLinkedQueue<MoveBlocker>();
		this.moveBlockerRuleApplier = new MoveBlockerRulesApplierDefaultImpl();
	}

	public void addMoveBlocker(MoveBlocker p) {
		moveBlockers.add(p);
	}

	public void removeMoveBlocker(MoveBlocker p) {
		moveBlockers.remove(p);
	}

	public void setMoveBlockerRules(MoveBlockerRulesApplier moveBlockerRules) {
		this.moveBlockerRuleApplier = moveBlockerRules;
	}

	public boolean moveValidation(Movable m, SpeedVector mov) {
		Shape intersectShape = IntersectTools.getIntersectShape(m, mov);
		Vector<MoveBlocker> moveBlockersInIntersection = new Vector<MoveBlocker>();
		Area intersectArea = new Area(intersectShape);
		Rectangle tmpIntersec = (intersectShape.getBounds());

		for (MoveBlocker moveBlocker : moveBlockers) {
			Rectangle tmpB = moveBlocker.getBoundingBox();
			if (tmpIntersec.intersects(tmpB)) {
				Area tmpArea = new Area(tmpB);
				tmpArea.intersect(intersectArea);
				if (!tmpArea.isEmpty()) { // I don't understand how this test
											// can fail. To me, if
											// tmpIntersec intersects with tmpB
											// then tmpArea is not empty
					moveBlockersInIntersection.add(moveBlocker);
				}
			}
		}

		if (!moveBlockersInIntersection.isEmpty()) {
			return moveBlockerRuleApplier.moveValidationProcessing(
					m, moveBlockersInIntersection);
		}

		return true;
	}
}
