package org.pitest.pitclipse.pitrunner.results.summary;

import org.pitest.pitclipse.reloc.guava.base.MoreObjects;
import org.pitest.pitclipse.reloc.guava.base.Objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

class Coverage implements Serializable {

    private static final int PRECISION = 10;
    private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);
    private static final long serialVersionUID = 6511618552254606506L;

    private final int covered;
    private final int total;
    private final BigDecimal coverage;

    private Coverage(int covered, int total, BigDecimal coverage) {
        this.covered = covered;
        this.total = total;
        this.coverage = coverage;
    }

    public static Coverage from(int covered, int total) {
        if (covered == 0 || total == 0) {
            return new Coverage(covered, total, BigDecimal.ZERO);
        } else if (covered >= total) {
            return new Coverage(covered, total, HUNDRED_PERCENT);
        } else {
            return new Coverage(covered, total, HUNDRED_PERCENT.multiply(BigDecimal.valueOf(covered, PRECISION))
                    .divide(BigDecimal.valueOf(total, PRECISION), RoundingMode.HALF_EVEN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Coverage rhs = (Coverage) o;

        return Objects.equal(this.covered, rhs.covered) &&
                Objects.equal(this.total, rhs.total);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.covered, this.total);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("covered", covered)
            .add("total", total)
            .add("coverage", coverage)
            .toString();
    }
}
