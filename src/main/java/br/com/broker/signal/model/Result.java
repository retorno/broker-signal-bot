package br.com.broker.signal.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Result {

	public Result(){}
	
	public Result(Long pointsStop, Long qtStops){
		this.pointsStop = pointsStop;
		this.qtStops = qtStops;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Long pointsStop;
	
	private Long qtStops;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPointsStop() {
		return pointsStop;
	}

	public void setPointsStop(Long pointsStop) {
		this.pointsStop = pointsStop;
	}

	public Long getQtStops() {
		return qtStops;
	}

	public void setQtStops(Long qtStops) {
		this.qtStops = qtStops;
	}
	
}
