package org.pitest.pitclipse.pitrunner.results.summary;

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

        Coverage coverage1 = (Coverage) o;

        if (covered != coverage1.covered) {
            return false;
        }
        if (total != coverage1.total) {
            return false;
        }
        return coverage != null ? coverage.equals(coverage1.coverage) : coverage1.coverage == null;
    }

    @Override
    public int hashCode() {
        int result = covered;
        result = 31 * result + total;
        result = 31 * result + (coverage != null ? coverage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Coverage{" +
                "covered=" + covered +
                ", total=" + total +
                ", coverage=" + coverage +
                '}';
    }
}
