/*
 Copyright (C) 2009 Ueli Hofstetter
 Copyright (C) 2009 John Nichol

 This source code is release under the BSD License.

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 
 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */


package org.jquantlib.cashflow;

import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.termstructures.Compounding;
import org.jquantlib.termstructures.InterestRate;
import org.jquantlib.time.Date;
import org.jquantlib.util.TypedVisitor;
import org.jquantlib.util.Visitor;

// TODO: code review :: Please review this class! :S
public class FixedRateCoupon extends Coupon {

	private InterestRate rate;
	private DayCounter dayCounter;

	public FixedRateCoupon(double nominal, 
			final Date paymentDate, 
			final double rate,
			final DayCounter dayCounter,
			final Date accrualStartDate, 
			final Date accrualEndDate) {
		this(nominal, paymentDate, rate, dayCounter, accrualStartDate, accrualEndDate, new Date(), new Date());
	}

	public FixedRateCoupon(double nominal, 
			final Date paymentDate, 
			final double rate,
			final DayCounter dayCounter,
			final Date accrualStartDate, 
			final Date accrualEndDate, 
			final Date refPeriodStart,
			final Date refPeriodEnd) {
		super(nominal, paymentDate, accrualStartDate, accrualEndDate, refPeriodStart, refPeriodEnd);

		this.rate = new InterestRate(rate, dayCounter, Compounding.Simple);
		this.dayCounter = dayCounter;
	}

	public FixedRateCoupon(
			final double nominal, 
			final Date paymentDate, 
			final InterestRate interestRate, 
			final DayCounter dayCounter, 
			final Date accrualStartDate,
			final Date accrualEndDate, 
			final Date refPeriodStart,
			final Date refPeriodEnd) {
		super(nominal, paymentDate, accrualStartDate, accrualEndDate, refPeriodStart, refPeriodEnd);

		if(refPeriodStart == null){
			this.refPeriodStart = new Date();
		}
		if(refPeriodEnd == null){
			this.refPeriodEnd = new Date();
		}

		this.rate = interestRate;
		this.dayCounter = dayCounter;
	}


	//
	// Overrides Coupon
	//

	@Override
	public DayCounter dayCounter() {
		return dayCounter;
	}

	@Override
	public double rate() {
		return rate.rate();
	}

	public InterestRate interestRate() {
		return interestRate();
	}
	
	@Override
	public double accruedAmount(Date d){
		if(d.le(accrualStartDate) || d.gt(paymentDate)){
			return 0.0;
		}
		else{
			Date minD = d.le(accrualEndDate) ? d : accrualEndDate;
			return nominal()*(rate.compoundFactor(accrualStartDate,
					minD,
					refPeriodStart,
					refPeriodEnd) - 1.0);
		}
	}


	//
	// Overrides CashFlow
	//

	@Override
	public double amount() {
		return nominal()*(rate.compoundFactor(accrualStartDate,
				accrualEndDate,
				refPeriodStart,
				refPeriodEnd) - 1.0);
	}


	//
	// implements TypedVisitable
	//

	@Override
	public void accept(final TypedVisitor<Object> v) {
		Visitor<Object> v1 = (v!=null) ? v.getVisitor(this.getClass()) : null;
		if (v1 != null) {
			v1.visit(this);
		} else {
			super.accept(v);
		}
	}

}
