/*
 Copyright (C) 2007 Richard Gomes

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

/*
 Copyright (C) 2004 Ferdinando Ametrano
 Copyright (C) 2000, 2001, 2002, 2003 RiskMap srl

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

package org.jquantlib.daycounters;

import org.jquantlib.util.Date;

/**
 * Actual/360 day count convention, also known as "Act/360", or "A/360".
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Day_count_convention">Day count Convention</a>
 * 
 * @author Richard Gomes
 * @author Srinivas Hasti
 */
public class Actual360 extends AbstractDayCounter {

	private static final Actual360	actual360	= new Actual360();

	private Actual360() {
	}

	public static final Actual360 getDayCounter() {
		return actual360;
	}

    @Override
	public final String getName() /* @ReadOnly */{
		return "Actual/360";
	}

    @Override
	public /*@Time*/ final double getYearFraction(final Date dateStart, final Date dateEnd) /* @ReadOnly */{
		return getDayCount(dateStart, dateEnd) / 360.0;
	}

    @Override
	public /*@Time*/ final double getYearFraction(final Date dateStart, final Date dateEnd, final Date refPeriodStart,
			final Date refPeriodEnd) /* @ReadOnly */{
		return getDayCount(dateStart, dateEnd) / 360.0;
	}

}
