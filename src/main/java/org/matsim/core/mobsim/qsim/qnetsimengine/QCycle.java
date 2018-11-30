package org.matsim.core.mobsim.qsim.qnetsimengine;

import fastOrForcedToFollow.Cyclist;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.mobsim.framework.DriverAgent;
import org.matsim.core.mobsim.framework.HasPerson;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.framework.PassengerAgent;
import org.matsim.run.RunMatsim;
import org.matsim.vehicles.Vehicle;

import java.util.Collection;

/**
 * The {@link QVehicle} is the thing that is passed from one link through the other, via {@link QNodeI}.  (The cycle plugin here replaces only
 *  the {@link QLinkI}.)  So we wrap the cycle dynamics (essentially generated by the {@link Cyclist}) a {@link QVehicle}, and make it available every
 *  time we have traversed a {@link QNodeI}, and just entered a new link.  We also use a {@link QVehicleImpl} as delegate for all methods which
 *  are not implemented by {@link Cyclist}.
 */
public class QCycle implements QVehicle
{

	private QVehicle qVehicle  ;
	private Cyclist cyclist ;
	
	
	/**
	 * Creates a QCycleAsVehicle based on the basicVehicle inputted. Cyclist is created later on, when the driver is set, i.e. in {@link #setDriver(DriverAgent)}
	 * @param basicVehicle
	 */
	public QCycle( Vehicle basicVehicle ) {
		this.qVehicle = new QVehicleImpl( basicVehicle ) ;
	}
	
	
	/**
	 * Sets the driver and internally creates the cyclist based on the person being the driver.
	 */
	@Override public void setDriver( final DriverAgent driver ) {
		qVehicle.setDriver( driver );

		if ( driver!=null ){   // is null when vehicle arrives, and driver LEAVES vehicle!
			Person person = ((HasPerson) driver).getPerson();
			final double v_0 = (double) person.getAttributes().getAttribute( RunMatsim.DESIRED_SPEED );
			final double theta_0 = (double) person.getAttributes().getAttribute( RunMatsim.HEADWAY_DISTANCE_INTERSECTION );
			final double theta_1 = (double) person.getAttributes().getAttribute( RunMatsim.HEADWAY_DISTANCE_SLOPE );
			final double lambda_c = (double) person.getAttributes().getAttribute( RunMatsim.BICYCLE_LENGTH );
			this.cyclist = Cyclist.createIndividualisedCyclistWithSqrtLTM(v_0, theta_0, theta_1, lambda_c);
		}

	}
	
	
	public Cyclist getCyclist() {
		return this.cyclist;
	}


	@Override public double getLinkEnterTime() {
        return qVehicle.getLinkEnterTime();
	}
	
	@Override public void setLinkEnterTime( final double linkEnterTime ) {
       qVehicle.setLinkEnterTime(linkEnterTime);
	}
	
	@Override public double getMaximumVelocity() {
		//Uses cyclist's value;
		return this.getCyclist().getDesiredSpeed();
	}
	
	@Override public double getFlowCapacityConsumptionInEquivalents() {
	     return qVehicle.getFlowCapacityConsumptionInEquivalents();
	}
	
	@Override public double getEarliestLinkExitTime() {
		//Uses cyclist's value
		return this.getCyclist().getTEarliestExit();
	}
	
	@Override public void setEarliestLinkExitTime( final double earliestLinkEndTime ) {
		//Uses cyclist's value
		this.cyclist.setTEarliestExit(earliestLinkEndTime);
	}
	
	@Override public double getSizeInEquivalents() {
		return qVehicle.getSizeInEquivalents();
	}
	
	@Override public Vehicle getVehicle() {
		return qVehicle.getVehicle();
	}
	
	@Override public MobsimDriverAgent getDriver() {
		return qVehicle.getDriver();
	}
	
	@Override public Id<Vehicle> getId() {
		return qVehicle.getId();
	}
	
	@Override public Link getCurrentLink() {
		return qVehicle.getCurrentLink();
	}
	
	@Override public boolean addPassenger( final PassengerAgent passenger ) {
		return qVehicle.addPassenger( passenger );
	}
	
	@Override public boolean removePassenger( final PassengerAgent passenger ) {
		return qVehicle.removePassenger( passenger );
	}
	
	@Override public Collection<? extends PassengerAgent> getPassengers() {
		return qVehicle.getPassengers();
	}
	
	@Override public int getPassengerCapacity() {
		return qVehicle.getPassengerCapacity();
	}
	
	@Override public void setCurrentLink( final Link link ) {
		qVehicle.setCurrentLink( link );
	}
	

	
	
}
