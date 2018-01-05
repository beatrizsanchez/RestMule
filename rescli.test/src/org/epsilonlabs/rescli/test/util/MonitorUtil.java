package org.epsilonlabs.rescli.test.util;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.data.IData;
import org.epsilonlabs.rescli.core.data.IDataSet;

public class MonitorUtil {

	private static final Logger LOG = LogManager.getLogger(MonitorUtil.class);

	public static void start(String name){
		final Thread thread = new Thread(new Runnable() {
			@Override public void run() {
				while (Thread.activeCount() > 1) {
					final Set<Thread> allStackTraces = Thread.getAllStackTraces().keySet();
					String status = "";
					for (Thread t : allStackTraces){
						status += t.getName() + "(" + t.getState() + ") "; 
					}
					LOG.info(Thread.activeCount() +" active Threads. "+ status);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {}
				}
			}
		});
		thread.setName("MONITOR " + name);
		thread.setDaemon(true);
		thread.start();
	}	

	public static void logResponse(IData<?> repositories1, String name){
		repositories1.observe().subscribe(
				next -> {}, 
				e -> LOG.info(e.getMessage()), 
				() -> LOG.info(name + " DONE")
				);
	}
}
