/*******************************************************************************
 * Copyright (c) 2017 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Konstantinos Barmpis - initial API and implementation
 ******************************************************************************/
package org.epsilonlabs.rescli.github.test.mde;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.github.model.SearchCode;
import org.epsilonlabs.rescli.github.model.SearchCode.Repository;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class FileToRepo implements ObservableSource<Repository>, Observer<SearchCode> {

	private static final Logger LOG = LogManager.getLogger(FileToRepo.class);

	protected PublishSubject<Repository> repoObs = PublishSubject.create();
	// notifications to tools interested in progress info
	protected Collection<Observer<? super Repository>> subscribers = new LinkedList<>();

	public Observable<Repository> repos() {
		return repoObs;
	}

	@Override
	public void onNext(SearchCode o) {

		try {

			Repository r = o.getRepository();

			//LOG.info(r.getId());
			repoObs.onNext(r);

		} catch (Exception e) {
			System.err.println("Error in onNext() of GeneratedGithubRepoToFiles:");
			e.printStackTrace();
		}

	}

	@Override
	public void onSubscribe(Disposable d) {
		//
	}

	@Override
	public void onError(Throwable e) {
		e.printStackTrace();
	}

	@Override
	public void onComplete() {
		repoObs.onComplete();
	}

	@Override
	public void subscribe(Observer<? super Repository> observer) {
		subscribers.add(observer);		
	}

}
