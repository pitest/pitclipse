/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner.results.summary;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

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
