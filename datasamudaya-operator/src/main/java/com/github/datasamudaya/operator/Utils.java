package com.github.datasamudaya.operator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Pod;
import io.javaoperatorsdk.operator.api.reconciler.Context;

public class Utils {
	private static final Logger log = LoggerFactory.getLogger(Utils.class);
	private static final int POD_RETRY = 30;
	private Utils() {
		
	}
	public static String readResource(String resource) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream fstream = Utils.class.getResourceAsStream(resource);
			while (fstream.available() > 0) {
				baos.write(fstream.read());
			}
			return baos.toString();			
		} catch(Exception e) {
			
		}
		return null;
	}
	
	public static Pod waitUntilPodIsUp(Context<DatasamudayaOperatorCustomResource> context, DatasamudayaOperatorCustomResource primary, Map<String, String> nameNodeLabel) {
		List<Pod> primaryPods = context.getClient().pods().inNamespace(primary.getMetadata().getNamespace()).withLabels(nameNodeLabel).list().getItems();
		int retry = 0;
		while(true) {
			primaryPods = context.getClient().pods().inNamespace(primary.getMetadata().getNamespace()).withLabels(nameNodeLabel).list().getItems();			
			Pod pod = primaryPods.get(0);
			if(pod.getStatus().getHostIP() != null && pod.getStatus().getHostIP().length()>=1) {
				return	pod; 
	    	}
			try {
				if(retry++ > POD_RETRY) {
					break;
				}
				Thread.sleep(1000);
				log.info("Pod Ip: {}", pod.getStatus().getHostIP());
	    	} catch (InterruptedException e) {
	    	}
		}
		
		return null;
	}
	
}
