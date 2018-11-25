package fastOrForcedToFollow;


import org.matsim.core.mobsim.qsim.qnetsimengine.QCycle;
import org.matsim.core.mobsim.qsim.qnetsimengine.QVehicle;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * 
 * @author mpaulsen
 */
public final class Link{

	
	/**
	 * The id of the link.
	 */
	private final String id;

	
	/**
	 * The total pseudolane distance (buffer distance) occupied of the cyclists currently on the link.
	 */
	private double occupiedSpace = 0;


	/**
	 * The Q containing the CQO's for the cyclists that have entered the link but not yet left it.
	 */
	private final PriorityQueue<QCycle> outQ;

	/**
	 * The array containing all pseudolanes of the link.
	 */
	private final PseudoLane[] psi;


	/**
	 * The total pseudolane distance, i.e. the product between the length and number of pseudolanes.
	 */
	private final double totalLaneLength; 

	/**
	 * The last time a vehicle was moved through the downstream end of the link.
	 */
	private double lastTimeMoved;
	
	
	/**
	 * A LinkedList containing the vehicles which will be leaving the link. 
	 */
	private final LinkedList<QVehicle> leavingVehicles;
	
	
	public void addVehicleToLeavingVehicles(final QVehicle veh){
		this.leavingVehicles.addLast(veh);
	}
	public QVehicle getFirstLeavingVehicle(){
		return leavingVehicles.isEmpty() ? null : leavingVehicles.peekFirst();
	}
	public QVehicle pollFirstLeavingVehicle(){
		return leavingVehicles.isEmpty() ? null : leavingVehicles.pollFirst();
	}
	public boolean hasNoLeavingVehicles(){
		return leavingVehicles.isEmpty();
	}
	

	/**
	 * Static factory method creating a link based on the width of the link. See also the {@link #Link(String, int, double) constructor}.
	 */
	public static Link createLinkFromWidth(final String id, final double width, final double length){
		return new Link(id, 1 + (int) Math.floor((width-Runner.DEAD_SPACE)/Runner.OMEGA), length );
	}
	
	/**
	 * Static factory method creating a link based directly on the number of pseudolanes of the link. See also the {@link #Link(String, int, double) constructor}.
	 */
	public static Link createLinkFromNumberOfPseudoLanes(final String id, final int Psi, final double length){
		return new Link(id, Psi, length);
	}
	
	/**
	 * @param id The id of the link.
	 * @param Psi The number of pseudolanes that the link has.
	 * @param length The length [m] of the link.
	 */
	private Link(final String id, final int Psi, final double length){
		this.id = id;
		this.psi = createPseudoLanes(Psi, length);

		this.outQ = new PriorityQueue<>( new Comparator<QCycle>(){
			@Override
			public int compare( QCycle cqo1, QCycle cqo2 ){
				return Double.compare(cqo1.getCyclist().getTEarliestExit(), cqo2.getCyclist().getTEarliestExit());
			}
		} ) ;
		
		this.leavingVehicles = new LinkedList<QVehicle>();

		double totalLaneLength = 0.;
		for(PseudoLane pseudoLane : psi){
			totalLaneLength += pseudoLane.getLength();
		}
		this.totalLaneLength = totalLaneLength;
		
	}

	/**
	 * @return The array of PseudoLanes to be created
	 */
	private PseudoLane[] createPseudoLanes(final int Psi, final double length){
		PseudoLane[] psi = new PseudoLane[Psi];
		for(int i = 0; i < Psi; i++){
			psi[i] = new PseudoLane(length);
		}
		return psi;
	}

	
	public String getId(){
		return id;
	}


	public int getNumberOfPseudoLanes(){
		return psi.length;
	}


	public PriorityQueue<QCycle> getOutQ(){
		return outQ;
	}

	/**
	 * Gets a specific <code>pseudolane</code> from the <code>link</code>.
	 * 
	 * @param i the index where 0 is the rightmost <code>pseudolane</code>, and <code>Psi</code> - 1 is the leftmost.
	 * 
	 * @return The i'th <code>pseudolane</code> from the right (0 being the rightmost).
	 */
	public PseudoLane getPseudoLane(final int i){
		return psi[i];
	}

	
	/**
	 * @return <code>true</code> iff the link is full, i.e. the occupied space is at least as large as the total lane length.
	 */
	public boolean isLinkFull(){
		return occupiedSpace >= totalLaneLength;
	}

	/**
	 * Reduces the occupied space of link by the safety distance corresponding to <code>speed</code> of <code>cyclist</code>.
	 * 
	 * @param cyclist The cyclist which is removed from the link.
	 * @param speed The speed the cyclist was assigned to the link.
	 */
	public void reduceOccupiedSpace(final Cyclist cyclist, final double speed){
		this.supplementOccupiedSpace(-cyclist.getSafetyBufferDistance(speed));
	}

	/**
	 * Increases the occupied space of link by the safety distance corresponding to <code>speed</code> of <code>cyclist</code>.
	 * 
	 * @param cyclist The cyclist which enters the link.
	 * @param speed The speed the cyclist is assigned to the link.
	 */

	public void increaseOccupiedSpace(final Cyclist cyclist, final double speed){
		this.supplementOccupiedSpace(cyclist.getSafetyBufferDistance(speed));
	}


	/**
	 * @param length The length [m] by which the occupied space will be supplemented.
	 */
	public void supplementOccupiedSpace(final double length){
		occupiedSpace += length;
	}

	
	
	public double getLastTimeMoved(){
		return this.lastTimeMoved;
	}
	
	public void setLastTimeMoved(final double lastTimeMoved){
		this.lastTimeMoved = lastTimeMoved;
	}

	
	/**
	 * Static factory method creating a link based directly on the number of pseudolanes of the link. See also the {@link #Link(String, int, double) constructor}.
	 */
	public static Link[] createLinkArrayFromNumberOfPseudoLanes(final String id, final int Psi, final double length){
		int N = (int) Math.ceil(length / Runner.L_MAX);
		Link[] linkArray = new Link[N];
		for(int i = 0; i < linkArray.length; i++){
			linkArray[i] = new Link(id + "_part_" + (i+1) , Psi, length/N);
		}
		return linkArray;
	}
}
