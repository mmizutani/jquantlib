/*
 Copyright (C) 2008 Richard Gomes

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

/*
 Copyright (C) 2004, 2005, 2006 StatPro Italia srl

 This file is part of QuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://quantlib.org/

 QuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <quantlib-dev@lists.sf.net>. The license is also available online at
 <http://quantlib.org/license.shtml>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 */

package org.jquantlib.termstructures;

import org.jquantlib.daycounters.Actual365Fixed;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.lang.reflect.ReflectConstants;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Frequency;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;
import org.jquantlib.time.calendars.Target;
import org.jquantlib.util.Date;

/**
 * This abstract class defines the interface of concrete rate structures which
 * will be derived from this one.
 * 
 * <p>
 * Rates are assumed to be annual continuous compounding.
 */
// TODO: add derived class ParSwapTermStructure similar to ZeroYieldTermStructure, DiscountStructure, ForwardRateStructure
// TODO: observability against evaluation date changes is checked.
// FIXME:: code review on return types of getSomethingRate(...)
public abstract class AbstractYieldTermStructure extends AbstractTermStructure implements YieldTermStructure {

    //
    // protected constructors
    //
    
    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * 
     * @category constructors
     * 
     * @see TermStructure#TermStructure() documentation for issues regarding constructors.
     */
    protected AbstractYieldTermStructure() {
        this(Actual365Fixed.getDayCounter());
    }

    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * <p>
     * Initialize with a {@link DayCounter} with <b>no explicit reference date</b>.
     * 
     * @category constructors
     * 
     * @note Term structures initialized by means of this constructor must manage
     * their own reference date by overriding the getReferenceDate() method.
     * 
     * @see TermStructure#TermStructure() documentation for issues regarding
     *      constructors.
     */
    protected AbstractYieldTermStructure(final DayCounter dc) {
        super(dc);
    }

    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * <p>
     * Initialize with a fixed reference date
     * 
     * @category constructors
     * 
     * @note TermStructure#TermStructure() documentation for issues regarding constructors.
     */
    protected AbstractYieldTermStructure(final Date referenceDate, final Calendar cal, final DayCounter dc) {
        super(referenceDate, cal, dc);
    }
    
    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * 
     * @category constructors
     * 
     * @param referenceDate
     * @param cal
     * @see YieldTermStructure#YieldTermStructure(Date, Calendar, DayCounter)
     */
    protected AbstractYieldTermStructure(final Date referenceDate, final Calendar cal) {
        super(referenceDate, cal, Actual365Fixed.getDayCounter());
    }
    
    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * 
     * @category constructors
     * 
     * @param referenceDate
     * @param dc
     * @see YieldTermStructure#YieldTermStructure(Date, Calendar, DayCounter)
     */
    protected AbstractYieldTermStructure(final Date referenceDate, final DayCounter dc) {
        super(referenceDate, Target.getCalendar(), dc);
    }
    
    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * 
     * @category constructors
     * 
     * @param referenceDate
     * @see YieldTermStructure#YieldTermStructure(Date, Calendar, DayCounter)
     */
    protected AbstractYieldTermStructure(final Date referenceDate) {
        super(referenceDate, Target.getCalendar(), Actual365Fixed.getDayCounter());
    }

    /**
     * Calculate the reference date based on the global evaluation date
     * 
     * @category constructors
     * 
     * @note TermStructure#TermStructure() documentation for issues regarding
     *      constructors.
     */
    protected AbstractYieldTermStructure(int settlementDays, final Calendar cal, final DayCounter dc) {
        super(settlementDays, cal, dc);
    }
    
    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * 
     * @category constructors
     * 
     * @param settlementDays
     * @param cal
     * @see YieldTermStructure#YieldTermStructure(int, Calendar, DayCounter)
     */
    protected AbstractYieldTermStructure(int settlementDays, final Calendar cal) {
        super(settlementDays, cal, Actual365Fixed.getDayCounter());
    }

    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * 
     * @category constructors
     * 
     * @param settlementDays
     * @param dc
     * @see YieldTermStructure#YieldTermStructure(int, Calendar, DayCounter)
     */
    protected AbstractYieldTermStructure(int settlementDays, final DayCounter dc) {
        super(settlementDays, Target.getCalendar(), dc);
    }

    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * 
     * @category constructors
     * 
     * @param settlementDays
     * @see YieldTermStructure#YieldTermStructure(int, Calendar, DayCounter)
     */
    protected AbstractYieldTermStructure(int settlementDays) {
        super(settlementDays, Target.getCalendar(), Actual365Fixed.getDayCounter());
    }

    
    //
    // abstract methods
    //

    /**
     * See the TermStructure documentation for issues regarding constructors. 
     * 
     * @category calculations
     */
    protected abstract /*DiscountFactor*/ double discountImpl(final /*@Time*/ double t);
    
    

