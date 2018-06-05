package br.com.broker.signal.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Shopping {

	public Shopping(){}
	
	public Shopping(Integer position, Long buyPrice, Long sellPrice, Long doublePositionPrice){
		this.position = position;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.doublePositionPrice = doublePositionPrice;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private Integer position;
	
	private Long buyPrice;
	
	private Long sellPrice;
	
	private Long doublePositionPrice;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Long getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(Long buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Long getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Long sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Long getDoublePositionPrice() {
		return doublePositionPrice;
	}

	public void setDoublePositionPrice(Long doublePositionPrice) {
		this.doublePositionPrice = doublePositionPrice;
	}
	
}
