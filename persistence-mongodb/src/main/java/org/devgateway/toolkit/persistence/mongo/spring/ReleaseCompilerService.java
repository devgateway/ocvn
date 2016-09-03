/**
 * 
 */
package org.devgateway.toolkit.persistence.mongo.spring;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.devgateway.ocds.persistence.mongo.Identifiable;
import org.devgateway.ocds.persistence.mongo.Record;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tag;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;
import org.devgateway.ocds.persistence.mongo.repository.RecordRepository;
import org.devgateway.ocds.persistence.mongo.repository.ReleaseRepository;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

/**
 * @author mihai
 * 
 */
@Service
public class ReleaseCompilerService {

	protected static final Logger logger = LoggerFactory.getLogger(ReleaseCompilerService.class);

	@Autowired
	private ReleaseRepository releaseRepository;

	@Autowired
	private RecordRepository recordRepository;

	// @Autowired
	// private OcdsSchemaValidatorService ocdsSchemaValidatorService;
	//
	// @Autowired
	// private ObjectMapper jacksonObjectMapper;

	@Autowired
	protected Reflections reflections;

	private Set<Field> fieldsAnnotatedWithMerge;

	@PostConstruct
	protected void init() {
		fieldsAnnotatedWithMerge = Sets.newConcurrentHashSet(reflections.getFieldsAnnotatedWith(Merge.class));
	}

	/**
	 * @param left
	 * @param right
	 * @return
	 * @see {@link MergeStrategy#overwrite}
	 */
	protected Object mergeFieldStrategyOverwrite(final Object left, final Object right) {
		return right;
	}

	/**
	 * @param left
	 * @param right
	 * @return
	 * @see {@link MergeStrategy#ocdsOmit}
	 */
	protected Object mergeFieldStrategyOcdsOmit(final Object left, final Object right) {
		return null;
	}

	/**
	 * 
	 * @param left
	 * @param right
	 * @return
	 * @see {@link MergeStrategy#ocdsVersion}
	 */
	protected Object mergeFieldStrategyOcdsVersion(final Object left, final Object right) {
		return right;
	}

	protected Identifiable getIdentifiableById(final Serializable id, final Collection<Identifiable> col) {
		for (Identifiable identifiable : col) {
			if (identifiable.getId().equals(id)) {
				return identifiable;
			}
		}
		return null;
	}

	/**
	 * @param left
	 * @param right
	 * @return
	 * @see {@link MergeStrategy#arrayMergeById}
	 */
	@SuppressWarnings("unchecked")
	protected <S extends Collection<Identifiable>> S mergeFieldStrategyArrayMergeById(final S left, final S right) {

		// target collections must be cloned
		S target = null;
		try {
			target = (S) left.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		target.addAll(left);

		// iterate all right elements
		for (Identifiable identifiable : right) {

			// if there is an existing element with the same id, perform merge
			// on the children and replace existing left element
			Identifiable leftIdentifiable = getIdentifiableById(identifiable.getId(), left);
			if (leftIdentifiable != null) {
				target.remove(leftIdentifiable);
				target.add(mergeOCDSBeans(leftIdentifiable, identifiable));
			} else {
				// otherwise add the new element to the left list
				target.add(identifiable);
			}
		}
		return target;
	}

	/**
	 * Merges the right object into a shallow copy of the left.
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <S> S mergeOCDSBeans(final S left, final S right) {

		// if there is no data to the right, the merge just returns the
		// unmutated left
		if (right == null) {
			return left;
		}

		Class<?> clazz = right.getClass();
		if (!left.getClass().equals(clazz)) {
			throw new RuntimeException("Attempted the merging of objects of different type!");
		}

		S target = null;
		try {
			target = (S) BeanUtils.cloneBean(left);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException
				| NoSuchMethodException e1) {
			throw new RuntimeException(e1);
		}

		Field[] rightDeclaredFields = right.getClass().getDeclaredFields();

		Field field = null;
		for (int i = 0; i < rightDeclaredFields.length; i++) {
			try {
				field = rightDeclaredFields[i];
				String fieldName = rightDeclaredFields[i].getName();
				Object rightFieldValue = PropertyUtils.getProperty(right, fieldName);
				Object leftFieldValue = PropertyUtils.getProperty(target, fieldName);
				if (fieldsAnnotatedWithMerge.contains(field)) {
					MergeStrategy mergeStrategy = field.getDeclaredAnnotation(Merge.class).value();
					switch (mergeStrategy) {
					case overwrite:
						PropertyUtils.setProperty(target, fieldName,
								mergeFieldStrategyOverwrite(leftFieldValue, rightFieldValue));
						break;
					case ocdsOmit:
						PropertyUtils.setProperty(target, fieldName,
								mergeFieldStrategyOcdsOmit(leftFieldValue, rightFieldValue));
						break;
					case ocdsVersion:
						PropertyUtils.setProperty(target, fieldName,
								mergeFieldStrategyOcdsVersion(leftFieldValue, rightFieldValue));
						break;
					case arrayMergeById:
						PropertyUtils.setProperty(target, fieldName, mergeFieldStrategyArrayMergeById(
								(Collection<Identifiable>) leftFieldValue, (Collection<Identifiable>) rightFieldValue));
						break;

					default:
						throw new RuntimeException("Unknown or unimplemented merge strategy!");
					}
				} else {
					PropertyUtils.setProperty(target, fieldName, mergeOCDSBeans(leftFieldValue, rightFieldValue));
				}

			} catch (Exception e) {
				logger.error(
						e.getMessage() + " while processing field " + clazz.getSimpleName() + "." + field.getName());
				throw new RuntimeException(e);
			}
		}

		return target;

	}

	protected Release createCompiledRelease(final Record record) {
		// empty records produce null compiled release
		if (record.getReleases().isEmpty()) {
			return null;
		}
		// records with just one release produce a compiled release identical to
		// the one release
		Release left = record.getReleases().get(0);
		if (record.getReleases().size() > 1) {
			// we merge each element of the list to its left partner
			List<Release> subList = record.getReleases().subList(1, record.getReleases().size());
			for (Release right : subList) {
				Release compiled = mergeOCDSBeans(left, right);
				left = compiled;
			}
		}

		// this was purposefully nullified by ocdsOmit
		left.setTag(new ArrayList<Tag>());

		left.getTag().add(Tag.compiled);

		return left;
	}

	public void createSaveCompiledReleaseAndSaveRecord(Record record) {
		Release compiledRelease = createCompiledRelease(record);
		record.setCompiledRelease(releaseRepository.save(compiledRelease));
		recordRepository.save(record);
	}

}
