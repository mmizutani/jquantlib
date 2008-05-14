/*
 Copyright (C) 2008 Richard Gomes

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquantlib-dev@lists.sf.net>. The license is also available online at
 <http://jquantlib.org/license.shtml>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
 
 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the originating copyright notice follows below.
 */

/**
 * @author Richard Gomes
 * @author Dominik Holenstein
 */

package org.jquantlib.math.distributions;


import org.jquantlib.math.Beta;

//TODO: Add test case for the CumulativeBinomialDistribution

public class CumulativeBinomialDistribution {
	static private double accuracy = 1e-16;
	static private int maxIteration = 100;
    static private int n_;
    static private double p_;
    
    public CumulativeBinomialDistribution(double p, int n){  
    	n_ = n;
    	p_ = p;
    	
    	if ((p<=0.0)) {
			throw new ArithmeticException("negative p not allowed");
		}
		if ((p>1.0)){
			throw new ArithmeticException("p>1.0 not allowed");
		}
    }

	public double evaluate(int k){
		 if (k >= n_)return 1.0;
         else return 1.0 - Beta.incompleteBetaFunction(k+1, n_-k, p_, accuracy, maxIteration);
	}
		
	
	/*
	 * Given an odd integer and a real number z it returns p such that:
	 * 1 - CumulativeBinomialDistribution((n-1/2, n, p) = CumulativeNormalDistribution(z)
	 * n must be odd
	 */
	static private double PeizerPrattMehtod2Inversion(double z, int n) {
		if(n%2 == 1) {
			throw new ArithmeticException("n must be an odd number: " + n + " not allowed");
		}
		
		double result = (z/(n+1.0/3.0+0.1/(n+1.0)));
		result *= result;
		result = Math.exp(-result*(n+1.0/6.0));
		result = 0.5 + (z>0 ? 1 : -1) * Math.sqrt((0.25*(1.0-result)));
		
		return result;	
	}

}