package com.dcits.tdd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.jvm.hotspot.utilities.Assert;
import com.dcits.tdd.MyTrace;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTraceTest {


	@Test
	void startTDD() {

	}

	@Test
	void stopTDD() {
		MyTrace trace = new MyTrace();
		String str = trace.getHelloStr();
		assertEquals("hello",str);

	}

	@BeforeEach
	void setUp() {
		MyTrace trace = new MyTrace();
		String str = trace.getHelloStr();

	}
}
