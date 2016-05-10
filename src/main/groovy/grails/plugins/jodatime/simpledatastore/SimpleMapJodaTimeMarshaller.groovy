package grails.plugins.jodatime.simpledatastore

import org.grails.datastore.mapping.engine.types.AbstractMappingAwareCustomTypeMarshaller
import org.grails.datastore.mapping.model.MappingFactory
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.datastore.mapping.query.Query
import org.grails.datastore.mapping.simple.query.SimpleMapResultList
import org.joda.time.DateMidnight
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import org.joda.time.Interval
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.MonthDay
import org.joda.time.Partial
import org.joda.time.Period
import org.joda.time.TimeOfDay
import org.joda.time.YearMonth
import org.joda.time.YearMonthDay

/**
 * A marshaller for Joda-Time types usable in the Simple Map datastore.
 * @param < T >
 */
class SimpleMapJodaTimeMarshaller<T> extends AbstractMappingAwareCustomTypeMarshaller<T, Map, SimpleMapResultList> {

	SimpleMapJodaTimeMarshaller(Class<T> targetType) {
		super(targetType)
	}

	@Override
	protected writeInternal(PersistentProperty property, String key, T value, Map nativeTarget) {
		nativeTarget[key] = value
	}

	@Override
	protected T readInternal(PersistentProperty property, String key, Map nativeSource) {
		nativeSource[key]
	}

	private static final SUPPORTED_OPERATIONS = [Query.Equals, Query.NotEquals]
	private static final SUPPORTED_OPERATIONS_FOR_COMPARABLE = SUPPORTED_OPERATIONS + [Query.GreaterThan, Query.GreaterThanEquals, Query.LessThan, Query.LessThanEquals, Query.Between]

	@Override
	protected void queryInternal(PersistentProperty property, String key, Query.PropertyCriterion criterion, SimpleMapResultList nativeQuery) {
		def supportedOperations = Comparable.isAssignableFrom(targetType) ? SUPPORTED_OPERATIONS_FOR_COMPARABLE : SUPPORTED_OPERATIONS
		def op = criterion.getClass()
		if (op in supportedOperations) {
			Closure handler = nativeQuery.query.handlers[op]
			nativeQuery.results << handler.call(criterion, property)
		} else {
			throw new RuntimeException("unsupported query type $criterion for property $property")
		}
	}

	static final Iterable<Class> SUPPORTED_TYPES = [LocalTime, LocalDate, LocalDateTime, MonthDay, TimeOfDay, YearMonth, YearMonthDay, Partial, DateTime, DateMidnight, Instant, Duration, DateTimeZone, Interval, Period]

	static initialize() {
		for (type in SUPPORTED_TYPES) {
			MappingFactory.registerCustomType(new SimpleMapJodaTimeMarshaller(type))
		}
	}
}
