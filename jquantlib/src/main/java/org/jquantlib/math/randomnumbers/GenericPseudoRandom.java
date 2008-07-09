/*
 Copyright (C) 2007 Richard Gomes

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
 Copyright (C) 2004 Ferdinando Ametrano
 Copyright (C) 2000, 2001, 2002, 2003 RiskMap srl
 Copyright (C) 2004 Walter Penschke

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

package org.jquantlib.math.randomnumbers;

/**
 * @author Richard Gomes
 */
public class GenericPseudoRandom<R extends RNG, I extends IC> {
    
    // FIXME: code review
    // private enum { allowsErrorEstimate = 1 };
    
    // data
    static private IC icInstance;
    
    
    // FIXME: code review
    /*static*/ public InverseCumulativeRsg<? extends RandomSequenceGenerator<R>, I> 
                makeSequenceGenerator(final int dimension, /* @Unsigned */ long seed) {
        
        RandomSequenceGenerator<R> g = new RandomSequenceGenerator<R>(dimension, seed);
        
        if (icInstance!=null) {
            return new InverseCumulativeRsg(g, icInstance);
        } else {
            return new InverseCumulativeRsg(g);
        }
    }
    
}


//namespace QuantLib {
//
//    // random number traits
//
//    template <class URNG, class IC>
//    struct GenericPseudoRandom {
//        // typedefs
//        typedef URNG urng_type;
//        typedef InverseCumulativeRng<urng_type,IC> rng_type;
//        typedef RandomSequenceGenerator<urng_type> ursg_type;
//        typedef InverseCumulativeRsg<ursg_type,IC> rsg_type;
//        // more traits
//        enum { allowsErrorEstimate = 1 };
//        // factory
//        static rsg_type make_sequence_generator(Size dimension,
//                                                BigNatural seed) {
//            ursg_type g(dimension, seed);
//            return (icInstance ? rsg_type(g, *icInstance) : rsg_type(g));
//        }
//        // data
//        static boost::shared_ptr<IC> icInstance;
//    };
//
//    // static member initialization
//    template<class URNG, class IC>
//    boost::shared_ptr<IC> GenericPseudoRandom<URNG, IC>::icInstance;
//
//
//    //! default traits for pseudo-random number generation
//    /*! \test a sequence generator is generated and tested by comparing
//              samples against known good values.
//    */
//    typedef GenericPseudoRandom<MersenneTwisterUniformRng,
//                                InverseCumulativeNormal> PseudoRandom;
//
//    //! traits for Poisson-distributed pseudo-random number generation
//    /*! \test sequence generators are generated and tested by comparing
//              samples against known good values.
//    */
//    typedef GenericPseudoRandom<MersenneTwisterUniformRng,
//                                InverseCumulativePoisson> PoissonPseudoRandom;
//
//
//    template <class URSG, class IC>
//    struct GenericLowDiscrepancy {
//        // typedefs
//        typedef URSG ursg_type;
//        typedef InverseCumulativeRsg<ursg_type,IC> rsg_type;
//        // more traits
//        enum { allowsErrorEstimate = 0 };
//        // factory
//        static rsg_type make_sequence_generator(Size dimension,
//                                                BigNatural seed) {
//            ursg_type g(dimension, seed);
//            return (icInstance ? rsg_type(g, *icInstance) : rsg_type(g));
//        }
//        // data
//        static boost::shared_ptr<IC> icInstance;
//    };
//
//    // static member initialization
//    template<class URSG, class IC>
//    boost::shared_ptr<IC> GenericLowDiscrepancy<URSG, IC>::icInstance;
//
//
//    //! default traits for low-discrepancy sequence generation
//    typedef GenericLowDiscrepancy<SobolRsg,
//                                  InverseCumulativeNormal> LowDiscrepancy;
//
//}
