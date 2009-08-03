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

package org.jquantlib.termstructures;

import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.time.Calendar;
import org.jquantlib.util.Date;
import org.jquantlib.util.TypedVisitable;
import org.jquantlib.util.TypedVisitor;
import org.jquantlib.util.Visitor;

/**
 * Black-volatility term structure
 * <p>
 * This abstract class defines the interface of concrete Black-volatility term
 * structures which will be derived from this one.
 * <p>
 * Volatilities are assumed to be expressed on an annual basis.
 * 
 * @author Richard Gomes
 */
// FIXME: code review
public abstract class BlackVolTermStructure extends AbstractTermStructure implements TermStructure, TypedVisitable<TermStructure> {

    static private final double dT = 1.0/365.0;

    /**
     * The minimum strike for which the term structure can return vols
     */
    public abstract /*@Price*/ double minStrike();

    /**
     * The maximum strike for which the term structure can return vols
     */
    public abstract /*@Price*/ double maxStrike();

    protected abstract /*@Volatility*/ double blackVolImpl(final /*@Time*/ double maturity, final /*@Price*/ double strike);

    protected abstract /*@Variance*/ double blackVarianceImpl(final /*@Time*/ double maturity, final /*@Price*/ double strike);



    // Constructors

    public BlackVolTermStructure(final DayCounter dc) {
        super(dc);
    }

    public BlackVolTermStructure(final Date refDate, final Calendar cal, final DayCounter dc) {
        super(refDate, cal, dc);
    }

    public BlackVolTermStructure(final int settlDays, final Calendar cal, final DayCounter dc) {
        super(settlDays, cal, dc);
    }

    /**
     * Present (a.k.a spot) volatility
     */
    public final /*@Volatility*/ double blackVol(final Date maturity, final /*@Price*/ double strike) {
        return blackVol(maturity, strike, false);
    }

    /**
     * Present (a.k.a spot) volatility
     */
    public final /*@Volatility*/ double blackVol(final Date maturity, final /*@Price*/ double strike, final boolean extrapolate) {
        /*@Time*/ final double t = super.timeFromReference(maturity);
        checkRange(t, strike, extrapolate);
        return blackVolImpl(t, strike);
    }

    /**
     * Present (a.k.a spot) volatility
     */
    public final /*@Volatility*/ double blackVol(final /*@Time*/ double maturity, final /*@Price*/ double strike) {
        return blackVol(maturity, strike, false);
    }

    /**
     * Present (a.k.a spot) volatility
     */
    public final /*@Volatility*/ double blackVol(final /*@Time*/ double maturity, final /*@Price*/ double strike, final boolean extrapolate) {
        checkRange(maturity, strike, extrapolate);
        return blackVolImpl(maturity, strike);
    }

    /**
     * Present (a.k.a spot) variance
     */
    public final /*@Variance*/ double blackVariance(final Date maturity, final /*@Price*/ double strike) {
        return blackVariance(maturity, strike, false);
    }

    /**
     * Present (a.k.a spot) variance
     */
    public final /*@Variance*/ double blackVariance(final Date maturity, final /*@Price*/ double strike, final boolean extrapolate) {
        /*@Time*/ final double t = super.timeFromReference(maturity);
        checkRange(t, strike, extrapolate);
        return blackVarianceImpl(t, strike);
    }

    /**
     * Present (a.k.a spot) variance
     */
    public final /*@Variance*/ double blackVariance(final /*@Time*/ double maturity, final /*@Price*/ double strike) {
        return blackVariance(maturity, strike, false);
    }

    /**
     * Present (a.k.a spot) variance
     */
    public final /*@Variance*/ double blackVariance(final /*@Time*/ double maturity, final /*@Price*/ double strike, final boolean extrapolate) {
        checkRange(maturity, strike, extrapolate);
        return blackVarianceImpl(maturity, strike);
    }


    private final void checkRange(final /*@Time*/ double time, final /*@Price*/ double strike, final boolean extrapolate) {
        super.checkRange(time, extrapolate);
        assert extrapolate || allowsExtrapolation() || (strike >= minStrike()) && (strike <= maxStrike()) : "strike is outside curve domain";
    }

