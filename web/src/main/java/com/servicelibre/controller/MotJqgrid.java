package com.servicelibre.controller;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.liste.Mot;

public class MotJqgrid  {
	
	int page;
	int total;
	int records;
	
	List<Row> rows;
	
	public MotJqgrid(int page, int total, int records, List<Mot> rows) {
		this.page = page;
		this.total = total;
		this.records = records;
		
		this.rows = new ArrayList<Row>(rows.size());
		for(Mot mot : rows){
			this.rows.add(new Row(mot));
		}
	
	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getRecords() {
		return records;
	}
	public void setRecords(int records) {
		this.records = records;
	}
	


	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	
	
}
