package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class OutputText {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String url;
	private String content;
	
	public OutputText() {	
	}
	
	public OutputText (String url) {
		super();
		this.url = url;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "OutputText [id=" + id + ", url=" + url + ", content=" + content + "]";
	}
	
}
