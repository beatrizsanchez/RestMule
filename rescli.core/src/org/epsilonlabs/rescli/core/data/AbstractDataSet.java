package org.epsilonlabs.rescli.core.data;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.page.IPaged;
import org.epsilonlabs.rescli.core.util.LoggerUtil;

import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

/**
 * 
 * {@link AbstractDataSet}
 * <p>
 * Copyright &copy; 2017 University of York.
 * @author Beatriz Sanchez
 * @version 1.0.0
 *
 */
public abstract class AbstractDataSet<T> implements IDataSet<T>{

	private static final Logger LOG = LogManager.getLogger(AbstractDataSet.class);

	private ReplaySubject<T> subject; 
	private Status status;
	private Integer total;
	private Integer count;
	private LoggerUtil tester;
	private Integer max;

	public AbstractDataSet(Integer max){
		this.status = Status.CREATED;
		this.count = 0;
		this.tester = new LoggerUtil();
		this.max = max;
		this.subject = ReplaySubject.create(max);
	}
	
	public AbstractDataSet(){
		this.status = Status.CREATED;
		this.count = 0;
		this.tester = new LoggerUtil();
		this.max = null;
		this.subject = ReplaySubject.create();
	}
	
	public AbstractDataSet(IPaged policy){
		this.status = Status.CREATED;
		this.count = 0;
		this.tester = new LoggerUtil();
		this.max = policy.hasMax() ? (policy.hasPerIteration() ? policy.perIteration() : 1) * policy.increment() * policy.max() : null;
		if (max != null){
			this.subject = ReplaySubject.create(max);
		}
		LOG.info("MAX" + this.max);
	}

	@Override
	public Status status(){
		return status;
	};
	
	@Override
	public Integer count(){
		return count;
	};

	@Override
	public Observable<T> observe(){
		return subject;
	};

	@Override
	public Integer total() {
		return (total == null) ? count : total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	@Override
	public synchronized Integer percentage(){
		return ( total() != 0 ) ? (count*100)/total() : 0;	
	}
	
	public synchronized void addElements(List<T> elements) {
		//tester();
		for (T element : elements){
			status = Status.ADDING;
			subject.onNext(element);
			count++; 
		}
		LOG.info("COUNT " + count + " / " + total());
		if (count.equals(total()) || count.equals(max)) {
			status = Status.COMPLETED;
			subject.onComplete();
		} else {
			this.status = Status.AWAITING;
		}
	}

	void tester(){
		if (tester.isEmpty()) {
			LOG.info("SETTING UP TESTER");
			tester.setDataSet(this);
			tester.setDaemon(true);
			tester.start();
		}
	}
}
