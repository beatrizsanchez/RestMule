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
import java.util.Map.Entry;

import org.apache.lucene.analysis.CharArrayMap.EntrySet;
import org.epsilonlabs.rescli.github.model.Commits;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

// Repo/File/Commits triple
public class CommitDataConsumer implements Observer<Entry<String, Entry<String, Commits>>> {

	// repo / {file - author : #commits}
	HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> outputMap;

	public CommitDataConsumer(HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>> outputMap) {
		this.outputMap = outputMap;
	}

	// Repo/File/Commits triple
	@Override
	public void onNext(Entry<String, Entry<String, Commits>> o) {

		String author = o.getValue().getValue().getCommit().getCommitterInner().getEmail();
		String repo = o.getKey();
		String file = o.getValue().getKey();

		HashMap<String, HashMap<String, HashSet<String>>> repoMap = outputMap.get(repo);
		//
		HashMap<String, HashSet<String>> fileMap = repoMap.get(file);

		if (fileMap == null)
			fileMap = new HashMap<>();

		HashSet<String> value = fileMap.get(author);

		if (value == null)
			value = new HashSet<>();

		value.add(o.getValue().getValue().getCommit().getUrl());

		fileMap.put(author, value);

		repoMap.put(file, fileMap);

		outputMap.put(repo, repoMap);

		//

	}

	@Override
	public void onError(Throwable e) {
		e.printStackTrace();

	}

	@Override
	public void onComplete() {
		System.out.println("DATA STREAM ENDED");
	}

	@Override
	public void onSubscribe(Disposable d) {
		//
	}

}