    /**
     * Future (a.k.a. forward) volatility
     * 
     * @param date1
     * @param date2
     * @param strike
     * @param extrapolate
     * @return
     */
    public final /*@Volatility*/ double blackForwardVol(final Date date1, final Date date2, final /*@Price*/ double strike, final boolean extrapolate) {
        assert date1.le(date2): "date1 later than date2";
        /*@Time*/ final double time1 = timeFromReference(date1);
        /*@Time*/ final double time2 = timeFromReference(date2);
        return blackForwardVol(time1, time2, strike, extrapolate);
    }

    /**
     * Future (a.k.a. forward) volatility
     * 
     * @param time1
     * @param time2
     * @param strike
     * @param extrapolate
     * @return
     */
    public final /*@Volatility*/ double blackForwardVol(final /*@Time*/ double time1, final /*@Time*/ double time2, final /*@Price*/ double strike, final boolean extrapolate) {
        /*@Time*/ final double t1 = time1;
        /*@Time*/ final double t2 = time2;
        assert t1 <= t2 : "t1 later than t2";
        checkRange(time2, strike, extrapolate);
        if (t1==t2) {
            if (t1==0.0) {
                /*@Time*/ final double epsilon = 1.0e-5;
                /*@Variance*/ final double var = blackVarianceImpl(epsilon, strike);
                return Math.sqrt(var/epsilon);
            } else {
                final double epsilon = Math.min(1.0e-5, t1);
                /*@Variance*/ final double var1 = blackVarianceImpl(t1-epsilon, strike);
                /*@Variance*/ final double var2 = blackVarianceImpl(t1+epsilon, strike);
                assert var2 >= var1 : "variances must be non-decreasing"; // TODO: message
                return  Math.sqrt((var2-var1) / (2*epsilon));
            }
        } else {
            /*@Variance*/ final double var1 = blackVarianceImpl(time1, strike);
            /*@Variance*/ final double var2 = blackVarianceImpl(time2, strike);
            assert var2 >= var1 : "variances must be non-decreasing"; // TODO: message
            return  Math.sqrt((var2-var1)/(t2-t1));
        }
    }

    /**
     * Future (a.k.a. forward) variance
     * 
     * @param date1
     * @param date2
     * @param strike
     * @param extrapolate
     * @return
     */
    public final /*@Variance*/ double blackForwardVariance(final Date date1, final Date date2, final /*@Price*/ double strike, final boolean extrapolate) {
        assert date1.le(date2) : "date1 later than date2";
        /*@Time*/ final double time1 = timeFromReference(date1);
        /*@Time*/ final double time2 = timeFromReference(date2);
        return blackForwardVariance(time1, time2, strike, extrapolate);
    }

    /**
     * Future (a.k.a. forward) variance
     * 
     * @param time1
     * @param time2
     * @param strike
     * @param extrapolate
     * @return
     */
    public final /*@Variance*/ double blackForwardVariance(final /*@Time*/ double time1, final /*@Time*/ double time2, final /*@Price*/ double strike, final boolean extrapolate) {
        /*@Time*/ final double t1 = time1;
        /*@Time*/ final double t2 = time2;
        assert t1<=t2 : "t1 later than t2";
        checkRange(time2, strike, extrapolate);
        /*@Variance*/ final double v1 = blackVarianceImpl(time1, strike);
        /*@Variance*/ final double v2 = blackVarianceImpl(time2, strike);
        assert v2 >= v1 : "variances must be non-decreasing";
        return v2-v1;
    }

    //
    // implements TypedVisitable
    //

    @Override
    public void accept(final TypedVisitor<TermStructure> v) {
        final Visitor<TermStructure> v1 = (v!=null) ? v.getVisitor(this.getClass()) : null;
        if (v1 != null)
            v1.visit(this);
        else
            throw new AssertionError("not a Black-volatility term structure visitor");
    }

}
