/*
 Copyright (C) 2007 Srinivas Hasti

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 
 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

package org.jquantlib.indexes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jquantlib.math.Closeness;
import org.jquantlib.time.Calendar;
import org.jquantlib.util.Date;
import org.jquantlib.util.DefaultObservable;
import org.jquantlib.util.Observable;
import org.jquantlib.util.Observer;
import org.jquantlib.util.TimeSeries;

/**
 * @author Srinivas Hasti
 * 
 */
//TODO: Code review and comments
public abstract class Index implements Observable {

	/**
	 * Return name of the Index
	 * @return
	 */
	public abstract String getName();

	/**
	 * Returns the calendar defining valid fixing dates
	 */
	public abstract Calendar getFixingCalendar();

	/**
	 * 
	 *  Returns TRUE if the fixing date is a valid one
	 */
	public abstract boolean isValidFixingDate(Date fixingDate);

	/**
	 * Returns the fixing at the given date
	 * ! the date passed as arguments must be the actual calendar date of the
	 * fixing; no settlement days must be used.
	 */
	public abstract double fixing(Date fixingDate, boolean forecastTodaysFixing);

	/**
	 * 
	 * Returns the fixing TimeSeries
	 */
	public TimeSeries<Double> timeSeries() {
		return IndexManager.getInstance().get(getName());
	}

	 
	/*
	 * Stores the historical fixing at the given date
	 * 
	 * The date passed as arguments must be the actual calendar date of the
	 * fixing; no settlement days must be used.
	 */
	public void addFixing(Date fixingDate, double fixing, boolean forceOverwrite) {
		List<Date> fixingDates = new ArrayList<Date>();
		fixingDates.add(fixingDate);

		List<Double> fixings = new ArrayList<Double>();
		fixings.add(fixing);

		addFixings(fixingDates, fixings, forceOverwrite);
	}

	
	/*
	 * Stores historical fixings from a TimeSeries
	 * 
	 * The dates in the TimeSeries must be the actual calendar dates of the
	 * fixings; no settlement days must be used.
	 */
	public void addFixings(TimeSeries<Double> t, boolean forceOverwrite) {
		addFixings(t.dates(), t.values(), forceOverwrite);
	}

	
	/*
	 * Stores historical fixings at the given dates
	 * 
	 * the dates passed as arguments must be the actual calendar dates of the
	 * fixings; no settlement days must be used.
	 */
	public void addFixings(Collection<Date> dates, Collection<Double> values,
			boolean forceOverwrite) {
		String tag = getName();
		TimeSeries<Double> h = IndexManager.getInstance().get(tag);
		boolean missingFixing;
		boolean validFixing;
		boolean noInvalidFixing = true;
		boolean noDuplicatedFixing = true;
		Date invalidDate = null;
		Date duplicatedDate = null;
		Double nullValue = null;
		Double invalidValue = Double.NaN;
		Double duplicatedValue = Double.NaN;

		Iterator<Double> valuesIterator = values.iterator();
		for (Date date : dates) {
			Double value = valuesIterator.next();
			validFixing = isValidFixingDate(date);
			double currentValue = h.find(date);
			missingFixing = forceOverwrite
					|| Closeness.isClose(currentValue, nullValue);
			if (validFixing) {
				if (missingFixing)
					h.add(date, value);
				else if (Closeness.isClose(currentValue, value)) {
					// Do nothing
				} else {
					noDuplicatedFixing = false;
					duplicatedDate = date;
					duplicatedValue = value;
				}
			} else {
				noInvalidFixing = false;
				invalidDate = date;
				invalidValue = value;
			}
		}
		IndexManager.getInstance().put(tag, h);
		if (!noInvalidFixing)
			throw new IllegalStateException(
					"At least one invalid fixing provided: " + invalidDate
							+ ", " + invalidValue);

		if (!noDuplicatedFixing)
			throw new IllegalStateException(
					"At least one duplicated fixing provided: "
							+ duplicatedDate + ", " + duplicatedValue);
	}

	/**
	 * Clear the fixings stored for the index
	 */
	public void clearFixings() {
		IndexManager.getInstance().clearHistory(getName());
	}

	/**
	 * Implements multiple inheritance via delegate pattern to an inner class
	 * 
	 */
	private Observable delegatedObservable = new DefaultObservable(this);

	public void addObserver(Observer observer) {
		delegatedObservable.addObserver(observer);
	}

	public int countObservers() {
		return delegatedObservable.countObservers();
	}

	public void deleteObserver(Observer observer) {
		delegatedObservable.deleteObserver(observer);
	}

	public void notifyObservers() {
		delegatedObservable.notifyObservers();
	}

	public void notifyObservers(Object arg) {
		delegatedObservable.notifyObservers(arg);
	}

	public void deleteObservers() {
		delegatedObservable.deleteObservers();
	}

	public List<Observer> getObservers() {
		return delegatedObservable.getObservers();
	}

}