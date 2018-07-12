package models;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kongxiangwen on 5/18/18 w:20.
 */
@Component
public class SuperBook {
	//@Autowired
	@Resource(name="book")
	public Book bk;


	public static void test()
	{
		System.out.println("test");
	}

}
