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

import java.util.HashMap;
import java.util.HashSet;

import org.epsilonlabs.rescli.github.model.Commits;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Trivial data consumer, printing to console
 * 
 * @author kb
 *
 */
public class CommitDataConsumer implements Observer<Commits> {

	HashMap<String, HashSet<String>> repoFileMap = new HashMap<>();

	int count = 0;

	@Override
	public void onNext(Commits o) {

		// System.err.println(o.getPath());
		//
		// Repository r = o.getRepository();
		//
		// String reponame = r.getFullName();
		//
		// HashSet<String> files = repoFileMap.get(reponame);
		//
		// if (files == null)
		// files = new HashSet<String>();
		//
		// files.add(o.getPath());
		// repoFileMap.put(reponame, files);

		// o.getRepository().get
		count++;
		// System.out.println(">" + o);

	}

	@Override
	public void onError(Throwable e) {
		e.printStackTrace();

	}

	@Override
	public void onComplete() {
		System.out.println("DATA STREAM ENDED");
		dumpData();
	}

	@Override
	public void onSubscribe(Disposable d) {
		//
	}

	public void dumpData() {
		for (String s : repoFileMap.keySet()) {
			System.out.println(s);
			for (String t : repoFileMap.get(s))
				System.out.println("\t" + t);
		}
		System.err.println(count);
	}

}
