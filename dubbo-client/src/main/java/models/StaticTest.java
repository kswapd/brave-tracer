package models;


/**
 * Created by kongxiangwen on 5/21/18 w:21.
 */
public  class StaticTest {
	private static int a = 0;
	public static class stest
	{
		public static void sprt(){
			System.out.println("ggg"+a);
			a ++;
		}

		public  void prt(){
			System.out.println("ggg"+a);
			a ++;
		}

	}

}
