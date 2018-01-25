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
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.core.data.IDataSet;
import org.epsilonlabs.rescli.github.model.Commits;
import org.epsilonlabs.rescli.github.model.SearchCode;
import org.epsilonlabs.rescli.github.model.SearchCode.Repository;
import org.epsilonlabs.rescli.github.test.query.CodeSearchQuery;
import org.epsilonlabs.rescli.github.test.query.GitHubTestUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class FileToCommits implements ObservableSource<Commits>, Observer<SearchCode> {

	private static final Logger LOG = LogManager.getLogger(FileToCommits.class);

	protected PublishSubject<Commits> commits = PublishSubject.create();
	// notifications to tools interested in progress info
	protected Collection<Observer<? super Commits>> subscribers = new LinkedList<>();

	public Observable<Commits> commits() {
		return commits;
	}

	private HashSet<String> cache = new HashSet<>();

	@Override
	public void onNext(SearchCode o) {

		if (!cache.contains(o.getPath())) {

			Repository r = o.getRepository();

			//
			// System.err.println(r.getOwner());
			// System.err.println(r.getOwner().getLogin());
			// System.err.println(r.getOwner().getHtmlUrl());
			// System.err.println(r.getOwner().getReposUrl());
			//
			// IDataSet<Commits> ret =
			// GitHubTestUtil.getOAuthClient().getReposCommits(r.getOwner().getLogin(),r.getFullName(),
			// "1018-01-24T17:50:00Z", "master", o.getName(), "",
			// "2018-01-24T17:50:00Z");

			IDataSet<Commits> ret = GitHubTestUtil.getOAuthClient().getReposCommits(r.getOwner().getLogin(),
					r.getName(), null, null, o.getPath(), null, null);
			//
			ret.observe().subscribe(commits);

			cache.add(o.getPath());

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
		commits.onComplete();
	}

	@Override
	public void subscribe(Observer<? super Commits> observer) {
		subscribers.add(observer);
	}

	public static void main(String[] a) {

		MDE mde = MDE.Eugenia;
		String q = new CodeSearchQuery().create(mde.getKeyword()).extension(mde.getExtension())
				.repo("https://github.com/GuanglongDu/GMFSVG").inFile().build().getQuery();
		System.out.println(q);

	}

}
