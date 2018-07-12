package models;

import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 5/18/18 w:20.
 */
@Component
public class Book {
	private String title ="test";
	private String author = "kxw";
	private int pages = 18;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
}
