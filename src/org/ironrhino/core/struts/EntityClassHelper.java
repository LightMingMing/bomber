package org.ironrhino.core.struts;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.ironrhino.core.hibernate.CreationUser;
import org.ironrhino.core.hibernate.CriterionOperator;
import org.ironrhino.core.hibernate.UpdateUser;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.FullnameSeperator;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.Attributable;
import org.ironrhino.core.model.BaseTreeableEntity;
import org.ironrhino.core.model.Persistable;
import org.ironrhino.core.search.elasticsearch.annotations.Index;
import org.ironrhino.core.search.elasticsearch.annotations.SearchableComponent;
import org.ironrhino.core.search.elasticsearch.annotations.SearchableId;
import org.ironrhino.core.search.elasticsearch.annotations.SearchableProperty;
import org.ironrhino.core.struts.AnnotationShadows.HiddenImpl;
import org.ironrhino.core.struts.AnnotationShadows.ReadonlyImpl;
import org.ironrhino.core.struts.AnnotationShadows.UiConfigImpl;
import org.ironrhino.core.util.AppInfo;
import org.ironrhino.core.util.AppInfo.Stage;
import org.ironrhino.core.util.DateUtils;
import org.ironrhino.core.util.ReflectionUtils;
import org.ironrhino.core.util.TypeUtils;
import org.ironrhino.core.util.ValueThenKeyComparator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityClassHelper {

	private static final Map<Class<?>, Map<String, UiConfigImpl>> uiConfigCache = new ConcurrentHashMap<>(64);
	private static final Map<Class<?>, Boolean> idAssignedCache = new ConcurrentHashMap<>(64);
	private static final boolean HIBERNATE_VALIDATOR_PRESENT = ClassUtils
			.isPresent("org.hibernate.validator.HibernateValidator", null);

	@SuppressWarnings("deprecation")
	public static Map<String, UiConfigImpl> getUiConfigs(Class<?> entityClass) {
		Map<String, UiConfigImpl> map = uiConfigCache.get(entityClass);
		if (map == null || AppInfo.getStage() == Stage.DEVELOPMENT) {
			synchronized (entityClass) {
				boolean idAssigned = isIdAssigned(entityClass);
				Map<String, NaturalId> naturalIds = org.ironrhino.core.util.AnnotationUtils
						.getAnnotatedPropertyNameAndAnnotations(entityClass, NaturalId.class);
				Set<String> hides = new HashSet<>();
				map = new HashMap<>();
				PropertyDescriptor[] pds = org.springframework.beans.BeanUtils.getPropertyDescriptors(entityClass);
				List<String> fields = ReflectionUtils.getAllFields(entityClass);
				for (PropertyDescriptor pd : pds) {
					String propertyName = pd.getName();
					if (pd.getReadMethod() == null || pd.getWriteMethod() == null
							&& AnnotationUtils.findAnnotation(pd.getReadMethod(), UiConfig.class) == null)
						continue;
					Method readMethod = pd.getReadMethod();
					Field declaredField;
					try {
						declaredField = readMethod.getDeclaringClass().getDeclaredField(propertyName);
					} catch (NoSuchFieldException e) {
						try {
							declaredField = ReflectionUtils.getField(readMethod.getDeclaringClass(), propertyName);
						} catch (NoSuchFieldException ex) {
							declaredField = null;
						}
					} catch (SecurityException e) {
						throw new RuntimeException(e);
					}
					if (findAnnotation(readMethod, declaredField, Version.class) != null)
						continue;
					UiConfig uiConfig = findAnnotation(readMethod, declaredField, UiConfig.class);
					if (uiConfig != null && uiConfig.hidden())
						continue;
					if ("new".equals(propertyName) || !idAssigned && "id".equals(propertyName) && uiConfig == null
							|| "class".equals(propertyName) || "fieldHandler".equals(propertyName)
							|| hides.contains(propertyName))
						continue;

					UiConfigImpl uci = new UiConfigImpl(pd.getName(), pd.getReadMethod().getGenericReturnType(),
							uiConfig);
					if ("id".equals(pd.getName()))
						uci.addCssClass("readonly-on-success");
					Class<?> collectionType = uci.getCollectionType();
					Class<?> elementType = uci.getElementType();
					if (uiConfig == null || uiConfig.displayOrder() == Integer.MAX_VALUE) {
						int index = fields.indexOf(pd.getName());
						if (index == -1)
							index = Integer.MAX_VALUE;
						uci.setDisplayOrder(index);
					}
					if (pd.getWriteMethod() == null && StringUtils.isBlank(uci.getInputTemplate())) {
						HiddenImpl hi = new HiddenImpl();
						hi.setValue(true);
						uci.setHiddenInInput(hi);
						ReadonlyImpl ri = new ReadonlyImpl();
						ri.setValue(true);
						uci.setReadonly(ri);
					}

					if (findAnnotation(readMethod, declaredField, CreationTimestamp.class) != null
							|| findAnnotation(readMethod, declaredField, UpdateTimestamp.class) != null
							|| findAnnotation(readMethod, declaredField, CreationUser.class) != null
							|| findAnnotation(readMethod, declaredField, UpdateUser.class) != null) {
						HiddenImpl hi = uci.getHiddenInInput();
						if (hi == null || hi.isDefaultOptions()) {
							hi = new HiddenImpl();
							hi.setValue(true);
							uci.setHiddenInInput(hi);
						}
						ReadonlyImpl ri = uci.getReadonly();
						if (ri == null || ri.isDefaultOptions()) {
							ri = new ReadonlyImpl();
							ri.setValue(true);
							uci.setReadonly(ri);
						}
					}

					OneToOne oneToOne = findAnnotation(readMethod, declaredField, OneToOne.class);
					if (oneToOne != null) {
						if (StringUtils.isNotBlank(oneToOne.mappedBy())) {
							uci.setInverseRelation(true);
							ReadonlyImpl ri = new ReadonlyImpl();
							ri.setValue(true);
							uci.setReadonly(ri);
						}
						MapsId mapsId = findAnnotation(readMethod, declaredField, MapsId.class);
						if (mapsId != null) {
							ReadonlyImpl ri = new ReadonlyImpl();
							ri.setExpression("!entity.new");
							uci.setReadonly(ri);
						}
					}

					OneToMany oneToMany = findAnnotation(readMethod, declaredField, OneToMany.class);
					ManyToMany manyToMany = findAnnotation(readMethod, declaredField, ManyToMany.class);
					if (oneToMany != null && StringUtils.isNotBlank(oneToMany.mappedBy())
							|| manyToMany != null && StringUtils.isNotBlank(manyToMany.mappedBy())) {
						uci.setInverseRelation(true);
						ReadonlyImpl ri = new ReadonlyImpl();
						ri.setValue(true);
						uci.setReadonly(ri);
					}

					Embedded embedded = findAnnotation(readMethod, declaredField, Embedded.class);
					EmbeddedId embeddedId = findAnnotation(readMethod, declaredField, EmbeddedId.class);
					if ((embedded != null || embeddedId != null)
							&& (uiConfig == null || !uiConfig.embeddedAsSingle())) {
						HiddenImpl hi = new HiddenImpl();
						hi.setValue(true);
						uci.setHiddenInList(hi);
						uci.setType("embedded");
						Map<String, UiConfigImpl> map2 = getUiConfigs(readMethod.getReturnType());
						for (UiConfigImpl ui : map2.values()) {
							if (StringUtils.isBlank(ui.getGroup()) && StringUtils.isNotBlank(uci.getGroup()))
								ui.setGroup(uci.getGroup());
							if (embeddedId != null) {
								ui.addCssClass("required");
								ui.addCssClass("id");
								ReadonlyImpl ri = new ReadonlyImpl();
								ri.setExpression("!entity.new");
								ui.setReadonly(ri);
							}
						}
						uci.setEmbeddedUiConfigs(map2);
					}

					if (collectionType != null && elementType != null
							&& elementType.getAnnotation(Embeddable.class) != null) {
						if (StringUtils.isBlank(uci.getListTemplate())) {
							HiddenImpl hi = new HiddenImpl();
							hi.setValue(true);
							uci.setHiddenInList(hi);
						}
						uci.setType("collection");
						uci.setEmbeddedUiConfigs(getUiConfigs(elementType));
						uci.setExcludedFromCriteria(true);
					}

					if (idAssigned && propertyName.equals("id"))
						uci.addCssClass("required checkavailable");
					if (Attributable.class.isAssignableFrom(entityClass) && pd.getName().equals("attributes")) {
						uci.setType("attributes");
					}
					if (collectionType != null) {
						uci.setExcludedFromOrdering(true);
						if (elementType == String.class
								&& (StringUtils.isBlank(uci.getType()) || "input".equals(uci.getType()))) {
							uci.addCssClass("tags");
							if (StringUtils.isBlank(uci.getTemplate()))
								uci.setTemplate(
										"<#if value?has_content><#list value as var><span class=\"label\">${var}</span><#sep> </#list></#if>");
						}
					}
					if (pd.getWriteMethod() == null)
						uci.setExcludedFromCriteria(true);
					if (findAnnotation(readMethod, declaredField, Transient.class) != null) {
						uci.setExcludedFromCriteria(true);
						uci.setExcludedFromLike(true);
						uci.setExcludedFromOrdering(true);
					}
					Lob lob = findAnnotation(readMethod, declaredField, Lob.class);
					if (lob != null) {
						uci.setExcludedFromCriteria(true);
						if (uci.getMaxlength() == 0)
							uci.setMaxlength(2 * 1024 * 1024);
					}
					if (lob != null || uci.getMaxlength() > 255)
						uci.setExcludedFromOrdering(true);

					Column column = findAnnotation(readMethod, declaredField, Column.class);
					Basic basic = findAnnotation(readMethod, declaredField, Basic.class);
					if (column != null && !column.nullable() || basic != null && !basic.optional())
						uci.setRequired(true);
					if (column != null) {
						if (column.length() != 255 && uci.getMaxlength() == 0)
							uci.setMaxlength(column.length());
						if (column.unique()) {
							uci.setUnique(true);
							uci.addCssClass("checkavailable");
						}
						if (!column.updatable() && !column.insertable()) {
							HiddenImpl hi = uci.getHiddenInInput();
							if (hi == null || hi.isDefaultOptions()) {
								hi = new HiddenImpl();
								hi.setValue(true);
								uci.setHiddenInInput(hi);
							}
							ReadonlyImpl ri = uci.getReadonly();
							if (ri == null || ri.isDefaultOptions()) {
								ri = new ReadonlyImpl();
								ri.setValue(true);
								uci.setReadonly(ri);
							}
						} else if (column.updatable() && !column.insertable()) {
							ReadonlyImpl ri = uci.getReadonly();
							if (ri == null || ri.isDefaultOptions()) {
								ri = new ReadonlyImpl();
								ri.setExpression("entity.new");
								uci.setReadonly(ri);
							}
						} else if (!column.updatable() && column.insertable()) {
							ReadonlyImpl ri = uci.getReadonly();
							if (ri == null || ri.isDefaultOptions()) {
								ri = new ReadonlyImpl();
								ri.setExpression("!entity.new");
								uci.setReadonly(ri);
							}
						}
					}
					if (findAnnotation(readMethod, declaredField, Formula.class) != null) {
						ReadonlyImpl ri = new ReadonlyImpl();
						ri.setValue(true);
						uci.setReadonly(ri);
					}

					Class<?> returnType = pd.getPropertyType();
					if (collectionType != null && elementType != null) {
						if (Persistable.class.isAssignableFrom(elementType)) {
							uci.setMultiple(true);
							if (oneToMany != null) {
								uci.setExcludedFromCriteria(true);
								uci.setInverseRelation(true);
								ReadonlyImpl rd = new ReadonlyImpl();
								rd.setValue(true);
								uci.setReadonly(rd);
							}
							uci.setTemplate(
									"<#if value?has_content><#list value as var><span class=\"label\">${var}</span><#sep> </#list></#if>");
							returnType = elementType;
							uci.setPropertyType(returnType);
						} else if (String.class == elementType || Number.class.isAssignableFrom(elementType)) {
							uci.setMultiple(true);
						}
					}
					if (returnType.isArray()) {
						Class<?> clazz = returnType.getComponentType();
						if (clazz.isEnum() || clazz == String.class) {
							uci.setMultiple(true);
							returnType = clazz;
							uci.setPropertyType(returnType);
							if (!clazz.isEnum() && !"dictionary".equals(uci.getType()))
								uci.addCssClass("tags");
						} else if (Number.class.isAssignableFrom(clazz)) {
							uci.setMultiple(true);
							uci.setPropertyType(returnType);
						}
					}
					if (collectionType != null && elementType != null && (elementType.isEnum()
							|| elementType == String.class && uci.getType().equals("dictionary"))) {
						uci.setMultiple(true);
						returnType = elementType;
						uci.setPropertyType(returnType);
					}
					if (uci.isMultiple() && StringUtils.isBlank(uci.getTemplate())) {
						if (uci.getType().equals("dictionary")) {
							uci.setTemplate(
									"<#if value?has_content><#list value as var><span class=\"label\"><#if displayDictionaryLabel??><@displayDictionaryLabel dictionaryName='"
											+ uci.getTemplateName()
											+ "' value=var!/><#else>${(var?string)!}</#if></span><#sep> </#list></#if>");
						} else {
							uci.setTemplate(
									"<#if value?has_content><#list value as var><span class=\"label\">${(var?string)!}</span><#sep> </#list></#if>");
						}
					}

					if (returnType.isEnum()) {
						uci.setType("enum");
						try {
							returnType.getMethod("getName");
							uci.setListKey("name");
						} catch (NoSuchMethodException e) {
							uci.setListKey("top");
						}
						try {
							returnType.getMethod("getDisplayName");
							uci.setListValue("displayName");
						} catch (NoSuchMethodException e) {
							uci.setListValue(uci.getListKey());
						}
					} else if (Persistable.class.isAssignableFrom(returnType)) {
						JoinColumn joinColumn = findAnnotation(readMethod, declaredField, JoinColumn.class);
						if (joinColumn != null && !joinColumn.nullable())
							uci.setRequired(true);
						if (joinColumn != null) {
							if (!joinColumn.updatable() && !joinColumn.insertable()) {
								HiddenImpl hi = uci.getHiddenInInput();
								if (hi == null || hi.isDefaultOptions()) {
									hi = new HiddenImpl();
									hi.setValue(true);
									uci.setHiddenInInput(hi);
								}
								ReadonlyImpl ri = uci.getReadonly();
								if (ri == null || ri.isDefaultOptions()) {
									ri = new ReadonlyImpl();
									ri.setValue(true);
									uci.setReadonly(ri);
								}
							} else if (joinColumn.updatable() && !joinColumn.insertable()) {
								ReadonlyImpl ri = uci.getReadonly();
								if (ri == null || ri.isDefaultOptions()) {
									ri = new ReadonlyImpl();
									ri.setExpression("entity.new");
									uci.setReadonly(ri);
								}
							} else if (!joinColumn.updatable() && joinColumn.insertable()) {
								ReadonlyImpl ri = uci.getReadonly();
								if (ri == null || ri.isDefaultOptions()) {
									ri = new ReadonlyImpl();
									ri.setExpression("!entity.new");
									uci.setReadonly(ri);
								}
							}
						}
						ManyToOne manyToOne = findAnnotation(readMethod, declaredField, ManyToOne.class);
						if (manyToOne != null) {
							if (!manyToOne.optional())
								uci.setRequired(true);
							if (joinColumn != null) {
								uci.setReferencedColumnName(joinColumn.referencedColumnName());
							}
						}
						if (BaseTreeableEntity.class.isAssignableFrom(returnType)
								&& uci.getType().equals(UiConfig.DEFAULT_TYPE)) {
							if (uci.getType().equals(UiConfig.DEFAULT_TYPE))
								uci.setType("treeselect");
							if (StringUtils.isBlank(uci.getPickUrl())) {
								String url = AutoConfigPackageProvider.getEntityUrl(returnType);
								StringBuilder sb = url != null
										? new StringBuilder(url)
										: new StringBuilder("/")
												.append(StringUtils.uncapitalize(entityClass.getSimpleName()));
								sb.append("/children");
								uci.setPickUrl(sb.toString());
							}
							if (!uci.getInternalDynamicAttributes().containsKey("data-separator")) {
								try {
									BaseTreeableEntity<?> te = (BaseTreeableEntity<?>) BeanUtils
											.instantiateClass(returnType);
									String separator = te.getFullnameSeperator();
									if (StringUtils.isNotBlank(separator))
										uci.getInternalDynamicAttributes().put("data-separator", separator);
								} catch (Exception e) {
								}
							}
						} else {
							if (uci.getType().equals(UiConfig.DEFAULT_TYPE))
								uci.setType("listpick");
							if (StringUtils.isBlank(uci.getPickUrl()))
								uci.setPickUrl(getPickUrl(returnType));
						}
						if (!uci.isMultiple() && StringUtils.isBlank(uci.getListTemplate())
								&& !uci.isSuppressViewLink()) {
							String url = AutoConfigPackageProvider.getEntityUrl(returnType);
							if (url == null)
								url = new StringBuilder("/")
										.append(StringUtils.uncapitalize(returnType.getSimpleName())).toString();
							uci.setListTemplate("<#if (value.id)?has_content><a href=\"<@url value='" + url
									+ "/view/${value.id}'/>\" class=\"view\" rel=\"richtable\" title=\"${action.getText('view')}\">${value?html}</a></#if>");
						}
					} else if (collectionType == null && TypeUtils.isNumeric(returnType)) {
						if (TypeUtils.isIntegralNumeric(returnType)) {
							uci.setInputType("number");
							uci.addCssClass((returnType == Long.TYPE || returnType == Long.class) ? "long" : "integer");
						} else if (TypeUtils.isDecimalNumeric(returnType)) {
							uci.setInputType("number");
							uci.addCssClass("double");
							int scale = column != null ? column.scale() : 2;
							if (scale == 0)
								scale = 2;
							StringBuilder step = new StringBuilder(scale + 2);
							step.append("0.");
							for (int i = 0; i < scale - 1; i++)
								step.append("0");
							step.append("1");
							uci.getInternalDynamicAttributes().put("step", step.toString());
							uci.getInternalDynamicAttributes().put("data-scale", String.valueOf(scale));
							if (StringUtils.isBlank(uci.getTemplate())) {
								StringBuilder template = new StringBuilder(scale + 40);
								template.append("<#if value?is_number>${value?string('");
								if (returnType == BigDecimal.class)
									template.append("#,##");
								template.append("0.");
								for (int i = 0; i < scale; i++)
									template.append("0");
								template.append("')}<#else>${value!}</#if>");
								uci.setTemplate(template.toString());
							}
						}
						Positive positive = findAnnotation(readMethod, declaredField, Positive.class);
						if (positive != null) {
							uci.addCssClass("positive");
						} else {
							PositiveOrZero poz = findAnnotation(readMethod, declaredField, PositiveOrZero.class);
							if (poz != null) {
								uci.addCssClass("positive");
								uci.addCssClass("zero");
							}
						}
						Set<String> cssClasses = uci.getCssClasses();
						if (cssClasses.contains("double") && !uci.getInternalDynamicAttributes().containsKey("step"))
							uci.getInternalDynamicAttributes().put("step", "0.01");
						if (cssClasses.contains("positive") && !uci.getInternalDynamicAttributes().containsKey("min")) {
							uci.getInternalDynamicAttributes().put("min", "1");
							if (cssClasses.contains("double"))
								uci.getInternalDynamicAttributes().put("min", "0.01");
							if (cssClasses.contains("zero"))
								uci.getInternalDynamicAttributes().put("min", "0");
						}
						Min min = findAnnotation(readMethod, declaredField, Min.class);
						if (min != null)
							uci.getInternalDynamicAttributes().put("min", String.valueOf(min.value()));
						Max max = findAnnotation(readMethod, declaredField, Max.class);
						if (max != null)
							uci.getInternalDynamicAttributes().put("max", String.valueOf(max.value()));
						DecimalMin decimalMin = findAnnotation(readMethod, declaredField, DecimalMin.class);
						if (decimalMin != null)
							uci.getInternalDynamicAttributes().put("min", decimalMin.value());
						DecimalMax decimalMax = findAnnotation(readMethod, declaredField, DecimalMax.class);
						if (decimalMax != null)
							uci.getInternalDynamicAttributes().put("max", decimalMax.value());
						if (HIBERNATE_VALIDATOR_PRESENT) {
							org.hibernate.validator.constraints.Range range = findAnnotation(readMethod, declaredField,
									org.hibernate.validator.constraints.Range.class);
							if (range != null) {
								if (range.min() != 0)
									uci.getInternalDynamicAttributes().put("min", String.valueOf(range.min()));
								if (range.max() != Integer.MAX_VALUE)
									uci.getInternalDynamicAttributes().put("max", String.valueOf(range.max()));
							}
						}
					} else if (Date.class.isAssignableFrom(returnType)
							|| java.time.temporal.Temporal.class.isAssignableFrom(returnType)) {
						String temporalType = "date";
						if (Date.class.isAssignableFrom(returnType)) {
							Temporal temporal = findAnnotation(readMethod, declaredField, Temporal.class);
							if (temporal != null)
								if (temporal.value() == TemporalType.TIMESTAMP)
									temporalType = "datetime";
								else if (temporal.value() == TemporalType.TIME)
									temporalType = "time";
						} else {
							temporalType = returnType == Duration.class
									? "duration"
									: returnType == LocalTime.class
											? "time"
											: (returnType == LocalDateTime.class || returnType == ZonedDateTime.class
													|| returnType == OffsetDateTime.class)
															? "datetime"
															: returnType == YearMonth.class ? "yearmonth" : "date";
						}
						uci.addCssClass(temporalType);
						// uci.setInputType(temporalType);
						if (StringUtils.isBlank(uci.getCellEdit()))
							uci.setCellEdit("click," + temporalType);
						if (!"time".equals(temporalType) && !"duration".equals(temporalType)) {
							if (!uci.getInternalDynamicAttributes().containsKey("data-enddate")) {
								if (findAnnotation(readMethod, declaredField, Past.class) != null) {
									uci.getInternalDynamicAttributes().put("data-enddate",
											DateUtils.formatDate10("datetime".equals(temporalType)
													? new Date()
													: DateUtils.addDays(new Date(), -1)));
								} else if (findAnnotation(readMethod, declaredField, PastOrPresent.class) != null) {
									uci.getInternalDynamicAttributes().put("data-enddate",
											DateUtils.formatDate10(new Date()));
								}
							}
							if (!uci.getInternalDynamicAttributes().containsKey("data-startdate")) {
								if (findAnnotation(readMethod, declaredField, Future.class) != null) {
									uci.getInternalDynamicAttributes().put("data-startdate",
											DateUtils.formatDate10("datetime".equals(temporalType)
													? new Date()
													: DateUtils.addDays(new Date(), 1)));
								} else if (findAnnotation(readMethod, declaredField, FutureOrPresent.class) != null) {
									uci.getInternalDynamicAttributes().put("data-startdate",
											DateUtils.formatDate10(new Date()));
								}
							}
						}
					} else if (String.class == returnType) {
						Size size = findAnnotation(readMethod, declaredField, Size.class);
						if (size != null) {
							if (size.min() != 0)
								uci.getInternalDynamicAttributes().put("minlength", String.valueOf(size.min()));
							if (size.max() != Integer.MAX_VALUE)
								uci.getInternalDynamicAttributes().put("maxlength", String.valueOf(size.max()));
						}
						if (HIBERNATE_VALIDATOR_PRESENT) {
							org.hibernate.validator.constraints.Length length = findAnnotation(readMethod,
									declaredField, org.hibernate.validator.constraints.Length.class);
							if (length != null) {
								if (length.min() != 0)
									uci.getInternalDynamicAttributes().put("minlength", String.valueOf(length.min()));
								if (length.max() != Integer.MAX_VALUE)
									uci.getInternalDynamicAttributes().put("maxlength", String.valueOf(length.max()));
							}
						}
						Pattern pattern = findAnnotation(readMethod, declaredField, Pattern.class);
						if (pattern != null) {
							uci.addCssClass("regex");
							uci.getInternalDynamicAttributes().put("data-regex", pattern.regexp());
						}
						if (findAnnotation(readMethod, declaredField, Email.class) != null
								|| pd.getName().toLowerCase(Locale.ROOT).contains("email")
										&& !pd.getName().contains("Password")) {
							uci.setInputType("email");
							uci.addCssClass("email");
						}
					} else if (returnType == Boolean.TYPE) {
						uci.setType("checkbox");
						uci.addCssClass("switch");
					} else if (returnType == Boolean.class) {
						uci.setType("select");
						uci.setListOptions(
								"{'true':'" + I18N.getText("true") + "','false':'" + I18N.getText("false") + "'}");
					} else if (returnType == File.class) {
						uci.setInputType("file");
						uci.addCssClass("custom");
						AutoConfig ac = entityClass.getAnnotation(AutoConfig.class);
						if (ac != null) {
							String fu = ac.fileupload();
							if (fu.indexOf('/') > 0)
								uci.getInternalDynamicAttributes().put("accept", fu);
						}
					}

					if (findAnnotation(readMethod, declaredField, NotNull.class) != null)
						uci.setRequired(true);
					if (findAnnotation(readMethod, declaredField, NotEmpty.class) != null
							|| findAnnotation(readMethod, declaredField, NotBlank.class) != null
							|| HIBERNATE_VALIDATOR_PRESENT && (findAnnotation(readMethod, declaredField,
									org.hibernate.validator.constraints.NotEmpty.class) != null
									|| findAnnotation(readMethod, declaredField,
											org.hibernate.validator.constraints.NotBlank.class) != null))
						uci.setRequired(true);

					SearchableProperty searchableProperty = findAnnotation(readMethod, declaredField,
							SearchableProperty.class);
					SearchableId searchableId = findAnnotation(readMethod, declaredField, SearchableId.class);
					SearchableComponent searchableComponent = findAnnotation(readMethod, declaredField,
							SearchableComponent.class);
					if (searchableProperty != null || searchableId != null || searchableComponent != null) {
						uci.setSearchable(true);
						if (searchableId != null
								|| searchableProperty != null && searchableProperty.index() == Index.NOT_ANALYZED)
							uci.setExactMatch(true);
						if (searchableComponent != null) {
							String s = searchableComponent.nestSearchableProperties();
							if (StringUtils.isNotBlank(s)) {
								Set<String> nestSearchableProperties = new LinkedHashSet<>();
								nestSearchableProperties.addAll(Arrays.asList(s.split("\\s*,\\s*")));
								uci.setNestSearchableProperties(nestSearchableProperties);
							}
						}
					}
					if (naturalIds.containsKey(pd.getName())) {
						uci.setRequired(true);
						// if (naturalIds.size() == 1)
						uci.addCssClass("checkavailable");
						if (!naturalIds.get(pd.getName()).mutable())
							uci.addCssClass("readonly-on-success");
						if (naturalIds.size() > 1) {
							if (Persistable.class.isAssignableFrom(uci.getPropertyType())) {
								List<String> list = new ArrayList<>(naturalIds.size() - 1);
								for (String name : naturalIds.keySet())
									if (!name.equals(pd.getName()))
										list.add(StringUtils.uncapitalize(entityClass.getSimpleName()) + '.' + name);
								uci.getInternalDynamicAttributes().put("data-checkwith", String.join(",", list));
							}
						}
					}
					if (uci.getType().equals("textarea") && uci.getMaxlength() > 0)
						uci.getInternalDynamicAttributes().put("maxlength", String.valueOf(uci.getMaxlength()));
					if (StringUtils.isNotBlank(uci.getGroup())) {
						uci.getInternalDynamicAttributes().put("_internal_group", I18N.getText(uci.getGroup()));
					}
					if (StringUtils.isNotBlank(uci.getDescription())) {
						uci.getInternalDynamicAttributes().put("_internal_description",
								I18N.getText(uci.getDescription()));
					}
					map.put(propertyName, uci);
				}
				List<Map.Entry<String, UiConfigImpl>> list = new ArrayList<>(map.entrySet());
				list.sort(comparator);
				Map<String, UiConfigImpl> sortedMap = new LinkedHashMap<>();
				for (Map.Entry<String, UiConfigImpl> entry : list)
					sortedMap.put(entry.getKey(), entry.getValue());
				map = sortedMap;
				uiConfigCache.put(entityClass, Collections.unmodifiableMap(map));
			}
		}
		return map;
	}

	private static UiConfigImpl clone(UiConfigImpl old) {
		UiConfigImpl config = new UiConfigImpl();
		BeanUtils.copyProperties(old, config);
		Set<String> cssClasses = new LinkedHashSet<>();
		if (config.getCssClasses().contains("date")) {
			cssClasses.add("date");
		} else if (config.getCssClasses().contains("datetime")) {
			cssClasses.add("datetime");
		} else if (config.getCssClasses().contains("time")) {
			cssClasses.add("time");
		} else if (config.getCssClasses().contains("yearmonth")) {
			cssClasses.add("yearmonth");
		} else if (config.getCssClasses().contains("integer")) {
			cssClasses.add("integer");
		} else if (config.getCssClasses().contains("long")) {
			cssClasses.add("long");
		} else if (config.getCssClasses().contains("double")) {
			cssClasses.add("double");
		}
		config.setCssClasses(cssClasses);
		if ("email".equals(config.getInputType()))
			config.setInputType("text");
		return config;
	}

	public static Map<String, UiConfigImpl> filterPropertyNamesInCriteria(Map<String, UiConfigImpl> uiConfigs) {
		Map<String, UiConfigImpl> propertyNamesInCriterion = new LinkedHashMap<>();
		uiConfigs.forEach((key, config) -> {
			if (!config.isExcludedFromCriteria()) {
				if ("embedded".equals(config.getType())) {
					for (Map.Entry<String, UiConfigImpl> entry2 : config.getEmbeddedUiConfigs().entrySet()) {
						UiConfigImpl config2 = entry2.getValue();
						if (!config2.isExcludedFromCriteria() && !CriterionOperator
								.getSupportedOperators(config2.getGenericPropertyType()).isEmpty()) {
							propertyNamesInCriterion.put(key + '.' + entry2.getKey(), clone(config2));
						}
					}
				} else if (!CriterionOperator.getSupportedOperators(config.getGenericPropertyType()).isEmpty()) {
					propertyNamesInCriterion.put(key, clone(config));
				}
			}
		});
		return propertyNamesInCriterion;
	}

	public static Map<String, UiConfigImpl> getPropertyNamesInCriteria(Class<? extends Persistable<?>> entityClass) {
		return filterPropertyNamesInCriteria(getUiConfigs(entityClass));
	}

	public static String getPickUrl(Class<?> entityClass) {
		String url = AutoConfigPackageProvider.getEntityUrl(entityClass);
		StringBuilder sb = url != null
				? new StringBuilder(url)
				: new StringBuilder("/").append(StringUtils.uncapitalize(entityClass.getSimpleName()));
		sb.append("/pick");
		Set<String> columns = new LinkedHashSet<>();
		BeanWrapperImpl bw = new BeanWrapperImpl(entityClass);
		if (BaseTreeableEntity.class.isAssignableFrom(entityClass)) {
			FullnameSeperator fs = entityClass.getAnnotation(FullnameSeperator.class);
			if (fs != null && !fs.independent() && bw.isReadableProperty("fullname"))
				columns.add("fullname");
			else
				columns.add("name");
		} else {
			if (bw.isReadableProperty("name"))
				columns.add("name");
			if (bw.isReadableProperty("fullname"))
				columns.add("fullname");
		}
		columns.addAll(org.ironrhino.core.util.AnnotationUtils
				.getAnnotatedPropertyNameAndAnnotations(entityClass, NaturalId.class).keySet());
		for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
			if (pd.getReadMethod() == null)
				continue;
			UiConfig uic = AnnotationUtils.findAnnotation(pd.getReadMethod(), UiConfig.class);
			if (uic == null) {
				try {
					Field f = pd.getReadMethod().getDeclaringClass().getDeclaredField(pd.getName());
					if (f != null)
						uic = f.getAnnotation(UiConfig.class);
				} catch (Exception e) {

				}
			}
			if (uic != null && uic.shownInPick())
				columns.add(pd.getName());
		}
		if (!columns.isEmpty()) {
			sb.append("?columns=" + String.join(",", columns));
		}
		return sb.toString();
	}

	public static boolean isIdAssigned(Class<?> entityClass) {
		Boolean b = idAssignedCache.get(entityClass);
		if (b == null || AppInfo.getStage() == Stage.DEVELOPMENT) {
			b = _isIdAssigned(entityClass);
			idAssignedCache.put(entityClass, b);
		}
		return b;
	}

	private static boolean _isIdAssigned(Class<?> entityClass) {
		if (entityClass.isInterface())
			return false;
		Map<String, MapsId> map = org.ironrhino.core.util.AnnotationUtils
				.getAnnotatedPropertyNameAndAnnotations(entityClass, MapsId.class);
		if (!map.isEmpty())
			return false;
		AnnotatedElement ae = null;
		try {
			Method m = entityClass.getMethod("getId");
			if (AnnotationUtils.findAnnotation(m, Id.class) != null
					|| AnnotationUtils.findAnnotation(m, EmbeddedId.class) != null) {
				ae = m;
			} else {
				Class<?> clz = entityClass;
				loop : while (clz != Object.class) {
					Field[] fields = clz.getDeclaredFields();
					for (Field f : fields) {
						if (f.getAnnotation(Id.class) != null || f.getAnnotation(EmbeddedId.class) != null) {
							ae = f;
							break loop;
						}
					}
					clz = clz.getSuperclass();
				}
			}
		} catch (Exception e) {
			return false;
		}
		if (ae == null)
			return false;
		GeneratedValue generatedValue = AnnotationUtils.findAnnotation(ae, GeneratedValue.class);
		GenericGenerator genericGenerator = AnnotationUtils.findAnnotation(ae, GenericGenerator.class);
		return generatedValue == null || genericGenerator != null && "assigned".equals(genericGenerator.strategy());
	}

	private static <T extends Annotation> T findAnnotation(Method readMethod, Field declaredField,
			Class<T> annotationClass) {
		T annotation = AnnotationUtils.findAnnotation(readMethod, annotationClass);
		if (annotation == null && declaredField != null)
			annotation = declaredField.getAnnotation(annotationClass);
		return annotation;
	}

	private static final ValueThenKeyComparator<String, UiConfigImpl> comparator = new ValueThenKeyComparator<String, UiConfigImpl>() {
		@Override
		protected int compareValue(UiConfigImpl a, UiConfigImpl b) {
			return a.getDisplayOrder() - b.getDisplayOrder();
		}
	};

}
