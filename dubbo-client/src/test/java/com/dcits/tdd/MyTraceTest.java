package com.dcits.tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.jvm.hotspot.utilities.Assert;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTraceTest {


	@Test
	void startTDD() {

	}

	@Test
	void stopTDD() {

		assertEquals(5,5);

	}

	@BeforeEach
	void setUp() {
		MyTrace trace = new MyTrace();
		String str = trace.getHelloStr();

	}
}
