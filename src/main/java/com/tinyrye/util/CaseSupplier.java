package com.tinyrye.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CaseSupplier<C,T> implements Function<C,T>
{
    private class CaseRelease<C,T> {
        public Predicate<C> caseTest;
        public Supplier<T> giver;
        public CaseRelease<C,T> caseTest(Predicate<C> caseTest) { this.caseTest = caseTest; return this; }
        public CaseRelease<C,T> giver(Supplier<T> giver) { this.giver = giver; return this; }
    }
    
    public class CaseReleaseBuilder
    {
        public final Predicate<C> caseTest;
        public CaseReleaseBuilder(Predicate<C> caseTest) {
            this.caseTest = caseTest;
        }
        public CaseSupplier<C,T> give(Supplier<T> giver) {
            return CaseSupplier.this.add(build(giver));
        }
        protected CaseRelease<C,T> build(Supplier<T> giver) {
            return new CaseRelease<C,T>().caseTest(caseTest).giver(giver);
        }
    }
    
    public List<CaseRelease<C,T>> caseReleases = new ArrayList<CaseRelease<C,T>>();
    public Function<C,T> otherwiseCase;
    
    public CaseReleaseBuilder on(Predicate<C> caseTest) {
        return new CaseReleaseBuilder(caseTest);
    }
    
    public CaseSupplier<C,T> otherwise(Function<C,T> otherwiseCase) {
        this.otherwiseCase = otherwiseCase;
        return this;
    }
    
    @Override
    public T apply(C caseValue) {
        return caseReleases.stream().filter(caseRelease -> caseRelease.caseTest.test(caseValue))
                .findFirst().map(caseRelease -> caseRelease.giver.get())
                //.orElse(otherwiseCase != null ? otherwiseCase.apply(caseValue) : (T) null);
                .orElse(otherwiseCase.apply(caseValue));
    }
    
    protected CaseSupplier<C,T> add(CaseRelease<C,T> caseRelease) {
        caseReleases.add(caseRelease);
        return this;
    }
}