    //
    // implements IYieldTermStructure
    //
    
    
    // ----- public methods ::: zero yield rates -----
    
    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#zeroRate(org.jquantlib.util.Date, org.jquantlib.daycounters.DayCounter, org.jquantlib.termstructures.Compounding)
     */
    @Override
    public final InterestRate zeroRate(final Date d, final DayCounter resultDayCounter, final Compounding comp) {
        return zeroRate(d, resultDayCounter, comp, Frequency.ANNUAL);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#zeroRate(org.jquantlib.util.Date, org.jquantlib.daycounters.DayCounter, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency)
     */
    @Override
    public final InterestRate zeroRate(final Date d, final DayCounter resultDayCounter, final Compounding comp, final Frequency freq) {
        return zeroRate(d, resultDayCounter, comp, freq, false);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#zeroRate(org.jquantlib.util.Date, org.jquantlib.daycounters.DayCounter, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency, boolean)
     */
    @Override
    public final InterestRate zeroRate(final Date d, final DayCounter dayCounter, final Compounding comp, final Frequency freq, boolean extrapolate) {
        if (d == referenceDate()) {
            /*@Time*/ double t = 0.0001;
            /*@CompoundFactor*/ double compound = 1/discount(t, extrapolate); // 1/discount(t,extrapolate)
            return InterestRate.impliedRate(compound, t, dayCounter, comp, freq);
        } else {
            /*@CompoundFactor*/ double compound = 1/discount(d, extrapolate); // 1/discount(d,extrapolate)
            return InterestRate.impliedRate(compound, referenceDate(), d, dayCounter, comp, freq);
        }
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#zeroRate(double, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency, boolean)
     */
    @Override
    public InterestRate zeroRate(final /*@Time*/ double  time, final Compounding comp, final Frequency freq, boolean extrapolate) {
        /*@Time*/ double t = time;
        if (t==0.0) {
            t = 0.0001;
        }
        /*@CompoundFactor*/ double compound = 1/discount(t, extrapolate);
        return InterestRate.impliedRate(compound, t, this.dayCounter(), comp, freq);
    }

    
    // ----- public methods ::: forward rates -----
    
    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#forwardRate(org.jquantlib.util.Date, org.jquantlib.util.Date, org.jquantlib.daycounters.DayCounter, org.jquantlib.termstructures.Compounding)
     */
    @Override
    public InterestRate forwardRate(final Date d1, final Date d2, final DayCounter resultDayCounter, final Compounding comp) {
        return forwardRate(d1, d2, resultDayCounter, comp, Frequency.ANNUAL);
    }

// FIXME: code review: is this method needed ???
//  protected InterestRate forwardRate(final Date d, final Period p, final DayCounter resultDayCounter, Compounding comp) {
//      return getForwardRate(d, p, resultDayCounter, comp);
//  }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#forwardRate(org.jquantlib.util.Date, org.jquantlib.util.Date, org.jquantlib.daycounters.DayCounter, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency)
     */
    @Override
    public InterestRate forwardRate(final Date d1, final Date d2, final DayCounter resultDayCounter, final Compounding comp, final Frequency freq) {
        return forwardRate(d1, d2, resultDayCounter, comp, freq, false);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#forwardRate(org.jquantlib.util.Date, org.jquantlib.util.Date, org.jquantlib.daycounters.DayCounter, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency, boolean)
     */
    @Override
    public InterestRate forwardRate(final Date d1, final Date d2, final DayCounter dayCounter, final Compounding comp, final Frequency freq, boolean extrapolate) {
        if (d1.equals(d2)) {
            /*@Time*/ double  t1 = timeFromReference(d1);
            /*@Time*/ double  t2 = t1+0.0001;
            /*@Time*/ double  delta = t2-t1;
            /*@DiscountFactor*/ double factor1 = discount(t1, extrapolate);
            /*@DiscountFactor*/ double factor2 = discount(t2, extrapolate);
            /*@CompoundFactor*/ double compound = factor1 / factor2;
            return InterestRate.impliedRate(compound, delta, dayCounter, comp, freq);
        } else if (d1.lt(d2)) {
            /*@DiscountFactor*/ double discount1 = discount(d1, extrapolate);
            /*@DiscountFactor*/ double discount2 = discount(d2, extrapolate);
            /*@CompoundFactor*/ double compound = discount1 / discount2;
            return InterestRate.impliedRate(compound, d1, d2, dayCounter, comp, freq);
        } else {
            throw new IllegalArgumentException(d1 + " later than " + d2);
        }
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#forwardRate(double, double, org.jquantlib.termstructures.Compounding)
     */
    @Override
    public InterestRate forwardRate(final /*@Time*/ double  t1, final /*@Time*/ double  t2, final Compounding comp) {
        return forwardRate(t1, t2, comp, Frequency.ANNUAL);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#forwardRate(double, double, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency)
     */
    @Override
    public InterestRate forwardRate(final /*@Time*/ double  t1, final /*@Time*/ double t2, final Compounding comp, final Frequency freq) {
        return forwardRate(t1, t2, comp, freq, false);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#forwardRate(double, double, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency, boolean)
     */
    // FIXME; this method is clearly buggy
    @Override
    public InterestRate forwardRate(final /*@Time*/ double  time1, final /*@Time*/ double  time2, final Compounding comp, final Frequency freq, boolean extrapolate) {
        /*@Time*/ double t1 = time1;
        /*@Time*/ double t2 = time2;
        if (t2==t1) t2 = t1+0.0001;
        if (!(t1<=t2)) throw new IllegalArgumentException("t1 (" + t1 + ") < t2 (" + t2 + ")");
        /*@DiscountFactor*/ double discount1 = discount(t1, extrapolate);
        /*@DiscountFactor*/ double discount2 = discount(t2, extrapolate);
        /*@CompoundFactor*/ double compound = discount1 / discount2;
        /*@Time*/ double delta = t2-t1;
        return InterestRate.impliedRate(compound, delta, this.dayCounter(), comp, freq);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#forwardRate(org.jquantlib.util.Date, org.jquantlib.time.Period, org.jquantlib.daycounters.DayCounter, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency)
     */
    @Override
    public InterestRate forwardRate(final Date d, final Period p, final DayCounter resultDayCounter, Compounding comp, Frequency freq) {
        return forwardRate(d, p, resultDayCounter, comp, freq, false);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#forwardRate(org.jquantlib.util.Date, org.jquantlib.time.Period, org.jquantlib.daycounters.DayCounter, org.jquantlib.termstructures.Compounding, org.jquantlib.time.Frequency, boolean)
     */
    @Override
    public InterestRate forwardRate(final Date d, final Period p, final DayCounter dayCounter, final Compounding comp, final Frequency freq, boolean extrapolate) {
        return forwardRate(d, d.getDateAfter(p), dayCounter, comp, freq, extrapolate);
    }


    // ----- public methods ::: discount factors -----
    
    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#discount(org.jquantlib.util.Date)
     */
    @Override
    public /*@DiscountFactor*/ double discount(final Date d) {
        return discount(d, false);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#discount(org.jquantlib.util.Date, boolean)
     */
    @Override
    public /*@DiscountFactor*/ double discount(final Date d, boolean extrapolate) {
        checkRange(d, extrapolate);
        return discountImpl(timeFromReference(d));
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#discount(double)
     */
    @Override
    public /*@DiscountFactor*/ double discount(final /*@Time*/ double t) {
        return discount(t, false);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#discount(double, boolean)
     */
    @Override
    public /*@DiscountFactor*/ double discount(final /*@Time*/ double t, boolean extrapolate) {
        checkRange(t, extrapolate);
        return discountImpl(t);
    }

    
    // ----- public methods ::: par rates -----
    
    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#parRate(int, org.jquantlib.util.Date, org.jquantlib.time.Frequency, boolean)
     */
    @Override
    public /*@Rate*/ double parRate(int tenor, final Date startDate, final Frequency freq, boolean extrapolate) {
        Date[] dates = new Date[tenor + 1];
        dates[0] = startDate;
        for (int i = 1; i <= tenor; i++)
            dates[i] = startDate.getDateAfter(new Period(i, TimeUnit.YEARS));
        return parRate(dates, freq, extrapolate);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#parRate(org.jquantlib.util.Date[], org.jquantlib.time.Frequency, boolean)
     */
    @Override
    public /*@Rate*/ double parRate(final Date[] dates, final Frequency freq, boolean extrapolate) {
        /*@Time*/ double [] times = new /*@Time*/ double [dates.length];
        for (int i = 0; i < dates.length; i++)
            times[i] = timeFromReference(dates[i]);
        return parRate(times, freq, extrapolate);
    }

    /* (non-Javadoc)
     * @see org.jquantlib.termstructures.IYieldTermStructure#parRate(double[], org.jquantlib.time.Frequency, boolean)
     */
    @Override
    public /*@Rate*/ double parRate(final /*@Time*/ double[] times, final Frequency frequency, boolean extrapolate) {
        if (times.length < 2)
            throw new IllegalArgumentException("at least two times are required");
        /*@Time*/ double last = times[times.length - 1];
        checkRange(last, extrapolate);
        /*@DiscountFactor*/ double sum = 0.0;
        for (int i = 1; i < times.length; i++) {
            sum += discountImpl(times[i]);
        }
        /*@Rate*/ double result = discountImpl(times[0]) - discountImpl(last);
        int freq = frequency.toInteger();
        result *= freq/sum;
        return result;
    }

}