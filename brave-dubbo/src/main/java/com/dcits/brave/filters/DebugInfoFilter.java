package com.dcits.brave.filters;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by kongxiangwen on 7/13/18 w:28.
 */
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
public class DebugInfoFilter implements Filter {


	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		long start = System.currentTimeMillis();
		Result result = invoker.invoke(invocation);
		long elapsed = System.currentTimeMillis() - start;
		if (invoker.getUrl() != null) {

			// log.info("[" +invoker.getInterface() +"] [" + invocation.getMethodName() +"] [" + elapsed +"]" );
			System.out.println(String.format("------thread:[%d], [%s], [%s], {%s}, [%s], [%s], [%d]  ", Thread.currentThread().getId(), invoker.getInterface(), invocation.getMethodName(),
					Arrays.toString(invocation.getArguments()), result.getValue(),
					result.getException(), elapsed));

		}
		return result;
	}


}

