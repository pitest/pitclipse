package org.pitest.pitclipse.runner.model;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import org.pitest.pitclipse.runner.results.DetectionStatus;

import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Collections2.transform;
import static org.pitest.pitclipse.runner.results.DetectionStatus.KILLED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.MEMORY_ERROR;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NON_VIABLE;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NOT_STARTED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NO_COVERAGE;
import static org.pitest.pitclipse.runner.results.DetectionStatus.RUN_ERROR;
import static org.pitest.pitclipse.runner.results.DetectionStatus.STARTED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.SURVIVED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.TIMED_OUT;

public class MutationsModel implements Visitable, Countable {

    private enum StatusComparator implements Comparator<Status> {
        INSTANCE;

        private static final Ordering<DetectionStatus> STATUSES_IN_ORDER = Ordering.explicit(ImmutableList.of(SURVIVED,
                NOT_STARTED, STARTED, KILLED, TIMED_OUT, NON_VIABLE, MEMORY_ERROR, RUN_ERROR, NO_COVERAGE));

        @Override
        public int compare(Status lhs, Status rhs) {
            return STATUSES_IN_ORDER.compare(lhs.getDetectionStatus(), rhs.getDetectionStatus());
        }
    }

    public static final MutationsModel EMPTY_MODEL = make(ImmutableList.<Status>of());

    private final ImmutableList<Status> statuses;

    private MutationsModel(ImmutableList<Status> statuses) {
        this.statuses = ImmutableList.copyOf(transform(statuses, new Function<Status, Status>() {
            @Override
            public Status apply(Status input) {
                return input.copyOf().withModel(MutationsModel.this).build();
            }
        }));
    }

    public static MutationsModel make(List<Status> statuses) {
        ImmutableList<Status> sortedStatuses = Ordering.from(StatusComparator.INSTANCE).immutableSortedCopy(statuses);
        return new MutationsModel(sortedStatuses);
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public <T> T accept(MutationsModelVisitor<T> visitor) {
        return visitor.visitModel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MutationsModel that = (MutationsModel) o;
        return Objects.equal(statuses, that.statuses);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(statuses);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("statuses", statuses)
            .toString();
    }

    @Override
    public long count() {
        long sum = 0L;
        for (Status status : statuses) {
            sum += status.count();
        }
        return sum;
    }
}