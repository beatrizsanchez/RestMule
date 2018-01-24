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


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Trivial data consumer, printing to console
 * 
 * @author kb
 *
 */
public class ConsoleOutput implements Observer<Object> {


	@Override
	public void onNext(Object o) {

		if (o instanceof Iterable<?>)
			for (Object oo : (Iterable<?>) o) {
				System.out.println(">>> " + oo);

			}
		else {
			System.out.println(">>> " + o);

		}

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
