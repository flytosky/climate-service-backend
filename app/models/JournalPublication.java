package models;

import javax.persistence.Entity;

@Entity
public class JournalPublication extends Publication{
	private String journalName;
	private int volume;
	private int column;
	private String page;
	
	public JournalPublication() {
		super();
	}
	public JournalPublication(String paperTitle, User author,
			String publicationChannel, int year, String journalName, int volume,
			int column, String page) {
		super(paperTitle, author, publicationChannel, year);
		this.journalName = journalName;
		this.volume = volume;
		this.column = column;
		this.page = page;
	}
	
	public String getJournalName() {
		return journalName;
	}
	public void setJournalName(String journalName) {
		this.journalName = journalName;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	
	@Override
	public String toString() {
		return "JournalPublication [journalName=" + journalName + ", volume="
				+ volume + ", column=" + column + ", page=" + page
				+ ", toString()=" + super.toString() + "]";
	}
}
