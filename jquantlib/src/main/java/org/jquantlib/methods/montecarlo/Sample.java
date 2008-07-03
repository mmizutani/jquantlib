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

package org.jquantlib.methods.montecarlo;

/**
 * 
 * @author Dominik Holenstein
 *
 * @param <E>
 */

//TODO: Add JavaDocs
//TODO: Code review

//weighted sample
public class Sample<T> {
	
	//
	// public fields
	//
    private T value;
    private double weight;
        
    // 
    // Constructor 
    //
    public Sample(final T value, double weight){
    	this.value=value;
    	this.weight=weight;
    }
    
    
    //
    // get values
    //
    
    public T getVal(){
    	return value;
    }
    
    public double getWeight() {
    	return weight;
    }
 
}
