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
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.github.model.SearchCode;
import org.epsilonlabs.rescli.github.model.SearchCode.Repository;
import org.epsilonlabs.rescli.github.test.query.CodeSearchQuery;
import org.epsilonlabs.rescli.github.test.query.GitHubTestUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class RepoToFile implements ObservableSource<SearchCode>, Observer<Repository> {

	private MDE mde;

	public RepoToFile(MDE mde) {
		this.mde = mde;
	}

	private static final Logger LOG = LogManager.getLogger(RepoToFile.class);

	protected PublishSubject<SearchCode> fileObs = PublishSubject.create();
	// notifications to tools interested in progress info
	protected Collection<Observer<? super SearchCode>> subscribers = new LinkedList<>();

	public Observable<SearchCode> files() {
		return fileObs;
	}

	@Override
	public void onNext(Repository o) {

		try {


			String q = new CodeSearchQuery().create(mde.getKeyword()).extension(mde.getExtension())
					.repo(o.getFullName()).build().getQuery();
			
			IDataSet<SearchCode> ret = GitHubTestUtil.getOAuthClient().getSearchCode("asc", q, null);

			ret.observe().subscribe(fileObs);

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
		fileObs.onComplete();
	}

	@Override
	public void subscribe(Observer<? super SearchCode> observer) {
		subscribers.add(observer);
	}

	public static void main(String[] a){
		
		
		MDE mde = MDE.Eugenia;
		String q = new CodeSearchQuery().create(mde.getKeyword()).extension(mde.getExtension()).repo("https://github.com/GuanglongDu/GMFSVG").inFile().build().getQuery();
		System.out.println(q);
		
	}
	
}
