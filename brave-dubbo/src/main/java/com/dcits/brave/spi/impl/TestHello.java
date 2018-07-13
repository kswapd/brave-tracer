package com.dcits.brave.spi.impl;

import com.dcits.brave.spi.interfaces.ITestHello;
import javax.imageio.ImageTranscoder;

/**
 * Created by kongxiangwen on 7/13/18 w:28.
 */
public class TestHello implements ITestHello {
	@Override
	public void sayHi(){
		System.out.println("Hi from:"+this.getClass().getSimpleName());
	}
}
