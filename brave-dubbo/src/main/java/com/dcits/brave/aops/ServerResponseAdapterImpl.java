package com.dcits.brave.aops;

import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.ServerResponseAdapter;
import java.util.ArrayList;
import java.util.Collection;

class ServerResponseAdapterImpl implements ServerResponseAdapter {


	public Collection<KeyValueAnnotation> responseAnnotations() {
		Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
		KeyValueAnnotation kv = KeyValueAnnotation.create("server-response", "333333");
		collection.add(kv);
		return collection;
	}

}
