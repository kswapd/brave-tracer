package com.dcits.brave.aops;

import com.github.kristofa.brave.ClientResponseAdapter;
import com.github.kristofa.brave.KeyValueAnnotation;
import java.util.ArrayList;
import java.util.Collection;

class ClientResponseAdapterImpl implements ClientResponseAdapter {


   public Collection<KeyValueAnnotation> responseAnnotations() {
	   Collection<KeyValueAnnotation> collection = new ArrayList<KeyValueAnnotation>();
	  /* KeyValueAnnotation kv = KeyValueAnnotation.create("client-response", "444444");
	   collection.add(kv);*/
	   return collection;
   }

}
