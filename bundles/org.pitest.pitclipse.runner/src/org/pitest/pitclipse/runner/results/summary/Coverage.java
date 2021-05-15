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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * <p>The amount of code covered by tests.</p>
 * 
 * <p>An instance of this class is typically used to represent a class' line and mutation coverage.</p>
 * 
 * <p>This class is serializable so that it can be sent from the PIT
 * application running in a background VM to the Eclipse listeners.</p>
 */
class Coverage implements Serializable {

    private static final int PRECISION = 10;
    private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);
    private static final long serialVersionUID = 6511618552254606506L;

    private final int covered;
    private final int total;
    private final BigDecimal percentage;

    private Coverage(int covered, int total, BigDecimal percentage) {
        this.covered = covered;
        this.total = total;
        this.percentage = percentage;
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

        return Objects.equals(this.covered, rhs.covered) &&
                Objects.equals(this.total, rhs.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.covered, this.total);
    }

    @Override
    public String toString() {
        return "Coverage [covered=" + covered + ", total=" + total + ", percentage=" + percentage + "]";
    }

}
