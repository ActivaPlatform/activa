package com.o2hlink.activa.notes;

import java.util.Date;

public class Note {
	
	public long id;
	
	public String text;
	
	public Date date;
	
	public Note(int id) {
		this.id = id;
	}
	
	public Note(int id, String text) {
		this.id = id;
		this.text = text;
	}
	
	public Note(long l, String text, Date date) {
		this.id = l;
		this.text = text;
		this.date = date;
	}
	
}