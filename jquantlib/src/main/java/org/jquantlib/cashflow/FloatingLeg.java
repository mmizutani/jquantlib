package org.jquantlib.cashflow;

import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.indexes.InterestRateIndex;
import org.jquantlib.lang.reflect.TypeTokenTree;
import org.jquantlib.math.matrixutilities.Array;
import org.jquantlib.time.BusinessDayConvention;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Schedule;
import org.jquantlib.util.Date;

// TODO: code review :: license, class comments, comments for access modifiers, comments for @Override
// TODO: code review :: Please complete this class and perform another code review.
public class FloatingLeg<InterestRateIndexType extends InterestRateIndex, FloatingCouponType, CappedFlooredCouponType> extends Leg {

    public FloatingLeg(
                final Array nominals,
                final Schedule schedule,
                final InterestRateIndexType index,
                final DayCounter paymentDayCounter,
                final BusinessDayConvention paymentAdj,
                final Array fixingDays,
                final Array gearings,
                final Array spreads,
                final Array caps,
                final Array floors,
                final boolean isInArrears,
                final boolean isZero) {
        super(schedule.size() - 1);

        if (System.getProperty("EXPERIMENTAL") == null)
            throw new UnsupportedOperationException("Work in progress");

        //
        //FIXME: This class is generic but we are not handling generic parameters properly.
        // Reason: Imagine that the class paremeter is a fixed coupon class type.
        // Imagine now that the call "Detail.get(gearings, i, 1.0)" returns non-zero.
        // It means that we will be dynamically instantiating a floating coupon type whilst a fixed coupon type was
        // passed as class parameter, which is obviously a programmer error. This situation should be detected.
        // Please let me know if you need help on it. [Richard]
        //

        final int n = schedule.size() - 1;
        assert nominals.length <= n : "too many nominals";
        assert gearings.length <= n : "too many gearings";
        assert spreads.length <= n  : "too many spreads";
        assert caps.length <= n     : "too many caps";
        assert floors.length <= n   : "too many floors";
        assert !isZero || !isInArrears : "features in-arrears and zero are not compatible";

        // the following is not always correct (orignial c++ comment)
        final Calendar calendar = schedule.getCalendar();

        // FIXME: constructor for uninitialized date available ?
        Date refStart, start, refEnd, end;
        Date paymentDate;

        for (int i = 0; i < n; i++) {
            refStart = start = schedule.date(i);
            refEnd = end = schedule.date(i + 1);
            paymentDate = calendar.adjust(end, paymentAdj);

            if (i == 0 && !schedule.isRegular(i + 1))
                refStart = calendar.adjust(end.decrement(schedule.tenor()), paymentAdj);
            if (i == n - 1 && !schedule.isRegular(i + 1))
                refEnd = calendar.adjust(start.increment(schedule.tenor()), paymentAdj);
            if (Detail.get(gearings, i, 1.0) == 0.0)
                add(new FixedRateCoupon(Detail.get(nominals, i, new Double(1.0)),
                                    paymentDate, Detail.effectiveFixedRate(spreads,caps,floors,i),
                                    paymentDayCounter,
                                    start, end, refStart, refEnd));
            else if (Detail.noOption(caps, floors, i)){
                //try{
                    //get the generic type
                    final Class<?> fctklass = new TypeTokenTree(this.getClass()).getRoot().get(1).getElement();
                    //construct a new instance using reflection. first get the constructor ...
                    FloatingCouponType frc;
                    try {
                        frc = (FloatingCouponType) fctklass.getConstructor(
                                Date.class,
                                int.class,
                                Date.class,
                                Date.class,
                                int.class,
                                InterestRateIndex.class,
                                double.class,
                                double.class,
                                Date.class,
                                Date.class,
                                DayCounter.class,
                                boolean.class)
                         //then create a new instance
                        .newInstance(paymentDate,
                            Detail.get(nominals, i, new Double(1.0)),
                            start, end,
                            Detail.get(fixingDays, i, index.fixingDays()),
                            index,
                            Detail.get(gearings, i, 1.0),
                            Detail.get(spreads, i, 0.0),
                            refStart, refEnd,
                            paymentDayCounter, isInArrears);
                    } catch (final Exception e) {
                        throw new AssertionError("Couldn't construct new instance from generic type");
                    }
                add((CashFlow)frc);
                }
            else {
                final Class<?> cfcklass = new TypeTokenTree(this.getClass()).getRoot().get(2).getElement();
                CappedFlooredCouponType cfctc;
                try {
                    //FIXME: not finished yet!!!!!!!!!!!!!!
                    cfctc = (CappedFlooredCouponType) cfcklass.getConstructor(
                            Date.class,
                            int.class,
                            Date.class,
                            Date.class,
                            int.class,
                            InterestRateIndex.class,
                            double.class,
                            double.class,
                            Date.class,
                            Date.class,
                            DayCounter.class,
                            boolean.class)
                     //then create a new instance
                    .newInstance(paymentDate,

                        Detail.get(nominals, i, new Double(1.0)),
                        start, end,
                        Detail.get(fixingDays, i, index.fixingDays()),
                        index,
                        Detail.get(gearings, i, 1.0),
                        Detail.get(spreads, i, 0.0),
                        refStart, refEnd,
                        paymentDayCounter, isInArrears);
                } catch (final Exception e) {
                    throw new AssertionError("Couldn't construct new instance from generic type");
                }
            add((CashFlow)cfctc);
         }
        }
    }
}


//}