package br.com.broker.signal.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Result {

	public Result(){}
	
	public Result(Long qtStops, Long qtWin){
		this.qtStops = qtStops;
		this.qtWin = qtWin;
	}
	
	public Result(Long qtStops, Long qtWin, Long qtStopsAux){
		this.qtStops = qtStops;
		this.qtWin = qtWin;
		this.qtStopsAux = qtStopsAux;
	}
	
	public Result(Long qtStops, Long qtWin, Long qtStopsAux, Long qtWinAux){
		this.qtStops = qtStops;
		this.qtWin = qtWin;
		this.qtStopsAux = qtStopsAux;
		this.qtWinAux = qtWinAux;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Long qtStops;
	
	private Long qtWin;
	
	private Long qtStopsAux;
	
	private Long qtWinAux;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getQtStops() {
		return qtStops;
	}

	public void setQtStops(Long qtStops) {
		this.qtStops = qtStops;
	}

	public Long getQtWin() {
		return qtWin;
	}

	public void setQtWin(Long qtWin) {
		this.qtWin = qtWin;
	}

	public Long getQtStopsAux() {
		return qtStopsAux;
	}

	public void setQtStopsAux(Long qtStopsAux) {
		this.qtStopsAux = qtStopsAux;
	}

	public Long getQtWinAux() {
		return qtWinAux;
	}

	public void setQtWinAux(Long qtWinAux) {
		this.qtWinAux = qtWinAux;
	}
	
}
